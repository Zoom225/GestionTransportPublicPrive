package com.gestion.GestionTransport.trajet;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class TypeTrajetJsonTests {
    @Autowired
    private ObjectMapper mapper;

    @ParameterizedTest
    @EnumSource(TypeTrajet.class)
    void conserverLeChoixDuClientDansUnEchangeJson(TypeTrajet type) {
        String json = mapper.writeValueAsString(type);
        assertThat(json).isEqualTo("\"" + type.name() + "\"");
        assertThat(mapper.readValue(json, TypeTrajet.class)).isEqualTo(type);
    }

    @ParameterizedTest
    @ValueSource(strings = {"INCONNU", "public", "PRIVÉ"})
    void refuserUnLibelleHorsDuContrat(String libelle) {
        assertThatThrownBy(() -> mapper.readValue("\"" + libelle + "\"", TypeTrajet.class))
                .isInstanceOf(JacksonException.class);
    }
}