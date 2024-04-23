package com.aeresfiru.catalogue.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.HeadersModifyingOperationPreprocessor;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WithMockUser(authorities = {"SCOPE_view_catalogue", "SCOPE_edit_catalogue"})
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
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
                                {
                                    content: [
                                        {"id":  1, "title":  "Product #1 filter", "details":  "Product #1 details"},
                                        {"id":  3, "title":  "Product #3 filter", "details":  "Product #3 details"}
                                    ]
                                }"""))
                .andDo(document("catalogue/products/find_all",
                        preprocessResponse(prettyPrint(), new HeadersModifyingOperationPreprocessor().remove("Vary")),
                        relaxedResponseFields(
                                fieldWithPath("content").description("Response content").type("list"),
                                fieldWithPath("pageable").description("Core information about page").type(Pageable.class),
                                fieldWithPath("sort").description("Sorting information").type(Sort.class)
                        )
                ));
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
                                {
                                    "id": 1,
                                    "title": "Product #1 filter",
                                    "details": "Product #1 details"
                                }""")
                ).andDo(document("catalogue/products/find_by_id",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("Product ID"),
                                fieldWithPath("title").description("Product title"),
                                fieldWithPath("details").description("Product details")
                        )
                ));
    }

    @Test
    void findProductById_IdNotExists_ReturnsProblemDetail() throws Exception {
        // when
        mockMvc.perform(get("/catalogue-api/v1/products/1")
                        .locale(Locale.US))
                // then
                .andExpect(status().isNotFound())
                .andDo(document("catalogue/products/find_by_id_not_exists",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("type").description("RFC-7807 problem type"),
                                fieldWithPath("title").description("Error title"),
                                fieldWithPath("status").description("HTTP status code of the error"),
                                fieldWithPath("detail").description("Error message"),
                                fieldWithPath("instance").description("Request path")
                        )
                ));
    }

    @Test
    void createProduct_RequestIsValid_ReturnsNewProduct() throws Exception {
        //when
        mockMvc.perform(post("/catalogue-api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Product title",
                                    "details": "Product details"
                                }
                                """))
                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "id" : 1,
                                    "title": "Product title",
                                    "details": "Product details"
                                }
                                """)
                ).andDo(document("catalogue/products/create",
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").description("Product title"),
                                fieldWithPath("details").description("Product details")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Product ID"),
                                fieldWithPath("title").description("Product title"),
                                fieldWithPath("details").description("Product details")
                        )
                ));
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
                ).andDo(document("catalogue/products/create_invalid_request",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("type").description("RFC-7807 problem type"),
                                fieldWithPath("title").description("Error title"),
                                fieldWithPath("status").description("HTTP status code of the error"),
                                fieldWithPath("detail").description("Error message"),
                                fieldWithPath("instance").description("Request path"),
                                fieldWithPath("errors").description("Invalid fields error messages")
                        )
                ));
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
                .andExpect(status().isForbidden())
                .andDo(document("catalogue/products/create_unauthorized",
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    @Sql("/sql/products.sql")
    void update_RequestIsValid_ReturnsProduct() throws Exception {
        // when
        mockMvc.perform(patch("/catalogue-api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Updated title"
                                }"""))
                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "id": 1,
                                    "title": "Updated title",
                                    "details": "Product #1 details"
                                }""")
                ).andDo(document("catalogue/products/update",
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").description("Updated product title")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Product ID"),
                                fieldWithPath("title").description("Product title"),
                                fieldWithPath("details").description("Product details")
                        )
                ));
    }

    @Test
    @Sql("/sql/products.sql")
    void update_RequestIsInvalid_ReturnsProblemDetail() throws Exception {
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
                ).andDo(document("catalogue/products/update_invalid_request",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("type").description("RFC-7807 problem type"),
                                fieldWithPath("title").description("Error title"),
                                fieldWithPath("status").description("HTTP status code of the error"),
                                fieldWithPath("detail").description("Error message"),
                                fieldWithPath("instance").description("Request path"),
                                fieldWithPath("errors").description("Invalid fields error messages")
                        )
                ));
    }

    @Test
    void update_ProductDoesNotExists_ReturnsProblemDetail() throws Exception {
        mockMvc.perform(patch("/catalogue-api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {"title": "Updated title"}
                                """))
                // then
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                ).andDo(document("catalogue/products/update_not_found",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("type").description("RFC-7807 problem type"),
                                fieldWithPath("title").description("Error title"),
                                fieldWithPath("status").description("HTTP status code of the error"),
                                fieldWithPath("detail").description("Error message"),
                                fieldWithPath("instance").description("Request path")
                        )
                ));
    }

    @Test
    @Sql("/sql/products.sql")
    void deleteProduct_RequestIsValid_ReturnsNoContent() throws Exception {
        // when
        mockMvc.perform(delete("/catalogue-api/v1/products/1"))
                // then
                .andExpect(status().isNoContent())
                .andDo(document("catalogue/products/delete",
                        preprocessResponse(prettyPrint())
                ));
    }
}
