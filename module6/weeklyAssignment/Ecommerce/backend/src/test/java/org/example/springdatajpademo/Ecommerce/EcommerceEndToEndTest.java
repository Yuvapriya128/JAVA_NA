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

import java.util.Map;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
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
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(request.getEmail()));
    }

    @Test
    void testGetAllCustomers_isAdminOnly() throws Exception {
        AuthContext auth = registerAndLogin("all-customers");

        mockMvc.perform(get("/api/ecom/customer")
                        .header("Authorization", auth.bearerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetCustomerById() throws Exception {
        AuthContext auth = registerAndLogin("customer-by-id");

        mockMvc.perform(get("/api/ecom/customer/{id}", auth.customerId)
                        .header("Authorization", auth.bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(auth.customerId));
    }

    @Test
    void testGetCurrentCustomerProfile() throws Exception {
        AuthContext auth = registerAndLogin("customer-me-get");

        mockMvc.perform(get("/api/ecom/customer/me")
                        .header("Authorization", auth.bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(auth.customerId))
                .andExpect(jsonPath("$.email").value(auth.email));
    }

    @Test
    void testUserCanUpdateOwnProfileUsingMeEndpoint() throws Exception {
        AuthContext auth = registerAndLogin("customer-me-update");

        Map<String, Object> profileUpdate = Map.of(
                "firstName", "Updated",
                "lastName", "User",
                "address", "999 Updated Street",
                "phoneNumber", "+919876543210"
        );

        mockMvc.perform(put("/api/ecom/customer/me")
                        .header("Authorization", auth.bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(auth.customerId))
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.email").value(auth.email))
                .andExpect(jsonPath("$.address").value("999 Updated Street"))
                .andExpect(jsonPath("$.phone").value("+919876543210"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void testUserCanChangeOwnPasswordUsingMeEndpoint() throws Exception {
        AuthContext auth = registerAndLogin("customer-me-password");

        Map<String, Object> passwordChange = Map.of(
                "currentPassword", auth.password,
                "newPassword", "NewPass@123",
                "confirmPassword", "NewPass@123"
        );

        mockMvc.perform(put("/api/ecom/customer/me/password")
                        .header("Authorization", auth.bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordChange)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"));

        String newToken = login(auth.email, "NewPass@123");

        mockMvc.perform(get("/api/ecom/customer/me")
                        .header("Authorization", newToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(auth.email));
    }

    @Test
    void testUserCannotUpdateAnotherCustomerUsingAdminEndpoint() throws Exception {
        AuthContext user1 = registerAndLogin("customer-admin-update-denied-1");
        AuthContext user2 = registerAndLogin("customer-admin-update-denied-2");

        CustomerUpdateDTO updateDTO = new CustomerUpdateDTO(
                user2.customerId,
                "Illegal Update",
                user2.email,
                "No Access Address",
                user2.password
        );

        mockMvc.perform(put("/api/ecom/customer/{id}", user2.customerId)
                        .header("Authorization", user1.bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateCustomer_isAdminOnly() throws Exception {
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
                .andExpect(status().isForbidden());
    }

    @Test
    void testAdminCanUpdateAnyCustomer() throws Exception {
        AuthContext user = registerAndLogin("customer-admin-update-allowed");
        String adminToken = login("admin", "123");

        CustomerUpdateDTO updateDTO = new CustomerUpdateDTO(
                user.customerId,
                "Admin Updated",
                user.email,
                "Admin Updated Address",
                user.password
        );

        mockMvc.perform(put("/api/ecom/customer/{id}", user.customerId)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.customerId))
                .andExpect(jsonPath("$.name").value("Admin Updated"));
    }

    @Test
    void testBootstrapAdminCanCreateAnotherAdmin() throws Exception {
        String adminToken = login("admin", "123");

        String marker = "admin-create-" + System.nanoTime();
        Map<String, Object> request = Map.of(
                "name", "Ops Admin",
                "email", marker + "@example.com",
                "address", "HQ",
                "password", "pass123",
                "role", "ADMIN"
        );

        mockMvc.perform(post("/api/ecom/customer/admin/create")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        String createdAdminToken = login(marker + "@example.com", "pass123");

        mockMvc.perform(get("/api/ecom/customer")
                        .header("Authorization", createdAdminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testAdminCanPromoteCustomerToAdmin() throws Exception {
        AuthContext user = registerAndLogin("promote-user");
        String bootstrapAdminToken = login("admin", "123");

        Map<String, Object> roleUpdate = Map.of("role", "ADMIN");

        mockMvc.perform(put("/api/ecom/customer/{id}/role", user.customerId)
                        .header("Authorization", bootstrapAdminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleUpdate)))
                .andExpect(status().isOk());

        String promotedAdminToken = login(user.email, user.password);

        mockMvc.perform(get("/api/ecom/customer")
                        .header("Authorization", promotedAdminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testAdminCanEditOwnProfileUsingMeEndpoint() throws Exception {
        AuthContext admin = createAndLoginAdmin("admin-me-update", "AdminPass@123");

        Map<String, Object> profileUpdate = Map.of(
        "firstName", "Ops",
        "lastName", "Lead",
        "address", "Admin HQ",
        "phoneNumber", "+14155550123"
        );

        mockMvc.perform(put("/api/ecom/customer/me")
                .header("Authorization", admin.bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileUpdate)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(admin.customerId))
        .andExpect(jsonPath("$.name").value("Ops Lead"))
        .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void testAdminCanChangeOwnPasswordUsingMeEndpoint() throws Exception {
        AuthContext admin = createAndLoginAdmin("admin-me-password", "AdminPass@123");

        Map<String, Object> passwordChange = Map.of(
        "currentPassword", "AdminPass@123",
        "newPassword", "AdminNew@123",
        "confirmPassword", "AdminNew@123"
        );

        mockMvc.perform(put("/api/ecom/customer/me/password")
                .header("Authorization", admin.bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordChange)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Password changed successfully"));

        String newToken = login(admin.email, "AdminNew@123");

        mockMvc.perform(get("/api/ecom/customer/me")
                .header("Authorization", newToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.role").value("ADMIN"))
        .andExpect(jsonPath("$.email").value(admin.email));
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
                "PENDING",
                "CARD"
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
    void testUserCannotCreateOrderForAnotherCustomer() throws Exception {
        AuthContext auth1 = registerAndLogin("order-owner-1");
        AuthContext auth2 = registerAndLogin("order-owner-2");

        Product seededProduct = new Product();
        seededProduct.setName("Mouse");
        seededProduct.setBrand("Logitech");
        seededProduct.setCategory("Electronics");
        seededProduct.setCost(50.0);
        Product savedProduct = productRepo.save(seededProduct);

        OrderRequestDTO orderRequest = new OrderRequestDTO(
                null,
                auth2.customerId,
                savedProduct.getId(),
                1,
                "CONFIRMED",
                "CARD"
        );

        mockMvc.perform(post("/api/ecom/order")
                        .header("Authorization", auth1.bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testMyOrdersReturnsOnlyCurrentUserOrders() throws Exception {
        AuthContext auth1 = registerAndLogin("my-orders-1");
        AuthContext auth2 = registerAndLogin("my-orders-2");

        Product seededProduct = new Product();
        seededProduct.setName("Keyboard");
        seededProduct.setBrand("Keychron");
        seededProduct.setCategory("Electronics");
        seededProduct.setCost(120.0);
        Product savedProduct = productRepo.save(seededProduct);

        OrderRequestDTO order1 = new OrderRequestDTO(null, auth1.customerId, savedProduct.getId(), 1, "CONFIRMED", "CARD");
        OrderRequestDTO order2 = new OrderRequestDTO(null, auth2.customerId, savedProduct.getId(), 1, "CONFIRMED", "UPI");

        MvcResult order1Result = mockMvc.perform(post("/api/ecom/order")
                        .header("Authorization", auth1.bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order1)))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult order2Result = mockMvc.perform(post("/api/ecom/order")
                        .header("Authorization", auth2.bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order2)))
                .andExpect(status().isCreated())
                .andReturn();

        Integer order1Id = objectMapper.readValue(order1Result.getResponse().getContentAsString(), OrderResponseDTO.class).getId();
        Integer order2Id = objectMapper.readValue(order2Result.getResponse().getContentAsString(), OrderResponseDTO.class).getId();

        mockMvc.perform(get("/api/ecom/order/my-orders")
                        .header("Authorization", auth1.bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem(order1Id)))
                .andExpect(jsonPath("$[*].id", not(hasItem(order2Id))));
    }

    @Test
    void testAdminCanAdvanceOrderStatusWorkflow() throws Exception {
        AuthContext auth = registerAndLogin("status-workflow");
        String adminToken = login("admin", "123");

        Product seededProduct = new Product();
        seededProduct.setName("Headphones");
        seededProduct.setBrand("Sony");
        seededProduct.setCategory("Electronics");
        seededProduct.setCost(250.0);
        Product savedProduct = productRepo.save(seededProduct);

        OrderRequestDTO orderRequest = new OrderRequestDTO(null, auth.customerId, savedProduct.getId(), 1, "CONFIRMED", "CASH_ON_DELIVERY");

        MvcResult createdOrderResult = mockMvc.perform(post("/api/ecom/order")
                        .header("Authorization", auth.bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andReturn();

        OrderResponseDTO createdOrder = objectMapper.readValue(
                createdOrderResult.getResponse().getContentAsString(),
                OrderResponseDTO.class
        );

        mockMvc.perform(put("/api/ecom/order/{id}", createdOrder.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new org.example.springdatajpademo.Ecommerce.DTO.OrderUpdateDTO(
                                createdOrder.getId(), auth.customerId, "PROCESSING", createdOrder.getTotalAmount(), null, null
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSING"));

        mockMvc.perform(put("/api/ecom/order/{id}", createdOrder.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new org.example.springdatajpademo.Ecommerce.DTO.OrderUpdateDTO(
                                createdOrder.getId(), auth.customerId, "SHIPPED", createdOrder.getTotalAmount(), null, null
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));

        mockMvc.perform(put("/api/ecom/order/{id}", createdOrder.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new org.example.springdatajpademo.Ecommerce.DTO.OrderUpdateDTO(
                                createdOrder.getId(), auth.customerId, "DELIVERED", createdOrder.getTotalAmount(), null, null
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"));
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

        String bearerToken = login(request.getEmail(), request.getPassword());

        return new AuthContext(customer.getId(), request.getEmail(), request.getPassword(), bearerToken);
    }

    private AuthContext createAndLoginAdmin(String marker, String password) throws Exception {
        String bootstrapAdminToken = login("admin", "123");
        String unique = marker + "-" + System.nanoTime();
        String email = unique + "@example.com";

        Map<String, Object> request = Map.of(
                "name", "Admin " + marker,
                "email", email,
                "address", "Admin Address",
                "password", password,
                "role", "ADMIN"
        );

        MvcResult createdAdminResult = mockMvc.perform(post("/api/ecom/customer/admin/create")
                        .header("Authorization", bootstrapAdminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        CustomerResponseDTO createdAdmin = objectMapper.readValue(
                createdAdminResult.getResponse().getContentAsString(),
                CustomerResponseDTO.class
        );

        String adminBearerToken = login(email, password);
        return new AuthContext(createdAdmin.getId(), email, password, adminBearerToken);
    }

    private String login(String email, String password) throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO(email, password);

        MvcResult loginResult = mockMvc.perform(post("/api/ecom/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponseDTO authResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(),
                AuthResponseDTO.class
        );

        return "Bearer " + authResponse.getToken();
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
