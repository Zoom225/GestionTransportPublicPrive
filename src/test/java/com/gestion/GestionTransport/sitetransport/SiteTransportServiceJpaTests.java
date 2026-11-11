package com.gestion.GestionTransport.sitetransport;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(SiteTransportService.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class SiteTransportServiceJpaTests {
    @Autowired SiteTransportService service;
    @Autowired SiteTransportRepository repository;

    @BeforeEach
    void viderUniquementLaBaseMemoireDeTest() { repository.deleteAllInBatch(); }

    @Test
    void creerPuisRechercherEtListerApresValidationDesTransactions() {
        var site = service.creer(" Bruxelles ");
        assertThat(service.rechercher(site.getId()).getNomVille()).isEqualTo("bruxelles");
        assertThat(service.lister()).extracting(SiteTransport::getId).containsExactly(site.getId());
    }

    @Test
    void conserverLaVilleExistanteApresUnRefusEtPermettreUneNouvelleCreation() {
        var premier = service.creer("Namur");
        assertThatThrownBy(() -> service.creer(" NAMUR ")).isInstanceOf(SiteTransportException.class);
        var autre = service.creer("Mons");
        assertThat(service.lister()).extracting(SiteTransport::getId)
                .containsExactlyInAnyOrder(premier.getId(), autre.getId());
    }

    @Test
    void deuxCreationsConcurrentesNeProduisentQuUneVille() throws Exception {
        var prets = new CountDownLatch(2);
        var depart = new CountDownLatch(1);
        var executeur = Executors.newFixedThreadPool(2);
        try {
            var premier = executeur.submit(() -> creerAuSignal("Charleroi", prets, depart));
            var second = executeur.submit(() -> creerAuSignal(" CHARLEROI ", prets, depart));
            assertThat(prets.await(10, TimeUnit.SECONDS)).isTrue();
            depart.countDown();
            List<Object> resultats = List.of(premier.get(15, TimeUnit.SECONDS), second.get(15, TimeUnit.SECONDS));
            assertThat(resultats.stream().filter(SiteTransport.class::isInstance).count()).isEqualTo(1L);
            assertThat(resultats.stream().filter(SiteTransportException.class::isInstance).count()).isEqualTo(1L);
            var conflit = (SiteTransportException) resultats.stream()
                    .filter(SiteTransportException.class::isInstance).findFirst().orElseThrow();
            assertThat(conflit.getCode()).isEqualTo(SiteTransportException.Code.SITE_TRANSPORT_DEJA_EXISTANT);
            assertThat(repository.count()).isEqualTo(1L);
            assertThat(service.lister()).extracting(SiteTransport::getNomVille).containsExactly("charleroi");
            assertThat(service.creer("Mons").getNomVille()).isEqualTo("mons");
        } finally {
            depart.countDown();
            executeur.shutdownNow();
            assertThat(executeur.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
        }
    }

    private Object creerAuSignal(String nom, CountDownLatch prets, CountDownLatch depart) throws InterruptedException {
        prets.countDown();
        if (!depart.await(10, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Signal de démarrage du test absent.");
        }
        try {
            return service.creer(nom);
        } catch (SiteTransportException exception) {
            return exception;
        }
    }
}
