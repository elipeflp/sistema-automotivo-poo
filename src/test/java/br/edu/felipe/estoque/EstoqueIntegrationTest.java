package br.edu.felipe.estoque;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.*;
import java.util.Map;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EstoqueIntegrationTest {
  @Autowired MockMvc mvc;
  @Autowired ObjectMapper json;

  long criar(String path, Object body) throws Exception {
    return json.readTree(
            mvc.perform(
                    post("/api" + path)
                        .contentType("application/json")
                        .content(json.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString())
        .get("id")
        .asLong();
  }

  long modelo() throws Exception {
    long marca = criar("/marcas", Map.of("nome", "Honda"));
    return criar("/modelos", Map.of("nome", "Civic", "marcaId", marca));
  }

  Map<String, Object> veiculo(long modelo, String status) {
    return Map.of(
        "modeloId",
        modelo,
        "ano",
        2024,
        "cor",
        "Preto",
        "preco",
        120000,
        "quilometragem",
        20000,
        "status",
        status,
        "tipo",
        "CARRO");
  }

  @Test
  void crudCompleto() throws Exception {
    long modelo = modelo(), id = criar("/veiculos", veiculo(modelo, "DISPONIVEL"));
    mvc.perform(get("/api/veiculos/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.marca").value("Honda"))
        .andExpect(jsonPath("$.modelo").value("Civic"));
    var dados = new java.util.HashMap<>(veiculo(modelo, "VENDIDO"));
    dados.put("preco", 115000);
    dados.put("quilometragem", 21000);
    mvc.perform(
            put("/api/veiculos/" + id)
                .contentType("application/json")
                .content(json.writeValueAsString(dados)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.preco").value(115000))
        .andExpect(jsonPath("$.quilometragem").value(21000))
        .andExpect(jsonPath("$.status").value("VENDIDO"));
    mvc.perform(delete("/api/veiculos/" + id)).andExpect(status().isNoContent());
    mvc.perform(get("/api/veiculos/" + id)).andExpect(status().isNotFound());
  }

  @Test
  void filtrosCombinados() throws Exception {
    long m = modelo();
    long id = criar("/veiculos", veiculo(m, "DISPONIVEL"));
    criar("/veiculos", veiculo(m, "VENDIDO"));
    var v =
        json.readTree(
            mvc.perform(get("/api/veiculos/" + id)).andReturn().getResponse().getContentAsString());
    mvc.perform(
            get("/api/veiculos")
                .param("marcaId", v.get("marcaId").asText())
                .param("modeloId", "" + m)
                .param("precoMin", "100000")
                .param("precoMax", "130000")
                .param("anoMin", "2024")
                .param("anoMax", "2024")
                .param("status", "DISPONIVEL")
                .param("tipo", "CARRO"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
    mvc.perform(get("/api/veiculos").param("precoMax", "50000"))
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void bloqueiaExclusaoDisponivel() throws Exception {
    long id = criar("/veiculos", veiculo(modelo(), "DISPONIVEL"));
    mvc.perform(delete("/api/veiculos/" + id)).andExpect(status().isConflict());
  }

  @Test
  void excluiDescontinuado() throws Exception {
    long id = criar("/veiculos", veiculo(modelo(), "DESCONTINUADO"));
    mvc.perform(delete("/api/veiculos/" + id)).andExpect(status().isNoContent());
  }

  @Test
  void rejeitaValoresInvalidos() throws Exception {
    var v = new java.util.HashMap<>(veiculo(modelo(), "DISPONIVEL"));
    v.put("preco", -1);
    v.put("quilometragem", -5);
    mvc.perform(
            post("/api/veiculos")
                .contentType("application/json")
                .content(json.writeValueAsString(v)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detalhes.length()").value(2));
  }

  @Test
  void rejeitaAnoFuturo() throws Exception {
    var v = new java.util.HashMap<>(veiculo(modelo(), "DISPONIVEL"));
    v.put("ano", java.time.Year.now().getValue() + 2);
    mvc.perform(
            post("/api/veiculos")
                .contentType("application/json")
                .content(json.writeValueAsString(v)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void rejeitaModeloInexistente() throws Exception {
    mvc.perform(
            post("/api/veiculos")
                .contentType("application/json")
                .content(json.writeValueAsString(veiculo(999999L, "DISPONIVEL"))))
        .andExpect(status().isNotFound());
  }

  @Test
  void rejeitaDuplicidadeMarca() throws Exception {
    criar("/marcas", Map.of("nome", "Honda"));
    mvc.perform(
            post("/api/marcas").contentType("application/json").content("{\"nome\":\" honda \"}"))
        .andExpect(status().isConflict());
  }

  @Test
  void preservaVinculos() throws Exception {
    long m = modelo();
    criar("/veiculos", veiculo(m, "DISPONIVEL"));
    mvc.perform(delete("/api/modelos/" + m)).andExpect(status().isConflict());
    long marca =
        json.readTree(
                mvc.perform(get("/api/modelos")).andReturn().getResponse().getContentAsString())
            .get(0)
            .get("marcaId")
            .asLong();
    mvc.perform(delete("/api/marcas/" + marca)).andExpect(status().isConflict());
  }

  @Test
  void rejeitaFaixasInvertidas() throws Exception {
    mvc.perform(get("/api/veiculos").param("precoMin", "200").param("precoMax", "100"))
        .andExpect(status().isBadRequest());
    mvc.perform(get("/api/veiculos").param("anoMin", "2025").param("anoMax", "2020"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void rejeitaStatusInvalido() throws Exception {
    mvc.perform(get("/api/veiculos").param("status", "INEXISTENTE"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void atualizaCatalogoEExcluiSemVinculos() throws Exception {
    long marca = criar("/marcas", Map.of("nome", "Marca A"));
    long modelo = criar("/modelos", Map.of("nome", "Modelo A", "marcaId", marca));
    mvc.perform(
            put("/api/marcas/" + marca)
                .contentType("application/json")
                .content("{\"nome\":\"Marca B\"}"))
        .andExpect(status().isOk());
    mvc.perform(
            put("/api/modelos/" + modelo)
                .contentType("application/json")
                .content(json.writeValueAsString(Map.of("nome", "Modelo B", "marcaId", marca))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.marca").value("Marca B"));
    mvc.perform(delete("/api/marcas/" + marca)).andExpect(status().isConflict());
    mvc.perform(delete("/api/modelos/" + modelo)).andExpect(status().isNoContent());
    mvc.perform(delete("/api/marcas/" + marca)).andExpect(status().isNoContent());
  }
}
