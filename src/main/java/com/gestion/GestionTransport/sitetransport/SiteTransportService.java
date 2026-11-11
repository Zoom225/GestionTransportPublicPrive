package com.gestion.GestionTransport.sitetransport;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SiteTransportService {
    private final SiteTransportRepository repository;

    public SiteTransportService(SiteTransportRepository repository) {
        this.repository = repository;
    }

    public List<SiteTransport> lister() { return repository.findAll(); }

    public SiteTransport rechercher(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("L identifiant du site de transport est obligatoire.");
        }
        return repository.findById(id).orElseThrow(SiteTransportException::introuvable);
    }

    @Transactional
    public SiteTransport creer(String nomVille) {
        var site = new SiteTransport(nomVille);
        if (repository.findByNomVille(site.getNomVille()).isPresent()) {
            throw SiteTransportException.dejaExistant(null);
        }
        try {
            // Le flush fait remonter la contrainte avant la sortie de la méthode transactionnelle.
            return repository.saveAndFlush(site);
        } catch (DataIntegrityViolationException exception) {
            if (estDoublonDeVille(exception)) {
                throw SiteTransportException.dejaExistant(exception);
            }
            throw exception;
        }
    }

    private boolean estDoublonDeVille(Throwable exception) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof ConstraintViolationException violation) {
                String contrainte = violation.getConstraintName();
                return "23505".equals(violation.getSQLState()) && contrainte != null
                        && contrainte.toLowerCase(Locale.ROOT).contains("uk_site_transport_nom_ville");
            }
        }
        return false;
    }
}