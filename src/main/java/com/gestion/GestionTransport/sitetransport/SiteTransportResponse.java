package com.gestion.GestionTransport.sitetransport;

import java.util.Objects;
import java.util.UUID;

/** Données publiques d'un site de transport, sans exposer l'entité JPA. */
public record SiteTransportResponse(UUID id, String nomVille) {
    public static SiteTransportResponse depuis(SiteTransport site) {
        Objects.requireNonNull(site, "Le site de transport est obligatoire.");
        return new SiteTransportResponse(site.getId(), site.getNomVille());
    }
}
