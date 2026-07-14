package it.scuola.materie_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.scuola.materie_service.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test di integrazione per MateriaController.
 * @SpringBootTest avvia il contesto Spring completo con DB reale.
 * Il JwtService è mockato per bypassare la validazione del token.
 */
@SpringBootTest
@ActiveProfiles("sviluppo")
@Transactional
class MateriaIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private JwtService jwtService;

    private static final String TOKEN = "Bearer token-di-test";
    private static final String URL_MATERIE = "/api/v1/materie";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        when(jwtService.isTokenValid(any())).thenReturn(true);
        when(jwtService.extractSubject(any())).thenReturn("test@scuola.it");
        when(jwtService.extractRole(any())).thenReturn("SEGRETERIA");
    }

    @Test
    void trovaTutte_senzaToken_ritorna401() throws Exception {
        mockMvc.perform(get(URL_MATERIE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void trovaTutte_conToken_ritorna200() throws Exception {
        mockMvc.perform(get(URL_MATERIE)
                        .header("Authorization", TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void creaMateria_valida_ritorna201() throws Exception {
        String body = """
                {
                  "nome": "Matematica",
                  "codice": "MAT01",
                  "descrizione": "Matematica di base",
                  "oreSettimanali": 5,
                  "tipoMateria": "TEORICA"
                }
                """;

        mockMvc.perform(post(URL_MATERIE)
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Matematica"))
                .andExpect(jsonPath("$.codice").value("MAT01"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void creaMateria_campiObbligatoriMancanti_ritorna400() throws Exception {
        mockMvc.perform(post(URL_MATERIE)
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void creaMateria_codiceDuplicato_ritorna409() throws Exception {
        String prima = """
                {
                  "nome": "Fisica",
                  "codice": "FIS01",
                  "oreSettimanali": 3,
                  "tipoMateria": "TEORICA"
                }
                """;

        mockMvc.perform(post(URL_MATERIE)
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prima))
                .andExpect(status().isCreated());

        String seconda = """
                {
                  "nome": "Fisica Avanzata",
                  "codice": "FIS01",
                  "oreSettimanali": 4,
                  "tipoMateria": "LABORATORIO"
                }
                """;

        mockMvc.perform(post(URL_MATERIE)
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(seconda))
                .andExpect(status().isConflict());
    }

    @Test
    void trovaPerID_esistente_ritorna200() throws Exception {
        String body = """
                {
                  "nome": "Chimica",
                  "codice": "CHI01",
                  "oreSettimanali": 3,
                  "tipoMateria": "LABORATORIO"
                }
                """;

        String risposta = mockMvc.perform(post(URL_MATERIE)
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(risposta).get("id").asText();

        mockMvc.perform(get(URL_MATERIE + "/" + id)
                        .header("Authorization", TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Chimica"));
    }

    @Test
    void trovaPerID_nonEsistente_ritorna404() throws Exception {
        mockMvc.perform(get(URL_MATERIE + "/00000000-0000-0000-0000-000000000000")
                        .header("Authorization", TOKEN))
                .andExpect(status().isNotFound());
    }

    @Test
    void aggiornaMateria_esistente_ritorna200() throws Exception {
        String create = """
                {
                  "nome": "Storia",
                  "codice": "STO01",
                  "oreSettimanali": 2,
                  "tipoMateria": "TEORICA"
                }
                """;

        String risposta = mockMvc.perform(post(URL_MATERIE)
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(create))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(risposta).get("id").asText();

        String update = """
                {
                  "nome": "Storia Moderna",
                  "oreSettimanali": 3
                }
                """;

        mockMvc.perform(put(URL_MATERIE + "/" + id)
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(update))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Storia Moderna"))
                .andExpect(jsonPath("$.oreSettimanali").value(3));
    }

    @Test
    void aggiornaMateria_nonEsistente_ritorna404() throws Exception {
        String update = """
                { "nome": "Qualcosa" }
                """;

        mockMvc.perform(put(URL_MATERIE + "/00000000-0000-0000-0000-000000000000")
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(update))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminaMateria_esistente_ritorna204() throws Exception {
        String body = """
                {
                  "nome": "Educazione Fisica",
                  "codice": "EDF01",
                  "oreSettimanali": 2,
                  "tipoMateria": "PRATICA"
                }
                """;

        String risposta = mockMvc.perform(post(URL_MATERIE)
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(risposta).get("id").asText();

        mockMvc.perform(delete(URL_MATERIE + "/" + id)
                        .header("Authorization", TOKEN))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminaMateria_nonEsistente_ritorna404() throws Exception {
        mockMvc.perform(delete(URL_MATERIE + "/00000000-0000-0000-0000-000000000000")
                        .header("Authorization", TOKEN))
                .andExpect(status().isNotFound());
    }
}
