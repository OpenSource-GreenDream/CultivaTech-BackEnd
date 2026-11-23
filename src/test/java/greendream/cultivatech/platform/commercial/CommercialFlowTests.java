package greendream.cultivatech.platform.commercial;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:commercial-flow;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class CommercialFlowTests {
    @Autowired private MockMvc mvc;

    @Test
    void catalogProductCanBeOrderedAndOrderStatusUpdated() throws Exception {
        mvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());
        mvc.perform(get("/api/v1/products/{id}", 999999))
                .andExpect(status().isNotFound());

        String product = mvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content("""
                                {"inventoryId":991,"fieldId":null,"name":"Organic compost","description":"One bag","price":12.50,"type":"fertilizer","imageUrl":"https://example.test/compost.png"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Organic compost"))
                .andExpect(jsonPath("$.type").value("fertilizer"))
                .andReturn().getResponse().getContentAsString();
        long productId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(product).get("id").asLong();

        mvc.perform(get("/api/v1/products/{id}", productId))
                .andExpect(status().isOk()).andExpect(jsonPath("$.price").value(12.5));

        String order = mvc.perform(post("/api/v1/orders")
                        .contentType("application/json")
                        .content("""
                                {"profileId":72,"productId":%d,"quantity":3,"paymentMethod":1,"subscription":false}
                                """.formatted(productId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName").value("Organic compost"))
                .andExpect(jsonPath("$.totalAmount").value(37.5))
                .andExpect(jsonPath("$.status").value(0))
                .andReturn().getResponse().getContentAsString();
        long orderId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(order).get("id").asLong();

        mvc.perform(get("/api/v1/orders").param("profileId", "72"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(orderId));
        mvc.perform(patch("/api/v1/orders/{id}/status", orderId)
                        .contentType("application/json").content("{\"status\":1}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value(1));
    }
}
