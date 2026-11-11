package com.gestion.GestionTransport.sitetransport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SiteTransportController.class)
@WithMockUser
class SiteTransportControllerTests {
    @Autowired MockMvc mvc;
    @MockitoBean SiteTransportService service;

    @Test void listeVide() throws Exception {
        when(service.lister()).thenReturn(List.of());
        mvc.perform(get("/api/sites")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
    }

    @Test void listeDeuxVilles() throws Exception {
        when(service.lister()).thenReturn(List.of(new SiteTransport("Bruxelles"), new SiteTransport("Namur")));
        mvc.perform(get("/api/sites")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomVille").value("bruxelles"))
                .andExpect(jsonPath("$[1].nomVille").value("namur"));
    }

    @Test void rechercheExistante() throws Exception {
        var site = new SiteTransport("Mons");
        when(service.rechercher(site.getId())).thenReturn(site);
        mvc.perform(get("/api/sites/" + site.getId())).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(site.getId().toString()))
                .andExpect(jsonPath("$.nomVille").value("mons"));
    }

    @Test void rechercheInexistante() throws Exception {
        var id = UUID.randomUUID();
        when(service.rechercher(id)).thenThrow(SiteTransportException.introuvable());
        mvc.perform(get("/api/sites/" + id)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test void identifiantInvalide() throws Exception {
        mvc.perform(get("/api/sites/pas-un-uuid")).andExpect(status().isBadRequest());
    }

    @Test void creation() throws Exception {
        var site = new SiteTransport("Bruxelles");
        when(service.creer("Bruxelles")).thenReturn(site);
        mvc.perform(post("/api/sites").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nomVille\":\"Bruxelles\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/sites/" + site.getId()))
                .andExpect(jsonPath("$.nomVille").value("bruxelles"));
    }

    @Test void doublon() throws Exception {
        when(service.creer("Bruxelles")).thenThrow(SiteTransportException.dejaExistant(null));
        mvc.perform(post("/api/sites").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nomVille\":\"Bruxelles\"}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
    }

    @Test void nomVide() throws Exception {
        when(service.creer("")).thenThrow(new IllegalArgumentException("Nom obligatoire"));
        mvc.perform(post("/api/sites").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nomVille\":\"\"}")).andExpect(status().isBadRequest());
    }

    @Test void nomNull() throws Exception {
        when(service.creer(null)).thenThrow(new IllegalArgumentException("Nom obligatoire"));
        mvc.perform(post("/api/sites").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nomVille\":null}")).andExpect(status().isBadRequest());
    }

    @Test void jsonMalForme() throws Exception {
        mvc.perform(post("/api/sites").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("{invalid")).andExpect(status().isBadRequest());
    }

    @Test void creationSansCsrfRefusee() throws Exception {
        mvc.perform(post("/api/sites").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nomVille\":\"Bruxelles\"}")).andExpect(status().isForbidden());
    }
}
