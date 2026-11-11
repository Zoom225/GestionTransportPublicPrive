package com.gestion.GestionTransport.sitetransport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SiteTransportDtoTests {
    @Test
    void requeteConserveLeNomFourniSansLeNormaliser() {
        var requete = new CreationSiteTransportRequest("  Bruxelles  ");
        assertEquals("  Bruxelles  ", requete.nomVille());
    }

    @Test
    void reponseExposeIdentifiantEtNomCanoniqueDuModele() {
        var site = new SiteTransport("  BRUXELLES ");
        var reponse = SiteTransportResponse.depuis(site);
        assertNotNull(reponse.id());
        assertEquals(site.getId(), reponse.id());
        assertEquals("bruxelles", reponse.nomVille());
    }

    @Test
    void conversionRefuseUnSiteAbsent() {
        assertThrows(NullPointerException.class, () -> SiteTransportResponse.depuis(null));
    }

    @Test
    void requeteNeFaitPasLaValidationMetier() {
        var requete = new CreationSiteTransportRequest(null);
        assertThrows(IllegalArgumentException.class, () -> new SiteTransport(requete.nomVille()));
    }
}
