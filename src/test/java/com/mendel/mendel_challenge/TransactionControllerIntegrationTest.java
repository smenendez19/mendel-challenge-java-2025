package com.mendel.mendel_challenge;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mendel.mendel_challenge.dto.GetSumTransactionResponse;
import com.mendel.mendel_challenge.dto.GetTransactionsByTypeResponse;
import com.mendel.mendel_challenge.dto.PutNewTransactionRequest;
import com.mendel.mendel_challenge.dto.PutNewTransactionResponse;
import com.mendel.mendel_challenge.repository.TransactionRepository;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Transaction Controller Integration Tests")
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
    }

    @Test
    @DisplayName("Debe insertar una transaccion exitosamente")
    void testPutNewTransaction_Success() throws Exception {
        Long transactionId = 1000L;
        PutNewTransactionRequest request = new PutNewTransactionRequest(100.50, "CREDIT", null);

        MvcResult result = mockMvc.perform(put("/transactions/{transactionId}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        PutNewTransactionResponse response = objectMapper.readValue(responseContent, PutNewTransactionResponse.class);
        
        assertEquals("ok", response.getStatus());
        assertTrue(transactionRepository.findById(transactionId).isPresent());
    }

    @Test
    @DisplayName("Debe lanzar error al insertar una transaccion duplicada")
    void testPutNewTransaction_DuplicateTransaction() throws Exception {
        Long transactionId = 2000L;
        PutNewTransactionRequest request = new PutNewTransactionRequest(200.75, "DEBIT", null);

        mockMvc.perform(put("/transactions/{transactionId}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/transactions/{transactionId}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Transaction already exists with id: 2000")));
    }

    @Test
    @DisplayName("Debe lanzar error al insertar transaccion sin amount")
    void testPutNewTransaction_MissingAmount() throws Exception {
        Long transactionId = 3000L;
        String requestJson = "{\"type\": \"CREDIT\"}";

        mockMvc.perform(put("/transactions/{transactionId}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Amount is required")));
    }

    @Test
    @DisplayName("Debe lanzar error al insertar transacción sin type")
    void testPutNewTransaction_MissingType() throws Exception {
        Long transactionId = 4000L;
        String requestJson = "{\"amount\": 150.00}";

        mockMvc.perform(put("/transactions/{transactionId}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Type is required")));
    }

    @Test
    @DisplayName("Debe lanzar error al insertar transaccion sin amount ni type")
    void testPutNewTransaction_MissingAmountAndType() throws Exception {
        Long transactionId = 5000L;
        String requestJson = "{}";

        mockMvc.perform(put("/transactions/{transactionId}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Debe insertar una transaccion exitosamente con parent_id")
    void testPutNewTransaction_WithParentId_Success() throws Exception {
        Long parentId = 6000L;
        PutNewTransactionRequest parentRequest = new PutNewTransactionRequest(500.00, "CREDIT", null);
        
        mockMvc.perform(put("/transactions/{transactionId}", parentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parentRequest)))
                .andExpect(status().isOk());

        Long childId = 6001L;
        PutNewTransactionRequest childRequest = new PutNewTransactionRequest(100.00, "DEBIT", parentId);

        mockMvc.perform(put("/transactions/{transactionId}", childId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(childRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));

        assertTrue(transactionRepository.findById(childId).isPresent());
        assertEquals(parentId, transactionRepository.findById(childId).get().getParentId());
    }

    @Test
    @DisplayName("Debe lanzar error al insertar transaccion con parent_id inexistente")
    void testPutNewTransaction_WithNonExistentParentId() throws Exception {
        Long nonExistentParentId = 99999L;
        Long childId = 6002L;
        PutNewTransactionRequest childRequest = new PutNewTransactionRequest(100.00, "DEBIT", nonExistentParentId);

        mockMvc.perform(put("/transactions/{transactionId}", childId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(childRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Parent Transaction not found with id: 99999")));
    }

    @Test
    @DisplayName("Debe consultar transacciones por type exitosamente")
    void testGetTransactionsByType_Success() throws Exception {
        String type = "SHOPPING";
        
        PutNewTransactionRequest request1 = new PutNewTransactionRequest(50.00, type, null);
        PutNewTransactionRequest request2 = new PutNewTransactionRequest(75.00, type, null);
        PutNewTransactionRequest request3 = new PutNewTransactionRequest(100.00, "CREDIT", null);

        mockMvc.perform(put("/transactions/7001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/transactions/7002")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/transactions/7003")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request3)))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/transactions/types/{type}", type))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionIds").isArray())
                .andExpect(jsonPath("$.transactionIds", hasSize(2)))
                .andExpect(jsonPath("$.transactionIds", containsInAnyOrder(7001, 7002)))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        GetTransactionsByTypeResponse response = objectMapper.readValue(responseContent, GetTransactionsByTypeResponse.class);
        
        assertEquals(2, response.getTransactionIds().size());
        assertTrue(response.getTransactionIds().contains(7001L));
        assertTrue(response.getTransactionIds().contains(7002L));
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando el type no existe")
    void testGetTransactionsByType_TypeNotFound() throws Exception {
        PutNewTransactionRequest request = new PutNewTransactionRequest(100.00, "CREDIT", null);
        
        mockMvc.perform(put("/transactions/8001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/transactions/types/{type}", "NONEXISTENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionIds").isArray())
                .andExpect(jsonPath("$.transactionIds", hasSize(0)))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        GetTransactionsByTypeResponse response = objectMapper.readValue(responseContent, GetTransactionsByTypeResponse.class);
        
        assertTrue(response.getTransactionIds().isEmpty());
    }

    @Test
    @DisplayName("Debe retornar suma correcta de transaccion sin hijos (solo suma de sí misma)")
    void testGetSum_NoChildren_Success() throws Exception {
        Long transactionId = 9000L;
        PutNewTransactionRequest request = new PutNewTransactionRequest(250.00, "CREDIT", null);
        
        mockMvc.perform(put("/transactions/{transactionId}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // La suma incluye el monto de la transacción misma (250.0)
        MvcResult result = mockMvc.perform(get("/transactions/sum/{transactionId}", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value(250.0))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        GetSumTransactionResponse response = objectMapper.readValue(responseContent, GetSumTransactionResponse.class);
        
        assertEquals(250.0, response.getSum());
    }

    @Test
    @DisplayName("Debe calcular suma correcta de 3 transacciones hijas con el mismo parent_id")
    void testGetSum_ThreeChildrenSameParent_Success() throws Exception {
        Long parentId = 10000L;
        PutNewTransactionRequest parentRequest = new PutNewTransactionRequest(1000.00, "PARENT", null);
        
        mockMvc.perform(put("/transactions/{transactionId}", parentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parentRequest)))
                .andExpect(status().isOk());

        PutNewTransactionRequest child1 = new PutNewTransactionRequest(100.00, "CHILD", parentId);
        PutNewTransactionRequest child2 = new PutNewTransactionRequest(200.00, "CHILD", parentId);
        PutNewTransactionRequest child3 = new PutNewTransactionRequest(300.00, "CHILD", parentId);

        mockMvc.perform(put("/transactions/10001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(child1)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/transactions/10002")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(child2)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/transactions/10003")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(child3)))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/transactions/sum/{transactionId}", parentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value(1600.0))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        GetSumTransactionResponse response = objectMapper.readValue(responseContent, GetSumTransactionResponse.class);
        
        assertEquals(1600.0, response.getSum());
    }

    @Test
    @DisplayName("Debe retornar suma 0.0 cuando transaction_id no existe")
    void testGetSum_TransactionNotFound() throws Exception {
        Long nonExistentId = 99999L;

        mockMvc.perform(get("/transactions/sum/{transactionId}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Transaction not found with id: 99999")));
    }
    
    @Test
    @DisplayName("Debe manejar transacciones con jerarquia de multiples niveles")
    void testGetSum_MultiLevelHierarchy_Success() throws Exception {
        PutNewTransactionRequest grandparent = new PutNewTransactionRequest(1000.00, "GP", null);
        mockMvc.perform(put("/transactions/12000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(grandparent)))
                .andExpect(status().isOk());

        PutNewTransactionRequest parent = new PutNewTransactionRequest(500.00, "P", 12000L);
        mockMvc.perform(put("/transactions/12001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parent)))
                .andExpect(status().isOk());

        PutNewTransactionRequest child = new PutNewTransactionRequest(250.00, "C", 12001L);
        mockMvc.perform(put("/transactions/12002")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(child)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/transactions/sum/12000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value(1750.0));

        mockMvc.perform(get("/transactions/sum/12001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value(750.0));
    }

    @Test
    @DisplayName("Debe validar que el amount sea positivo")
    void testPutNewTransaction_NegativeAmount() throws Exception {
        Long transactionId = 13000L;
        String requestJson = "{\"amount\": -100.00, \"type\": \"DEBIT\"}";

        mockMvc.perform(put("/transactions/{transactionId}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Amount must be positive")));
    }

    @Test
    @DisplayName("Debe manejar multiples types diferentes correctamente")
    void testGetTransactionsByType_MultipleDifferentTypes() throws Exception {
        mockMvc.perform(put("/transactions/14001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PutNewTransactionRequest(100.0, "TYPE_A", null))))
                .andExpect(status().isOk());

        mockMvc.perform(put("/transactions/14002")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PutNewTransactionRequest(200.0, "TYPE_B", null))))
                .andExpect(status().isOk());

        mockMvc.perform(put("/transactions/14003")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PutNewTransactionRequest(300.0, "TYPE_A", null))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/transactions/types/TYPE_A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionIds", hasSize(2)))
                .andExpect(jsonPath("$.transactionIds", containsInAnyOrder(14001, 14003)));

        mockMvc.perform(get("/transactions/types/TYPE_B"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionIds", hasSize(1)))
                .andExpect(jsonPath("$.transactionIds", contains(14002)));
    }

    @Test
    @DisplayName("Debe calcular suma recursiva correcta con 4 niveles de jerarquia")
    void testGetSum_FourLevelHierarchy_Success() throws Exception {
        mockMvc.perform(put("/transactions/15000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PutNewTransactionRequest(1000.0, "ROOT", null))))
                .andExpect(status().isOk());

        mockMvc.perform(put("/transactions/15001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PutNewTransactionRequest(100.0, "CHILD1", 15000L))))
                .andExpect(status().isOk());

        mockMvc.perform(put("/transactions/15002")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PutNewTransactionRequest(200.0, "CHILD2", 15000L))))
                .andExpect(status().isOk());

        mockMvc.perform(put("/transactions/15003")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PutNewTransactionRequest(50.0, "GRANDCHILD1", 15001L))))
                .andExpect(status().isOk());

        mockMvc.perform(put("/transactions/15004")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PutNewTransactionRequest(75.0, "GRANDCHILD2", 15002L))))
                .andExpect(status().isOk());

        mockMvc.perform(put("/transactions/15005")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PutNewTransactionRequest(25.0, "GREATGRANDCHILD", 15003L))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/transactions/sum/15000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value(1450.0));

        mockMvc.perform(get("/transactions/sum/15001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value(175.0));

        mockMvc.perform(get("/transactions/sum/15002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value(275.0));

        mockMvc.perform(get("/transactions/sum/15003"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value(75.0));

        mockMvc.perform(get("/transactions/sum/15005"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value(25.0));
    }

    @Test
    @DisplayName("Debe funcionar segun el ejemplo especificado: PUT 10, 11, 12 y GET sums")
    void testExampleScenario() throws Exception {
        mockMvc.perform(put("/transactions/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PutNewTransactionRequest(5000.0, "cars", null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));

        mockMvc.perform(put("/transactions/11")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PutNewTransactionRequest(10000.0, "shopping", 10L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));

        mockMvc.perform(put("/transactions/12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PutNewTransactionRequest(5000.0, "shopping", 11L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));

        mockMvc.perform(get("/transactions/types/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionIds", hasSize(1)))
                .andExpect(jsonPath("$.transactionIds[0]").value(10));

        mockMvc.perform(get("/transactions/sum/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value(20000.0));

        mockMvc.perform(get("/transactions/sum/11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value(15000.0));
    }
}
