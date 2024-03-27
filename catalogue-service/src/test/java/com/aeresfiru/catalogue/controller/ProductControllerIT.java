package com.aeresfiru.catalogue.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WithMockUser(authorities = {"SCOPE_view_catalogue", "SCOPE_edit_catalogue"})
class ProductControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("/sql/products.sql")
    void findProducts_RequestIsValid_ReturnsProductsList() throws Exception {
        // when
        mockMvc.perform(get("/catalogue-api/v1/products")
                        .param("filter", "filter"))
                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                    {"id":  1, "title":  "Product #1 filter", "details":  "Product #1 details"},
                                    {"id":  3, "title":  "Product #3 filter", "details":  "Product #3 details"}
                                ]
                                """, true)
                );
    }

    @Test
    @Sql("/sql/products.sql")
    void findProductById_ProductExists_ReturnsProduct() throws Exception {
        // when
        mockMvc.perform(get("/catalogue-api/v1/products/1"))
                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {"id": 1, "title": "Product #1 filter", "details": "Product #1 details"}
                                """, true)
                );
    }

    @Test
    void findProductById_IdNotExists_ReturnsProblemDetail() throws Exception {
        // when
        mockMvc.perform(get("/catalogue-api/v1/products/1")
                        .locale(Locale.US))
                // then
                .andExpect(status().isNotFound());
    }
//
//    @Test
//    @WithAnonymousUser
//    void findProductById_UserIsNotAuthorized_ReturnsUnauthorized() throws Exception {
//        // when
//        mockMvc.perform(get("/catalogue-api/v1/products/1"))
//                // then
//                .andExpect(status().isUnauthorized());
//    }

    @Test
    void createProduct_RequestIsValid_ReturnsNewProduct() throws Exception {
        //when
        mockMvc.perform(post("/catalogue-api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":  "Product title", "details":  "Product details"}
                                """))
                // then
                .andExpectAll(
                        status().isCreated(),
                        header().string(HttpHeaders.LOCATION, "http://localhost/catalogue-api/v1/products/1"),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "id" : 1, "title": "Product title", "details": "Product details"
                                }
                                """, true)
                );
    }

    @Test
    void createProduct_RequestIsInvalid_ReturnsProblemDetail() throws Exception {
        // when
        mockMvc.perform(post("/catalogue-api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {"title":  "", "details":  "Product details"}
                                """)
                        .locale(Locale.US))
                // then
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                    {
                                        "errors": [
                                            "Title must not be blank",
                                            "Title size must be between 3 and 50 symbols"
                                        ]
                                    }
                                """)
                );
    }

    @Test
    @WithMockUser(authorities = "SCOPE_view_catalogue")
    void createProduct_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // when
        mockMvc.perform(post("/catalogue-api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {"title":  "Product title", "details":  "Product details"}
                                """))
                // then
                .andExpect(status().isForbidden());
    }

    @Test
    @Sql("/sql/products.sql")
    void partialUpdate_RequestIsValid_ReturnsProduct() throws Exception {
        // when
        mockMvc.perform(patch("/catalogue-api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                        {"title": "Updated title"}
                                """))
                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                    {"id": 1, "title":  "Updated title", "details": "Product #1 details"}
                                """, true)
                );
    }

    @Test
    @Sql("/sql/products.sql")
    void partialUpdate_RequestIsInvalid_ReturnsProblemDetail() throws Exception {
        // when
        mockMvc.perform(patch("/catalogue-api/v1/products/1")
                        .locale(Locale.US)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {"title":  ""}
                                """))
                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                 {
                                    "errors": [
                                        "Title size must be between 3 and 50 symbols"
                                        ]
                                 }
                                """)
                );
    }

    @Test
    void partialUpdate_ProductDoesNotExists_ReturnsProblemDetail() throws Exception {
        mockMvc.perform(patch("/catalogue-api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {"title": "Updated title"}
                                """))
                // then
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
    }

    @Test
    @Sql("/sql/products.sql")
    void update_RequestIsValid_ReturnsProduct() throws Exception {
        // when
        mockMvc.perform(put("/catalogue-api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {"title": "Updated title", "details":  "Product #1 details"}
                                """))
                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                    {"id": 1, "title": "Updated title", "details": "Product #1 details"}
                                """)
                );
    }

    @Test
    @Sql("/sql/products.sql")
    void update_RequestIsInvalid_ReturnsProblemDetail() throws Exception {
        // when
        mockMvc.perform(put("/catalogue-api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {"title": "_"}
                                """)
                        .locale(Locale.US))
                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                    {
                                        "errors": [
                                            "Title size must be between 3 and 50 symbols"
                                        ]
                                    }
                                """)
                );
    }

    @Test
    void update_ProductDoesNotExist_ReturnsProblemDetail() throws Exception {
        // when
        mockMvc.perform(put("/catalogue-api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                   {"title": "Updated title"}
                                """))
                // then
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                );
    }

    @Test
    @Sql("/sql/products.sql")
    void deleteProduct_RequestIsValid_ReturnsNoContent() throws Exception {
        // when
        mockMvc.perform(delete("/catalogue-api/v1/products/1"))
                // then
                .andExpect(status().isNoContent());
    }
}
