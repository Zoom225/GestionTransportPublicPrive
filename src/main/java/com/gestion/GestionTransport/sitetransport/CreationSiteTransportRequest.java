package com.gestion.GestionTransport.sitetransport;

/**
 * Données reçues pour une future création de site de transport.
 * La validation du nom et sa normalisation restent dans SiteTransport.
 */
public record CreationSiteTransportRequest(String nomVille) {
}
