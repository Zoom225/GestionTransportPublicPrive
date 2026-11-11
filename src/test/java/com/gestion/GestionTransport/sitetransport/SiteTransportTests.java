package com.gestion.GestionTransport.sitetransport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SiteTransportTests {
    @ParameterizedTest
    @CsvSource({"Bruxelles,bruxelles", "NAMUR,namur", "Mons,mons", "Charleroi,charleroi",
            "ＮＡＭＵＲ,namur", "Évreux,évreux"})
    void normaliserLesVariantesDeVille(String entree, String attendu) {
        assertThat(new SiteTransport(entree).getNomVille()).isEqualTo(attendu);
    }

    @Test
    void reduireLesEspacesUnicodeSansRetirerLesAccents() {
        assertThat(new SiteTransport("\u00a0  La\t\n Louvière \u00a0").getNomVille())
                .isEqualTo("la louvière");
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"   ", "\t\n", "\u00a0\u2003"})
    void refuserUnNomAbsentOuVide(String nom) {
        assertThatThrownBy(() -> new SiteTransport(nom)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Le nom de ville est obligatoire.");
    }

    @Test
    void limiterLaLongueurApresNormalisation() {
        assertThat(new SiteTransport("a".repeat(120)).getNomVille()).hasSize(120);
        assertThatThrownBy(() -> new SiteTransport("a".repeat(121)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void donnerUneIdentiteStableDistincteDuNom() {
        var premier = new SiteTransport("Namur");
        var identifiant = premier.getId();
        assertThat(identifiant).isNotNull();
        assertThat(premier.getId()).isEqualTo(identifiant);
        assertThat(new SiteTransport("NAMUR").getId()).isNotEqualTo(identifiant);
    }
}