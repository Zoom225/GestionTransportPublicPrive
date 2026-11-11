package com.gestion.GestionTransport.sitetransport;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:site_transport_tests;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class SiteTransportJpaTests {
    @PersistenceContext EntityManager entityManager;
    @Autowired PlatformTransactionManager transactionManager;
    TransactionTemplate transaction;

    @BeforeEach
    void preparerBaseMemoireDediee() {
        transaction = new TransactionTemplate(transactionManager);
        transaction.executeWithoutResult(etat ->
                entityManager.createQuery("delete from SiteTransport").executeUpdate());
    }

    @Test
    void conserverIdentifiantEtNomApresRechargement() {
        var ville = new SiteTransport(" Bruxelles ");
        UUID identifiant = ville.getId();
        transaction.executeWithoutResult(etat -> entityManager.persist(ville));
        transaction.executeWithoutResult(etat -> {
            var relue = entityManager.find(SiteTransport.class, identifiant);
            assertThat(relue.getId()).isEqualTo(identifiant);
            assertThat(relue.getNomVille()).isEqualTo("bruxelles");
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"Namur", " NAMUR ", "\u00a0namur\u00a0", "ＮＡＭＵＲ"})
    void refuserEnBaseDeuxIdentitesPourLaMemeVille(String variante) {
        transaction.executeWithoutResult(etat -> entityManager.persist(new SiteTransport("Namur")));
        assertThatThrownBy(() -> transaction.executeWithoutResult(etat -> {
            entityManager.persist(new SiteTransport(variante));
            entityManager.flush();
        })).hasRootCauseInstanceOf(java.sql.SQLIntegrityConstraintViolationException.class)
            .satisfies(exception -> assertThat(exception.getMessage()).containsIgnoringCase("uk_site_transport_nom_ville"));
        transaction.executeWithoutResult(etat -> assertThat(
                entityManager.createQuery("select count(s) from SiteTransport s", Long.class)
                        .getSingleResult()).isEqualTo(1L));
    }

    @Test
    void accepterLesQuatreVillesDistinctesSansInitialiseur() {
        transaction.executeWithoutResult(etat -> {
            for (String ville : new String[]{"Bruxelles", "Namur", "Mons", "Charleroi"}) {
                entityManager.persist(new SiteTransport(ville));
            }
        });
        transaction.executeWithoutResult(etat -> assertThat(
                entityManager.createQuery("select count(s) from SiteTransport s", Long.class)
                        .getSingleResult()).isEqualTo(4L));
    }
}