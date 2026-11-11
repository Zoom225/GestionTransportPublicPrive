package com.gestion.GestionTransport.sitetransport;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SiteTransportServiceTests {
    @Mock SiteTransportRepository repository;
    SiteTransportService service;
    @BeforeEach void preparer() { service = new SiteTransportService(repository); }

    @Test void listerLesSitesExistants() {
        var sites = List.of(new SiteTransport("Bruxelles"));
        when(repository.findAll()).thenReturn(sites);
        assertThat(service.lister()).isEqualTo(sites);
    }

    @Test void listerUneBaseVide() {
        when(repository.findAll()).thenReturn(List.of());
        assertThat(service.lister()).isEmpty();
    }

    @Test void rechercherParIdentifiant() {
        var site = new SiteTransport("Mons");
        when(repository.findById(site.getId())).thenReturn(Optional.of(site));
        assertThat(service.rechercher(site.getId())).isSameAs(site);
    }

    @Test void signalerUnSiteAbsent() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.rechercher(id)).isInstanceOfSatisfying(
                SiteTransportException.class, erreur -> assertThat(erreur.getCode())
                        .isEqualTo(SiteTransportException.Code.SITE_TRANSPORT_INTROUVABLE));
    }

    @Test void refuserUnIdentifiantAbsent() {
        assertThatThrownBy(() -> service.rechercher(null)).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(repository);
    }

    @Test void creerAvecLaNormalisationDuModele() {
        when(repository.findByNomVille("namur")).thenReturn(Optional.empty());
        when(repository.saveAndFlush(any(SiteTransport.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var site = service.creer("\u00a0 NAMUR \u00a0");
        assertThat(site.getNomVille()).isEqualTo("namur");
        assertThat(site.getId()).isNotNull();
        verify(repository).findByNomVille(site.getNomVille());
        verify(repository).saveAndFlush(site);
    }

    @ParameterizedTest
    @NullSource @EmptySource
    @ValueSource(strings = {"  ", "\u00a0", "\t\n"})
    void refuserUnNomInvalideSansAppelAuRepository(String nom) {
        assertThatThrownBy(() -> service.creer(nom)).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(repository);
    }

    @Test void refuserUnNomTropLong() {
        assertThatThrownBy(() -> service.creer("a".repeat(121))).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(repository);
    }

    @Test void refuserUneVilleDejaPresente() {
        when(repository.findByNomVille("namur")).thenReturn(Optional.of(new SiteTransport("Namur")));
        assertThatThrownBy(() -> service.creer(" NAMUR ")).isInstanceOfSatisfying(
                SiteTransportException.class, erreur -> assertThat(erreur.getCode())
                        .isEqualTo(SiteTransportException.Code.SITE_TRANSPORT_DEJA_EXISTANT));
        verify(repository, never()).saveAndFlush(any());
    }

    @Test void traduireLeConflitSqlApresUnControleNegatif() {
        when(repository.findByNomVille("namur")).thenReturn(Optional.empty());
        var conflit = conflit("uk_site_transport_nom_ville", "23505");
        when(repository.saveAndFlush(any())).thenThrow(conflit);
        assertThatThrownBy(() -> service.creer("Namur")).isInstanceOfSatisfying(
                SiteTransportException.class, erreur -> {
                    assertThat(erreur.getCode()).isEqualTo(SiteTransportException.Code.SITE_TRANSPORT_DEJA_EXISTANT);
                    assertThat(erreur.getCause()).isSameAs(conflit);
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"pk_site_transport", "autre_contrainte"})
    void nePasMasquerUneAutreViolationDUnique(String contrainte) {
        when(repository.findByNomVille("mons")).thenReturn(Optional.empty());
        var erreur = conflit(contrainte, "23505");
        when(repository.saveAndFlush(any())).thenThrow(erreur);
        assertThatThrownBy(() -> service.creer("Mons")).isSameAs(erreur);
    }

    @Test void nePasMasquerUneAutreErreurSql() {
        when(repository.findByNomVille("mons")).thenReturn(Optional.empty());
        var erreur = conflit("uk_site_transport_nom_ville", "23502");
        when(repository.saveAndFlush(any())).thenThrow(erreur);
        assertThatThrownBy(() -> service.creer("Mons")).isSameAs(erreur);
    }

    @Test void nePasMasquerUneErreurDIntegriteNonIdentifiee() {
        when(repository.findByNomVille("mons")).thenReturn(Optional.empty());
        var erreur = new DataIntegrityViolationException("autre intégrité");
        when(repository.saveAndFlush(any())).thenThrow(erreur);
        assertThatThrownBy(() -> service.creer("Mons")).isSameAs(erreur);
    }

    private DataIntegrityViolationException conflit(String contrainte, String etatSql) {
        return new DataIntegrityViolationException("conflit SQL", new ConstraintViolationException(
                "intégrité", new SQLException("conflit", etatSql), contrainte));
    }
}