package com.aeresfiru.manager.controller;

import com.aeresfiru.manager.client.payload.Product;
import com.aeresfiru.manager.client.payload.UpdateProductRequest;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WireMockTest(httpPort = 54321)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WithMockUser(username = "j.daniels", roles = "MANAGER")
class ProductControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getProductEditPage_RequestIsValid_ReturnsProductEditPage() throws Exception {
        // given
        WireMock.stubFor(WireMock.get("/catalogue-api/v1/products/1")
                .willReturn(WireMock.ok()
                        .withBody("""
                                {"id": 1, "title": "title", "details": "details"}
                                """)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        // when
        this.mockMvc.perform(MockMvcRequestBuilders.get("/catalogue/products/1/edit"))
                // then
                .andExpectAll(
                        status().isOk(),
                        view().name("catalogue/products/edit"),
                        model().attribute("product", new Product(1, "title", "details"))
                );

        WireMock.verify(WireMock.getRequestedFor(WireMock.urlPathMatching("/catalogue-api/v1/products/1")));
    }

    @Test
    void getProductEditPage_RequestIsValid_ReturnsNotFound() throws Exception {
        // given
        WireMock.stubFor(WireMock.get("/catalogue-api/v1/products/1")
                .willReturn(WireMock.notFound()
                        .withBody("""
                                {"title": "title", "detail": "detail"}
                                """)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)));

        // when
        this.mockMvc.perform(MockMvcRequestBuilders.get("/catalogue/products/1/edit"))
                // then
                .andExpectAll(
                        status().isNotFound(),
                        view().name("errors/404")
                );

        WireMock.verify(WireMock.getRequestedFor(WireMock.urlPathMatching("/catalogue-api/v1/products/1")));
    }

    @Test
    void updateProduct_RequestIsValid_RedirectsToProductPage() throws Exception {
        // given
        WireMock.stubFor(WireMock.get("/catalogue-api/v1/products/1")
                .willReturn(WireMock.ok()
                        .withBody("""
                                {"id": 1, "title": "title", "details": "details" }
                                """)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        WireMock.stubFor(WireMock.patch("/catalogue-api/v1/products/1")
                .withRequestBody(WireMock.equalToJson("""
                        {"title": "New title", "details": "New details"}
                        """))
                .willReturn(WireMock.ok()
                        .withBody("""
                                {"id": 1, "title": "New title", "details": "New details"}
                                """)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        // when
        this.mockMvc.perform(MockMvcRequestBuilders.post("/catalogue/products/1/edit")
                        .param("title", "New title")
                        .param("details", "New details")
                        .with(csrf()))
                // then
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/catalogue/products/1/edit")
                );

        WireMock.verify(WireMock.patchRequestedFor(WireMock.urlPathMatching("/catalogue-api/v1/products/1"))
                .withRequestBody(WireMock.equalToJson("""
                        {"title": "New title", "details": "New details"}
                        """)));
    }

    @Test
    void updateProduct_RequestIsInvalid_ReturnsProductEditPage() throws Exception {
        // given
        WireMock.stubFor(WireMock.get("/catalogue-api/v1/products/1")
                .willReturn(WireMock.ok()
                        .withBody("""
                                {"id": 1, "title": "title", "details": "details" }
                                """)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        WireMock.stubFor(WireMock.patch("/catalogue-api/v1/products/1")
                .withRequestBody(WireMock.equalToJson("""
                        { "title": "", "details": null }
                        """))
                .willReturn(WireMock.badRequest()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                        .withBody("""
                                { "errors": ["Error #1", "Error #2"] }
                                """)));

        // when
        this.mockMvc.perform(MockMvcRequestBuilders.post("/catalogue/products/1/edit")
                        .param("title", "")
//                        .param("details", "")
                        .with(csrf()))
                // then
                .andExpectAll(
                        status().isBadRequest(),
                        view().name("catalogue/products/edit"),
                        model().attribute("product", new Product(1, "title", "details")),
                        model().attributeExists("errors"),
                        model().attribute("payload", new UpdateProductRequest("", null))
                );

        WireMock.verify(WireMock.patchRequestedFor(WireMock.urlPathMatching("/catalogue-api/v1/products/1"))
                .withRequestBody(WireMock.equalToJson("""
                        { "title": "", "details": null }
                        """)));
    }

    @Test
    void updateProduct_ProductDoesNotExist_ReturnsError404Page() throws Exception {
        // given
        WireMock.stubFor(WireMock.get("/catalogue-api/v1/products/1")
                .willReturn(WireMock.ok()
                        .withBody("""
                                {"id": 1, "title": "title", "details": "details" }
                                """)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        WireMock.stubFor(WireMock.patch("/catalogue-api/v1/products/1")
                .willReturn(WireMock.notFound()
                        .withBody("""
                                {"title": "title", "detail": "detail"}
                                """)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)));

        // when
        this.mockMvc.perform(MockMvcRequestBuilders.post("/catalogue/products/1/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("title", "title")
                        .param("details", "details")
                        .with(csrf()))
                // then
                .andExpectAll(
                        status().isNotFound(),
                        view().name("errors/404"),
                        model().attributeExists("problemDetail")
                );
    }

    @Test
    void deleteProduct_ProductExists_RedirectsToProductsListPage() throws Exception {
        // given
        WireMock.stubFor(WireMock.get("/catalogue-api/v1/products/1")
                .willReturn(WireMock.okJson("""
                        {
                            "id": 1,
                            "title": "title",
                            "details": "details"
                        }
                        """)));

        WireMock.stubFor(WireMock.delete("/catalogue-api/v1/products/1")
                .willReturn(WireMock.noContent()));

        // when
        this.mockMvc.perform(MockMvcRequestBuilders.post("/catalogue/products/1/delete")
                        .with(csrf()))
                // then
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/catalogue/products/list")
                );

        WireMock.verify(WireMock.deleteRequestedFor(WireMock.urlPathMatching("/catalogue-api/v1/products/1")));
    }
}
