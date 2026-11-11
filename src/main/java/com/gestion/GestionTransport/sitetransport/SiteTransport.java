package com.gestion.GestionTransport.sitetransport;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

/** Une ville de transport, unique selon son nom canonique. */
@Entity
@Table(name = "sites_transport", uniqueConstraints =
        @UniqueConstraint(name = "uk_site_transport_nom_ville", columnNames = "nom_ville"))
public class SiteTransport {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "nom_ville", nullable = false, length = 120)
    private String nomVille;

    protected SiteTransport() {
        // Réservé à JPA : aucun objet métier incomplet exposé au code appelant.
    }

    public SiteTransport(String nomVille) {
        this.nomVille = normaliser(nomVille);
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public String getNomVille() {
        return nomVille;
    }

    private static String normaliser(String nomVille) {
        if (nomVille == null) {
            throw new IllegalArgumentException("Le nom de ville est obligatoire.");
        }
        String canonique = Normalizer.normalize(nomVille, Normalizer.Form.NFKC)
                .replaceAll("[\\p{javaWhitespace}\\p{Zs}]+", " ")
                .trim()
                .toLowerCase(Locale.ROOT);
        if (canonique.isEmpty()) {
            throw new IllegalArgumentException("Le nom de ville est obligatoire.");
        }
        if (canonique.length() > 120) {
            throw new IllegalArgumentException("Le nom de ville ne doit pas dépasser 120 caractères.");
        }
        return canonique;
    }
}