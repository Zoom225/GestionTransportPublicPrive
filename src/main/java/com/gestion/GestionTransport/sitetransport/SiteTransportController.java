package com.gestion.GestionTransport.sitetransport;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** API REST des villes de transport ; aucune logique métier dans le contrôleur. */
@RestController
@RequestMapping("/api/sites")
public class SiteTransportController {
    private final SiteTransportService service;

    public SiteTransportController(SiteTransportService service) {
        this.service = service;
    }

    @GetMapping
    public List<SiteTransportResponse> lister() {
        return service.lister().stream().map(SiteTransportResponse::depuis).toList();
    }

    @GetMapping("/{id}")
    public SiteTransportResponse rechercher(@PathVariable UUID id) {
        return SiteTransportResponse.depuis(service.rechercher(id));
    }

    @PostMapping
    public ResponseEntity<SiteTransportResponse> creer(@RequestBody CreationSiteTransportRequest requete) {
        var site = service.creer(requete.nomVille());
        return ResponseEntity.created(URI.create("/api/sites/" + site.getId()))
                .body(SiteTransportResponse.depuis(site));
    }
}
