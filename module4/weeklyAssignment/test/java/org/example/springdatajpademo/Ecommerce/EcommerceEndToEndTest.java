package org.example.springdatajpademo.Ecommerce;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.springdatajpademo.Ecommerce.DTO.AuthRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.AuthResponseDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerResponseDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerUpdateDTO;
import org.example.springdatajpademo.Ecommerce.DTO.OrderRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.OrderResponseDTO;
import org.example.springdatajpademo.Ecommerce.DTO.ProductRequestDTO;
import org.example.springdatajpademo.Ecommerce.model.Product;
import org.example.springdatajpademo.Ecommerce.repository.ProductRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Ecommerce API End-to-End Tests")
class EcommerceEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepo productRepo;

    @Test
    void testCreateCustomer() throws Exception {
        CustomerRequestDTO request = newCustomerRequest("create-customer");

        mockMvc.perform(post("/api/ecom/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customer_id").exists())
                .andExpect(jsonPath("$.email").value(request.getEmail()));
    }

    @Test
    void testGetAllCustomers() throws Exception {
        AuthContext auth = registerAndLogin("all-customers");

        mockMvc.perform(get("/api/ecom/customer")
                        .header("Authorization", auth.bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void testGetCustomerById() throws Exception {
        AuthContext auth = registerAndLogin("customer-by-id");

        mockMvc.perform(get("/api/ecom/customer/{id}", auth.customerId)
                        .header("Authorization", auth.bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer_id").value(auth.customerId));
    }

    @Test
    void testUpdateCustomer() throws Exception {
        AuthContext auth = registerAndLogin("update-customer");

        CustomerUpdateDTO updateDTO = new CustomerUpdateDTO(
                auth.customerId,
                "Jane Doe",
                auth.email,
                "456 New Street",
                auth.password
        );

        mockMvc.perform(put("/api/ecom/customer/{id}", auth.customerId)
                        .header("Authorization", auth.bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"));
    }

    @Test
    void testProductWriteEndpointsAreAdminOnly() throws Exception {
        AuthContext auth = registerAndLogin("product-admin-only");

        ProductRequestDTO productRequest = new ProductRequestDTO("Laptop", "Dell", "Electronics", 1000.0);

        mockMvc.perform(post("/api/ecom/product")
                        .header("Authorization", auth.bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testOrderFlowForUser() throws Exception {
        AuthContext auth = registerAndLogin("order-flow");

        Product seededProduct = new Product();
        seededProduct.setName("Phone");
        seededProduct.setBrand("Samsung");
        seededProduct.setCategory("Electronics");
        seededProduct.setCost(800.0);
        Product savedProduct = productRepo.save(seededProduct);

        OrderRequestDTO orderRequest = new OrderRequestDTO(
                null,
                auth.customerId,
                savedProduct.getId(),
                2,
                "PENDING"
        );

        MvcResult createdOrderResult = mockMvc.perform(post("/api/ecom/order")
                        .header("Authorization", auth.bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        OrderResponseDTO createdOrder = objectMapper.readValue(
                createdOrderResult.getResponse().getContentAsString(),
                OrderResponseDTO.class
        );

        mockMvc.perform(get("/api/ecom/order/{id}", createdOrder.getId())
                        .header("Authorization", auth.bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdOrder.getId()));

        mockMvc.perform(get("/api/ecom/order/customer/{id}", auth.customerId)
                        .header("Authorization", auth.bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        mockMvc.perform(delete("/api/ecom/order/{id}", createdOrder.getId())
                        .header("Authorization", auth.bearerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void testAdminOnlyOrderEndpointsForUser() throws Exception {
        AuthContext auth = registerAndLogin("order-admin-only");

        mockMvc.perform(get("/api/ecom/order")
                        .header("Authorization", auth.bearerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testInvalidCustomerEmailValidation() throws Exception {
        CustomerRequestDTO invalid = new CustomerRequestDTO(
                "Invalid",
                "invalid-email",
                "Bad Address",
                "password123"
        );

        mockMvc.perform(post("/api/ecom/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/ecom/product"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetNonExistentCustomerWithAuth() throws Exception {
        AuthContext auth = registerAndLogin("non-existent-customer");

        mockMvc.perform(get("/api/ecom/customer/{id}", 999999)
                        .header("Authorization", auth.bearerToken))
                .andExpect(status().isNotFound());
    }

    private AuthContext registerAndLogin(String marker) throws Exception {
        CustomerRequestDTO request = newCustomerRequest(marker);

        MvcResult createdCustomerResult = mockMvc.perform(post("/api/ecom/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        CustomerResponseDTO customer = objectMapper.readValue(
                createdCustomerResult.getResponse().getContentAsString(),
                CustomerResponseDTO.class
        );

        AuthRequestDTO authRequestDTO = new AuthRequestDTO(request.getEmail(), request.getPassword());

        MvcResult loginResult = mockMvc.perform(post("/api/ecom/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponseDTO authResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(),
                AuthResponseDTO.class
        );

        return new AuthContext(customer.getId(), request.getEmail(), request.getPassword(), "Bearer " + authResponse.getToken());
    }

    private CustomerRequestDTO newCustomerRequest(String marker) {
        String unique = marker + "-" + System.nanoTime();
        return new CustomerRequestDTO(
                "User " + marker,
                unique + "@example.com",
                "123 Main Street",
                "password123"
        );
    }

    private record AuthContext(Integer customerId, String email, String password, String bearerToken) {
    }
}
