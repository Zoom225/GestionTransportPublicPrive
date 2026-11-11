package com.gestion.GestionTransport.sitetransport;

import jakarta.persistence.EntityManager;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.UUID;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class SiteTransportRepositoryTests {
    @Autowired SiteTransportRepository repository;
    @Autowired EntityManager entityManager;
    @Autowired DataSource dataSource;

    @Test
    void utiliserUneBaseH2EnMemoire() throws Exception {
        try (var connexion = dataSource.getConnection()) {
            assertThat(connexion.getMetaData().getDatabaseProductName()).isEqualTo("H2");
            assertThat(connexion.getMetaData().getURL()).startsWith("jdbc:h2:mem:");
        }
    }

    @Test
    void sauvegarderPuisRelireAvecIdentifiantStable() {
        var ville = new SiteTransport(" Bruxelles ");
        var sauvegardee = repository.saveAndFlush(ville);
        assertThat(sauvegardee.getId()).isEqualTo(ville.getId());
        entityManager.clear();

        assertThat(repository.findById(ville.getId())).hasValueSatisfying(relue -> {
            assertThat(relue.getId()).isEqualTo(ville.getId());
            assertThat(relue.getNomVille()).isEqualTo("bruxelles");
        });
        assertThat(repository.existsById(ville.getId())).isTrue();
    }

    @Test
    void retrouverParNomCanoniqueEtDetecterUneVilleExistante() {
        var ville = new SiteTransport("\u00a0 NAMUR \u00a0");
        repository.saveAndFlush(ville);
        entityManager.clear();

        assertThat(repository.findByNomVille(ville.getNomVille())).hasValueSatisfying(relue ->
                assertThat(relue.getId()).isEqualTo(ville.getId()));
    }

    @Test
    void retournerUnResultatAbsentSansCreerDeVille() {
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
        assertThat(repository.findByNomVille("ville absente")).isEmpty();
        assertThat(repository.count()).isZero();
    }

    @Test
    void nePasNormaliserUneSecondeFoisDansLeRepository() {
        repository.saveAndFlush(new SiteTransport("Namur"));
        entityManager.clear();
        assertThat(repository.findByNomVille(" NAMUR ")).isEmpty();
        assertThat(repository.findByNomVille("namur")).isPresent();
    }

    @Test
    void traduireLeRefusSqlDUnDoublonEnExceptionSpring() {
        var premiere = new SiteTransport("Mons");
        var doublon = new SiteTransport(" MONS ");
        assertThat(doublon.getId()).isNotEqualTo(premiere.getId());
        repository.saveAndFlush(premiere);
        entityManager.clear();

        assertThatThrownBy(() -> repository.saveAndFlush(doublon))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasRootCauseInstanceOf(SQLIntegrityConstraintViolationException.class)
                .satisfies(exception -> assertThat(exception.getMessage())
                        .containsIgnoringCase("uk_site_transport_nom_ville"));
    }
}