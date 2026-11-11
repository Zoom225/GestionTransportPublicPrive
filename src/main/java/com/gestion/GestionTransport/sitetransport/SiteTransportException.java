package com.gestion.GestionTransport.sitetransport;

/** Erreur métier indépendante du transport HTTP. */
public final class SiteTransportException extends RuntimeException {
    public enum Code { SITE_TRANSPORT_INTROUVABLE, SITE_TRANSPORT_DEJA_EXISTANT }
    private final Code code;

    private SiteTransportException(Code code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    static SiteTransportException introuvable() {
        return new SiteTransportException(Code.SITE_TRANSPORT_INTROUVABLE,
                "Le site de transport demandé est introuvable.", null);
    }

    static SiteTransportException dejaExistant(Throwable cause) {
        return new SiteTransportException(Code.SITE_TRANSPORT_DEJA_EXISTANT,
                "Un site de transport existe déjà pour cette ville.", cause);
    }

    public Code getCode() { return code; }
}