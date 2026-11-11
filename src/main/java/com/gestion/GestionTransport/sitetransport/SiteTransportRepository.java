package com.gestion.GestionTransport.sitetransport;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteTransportRepository extends JpaRepository<SiteTransport, UUID> {
    /** Recherche exacte : le nom fourni doit être déjà canonique, comme dans le modèle. */
    Optional<SiteTransport> findByNomVille(String nomVilleCanonique);
}