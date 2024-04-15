package com.aeresfiru.manager.controller;

import com.aeresfiru.manager.client.payload.CreateProductRequest;
import com.aeresfiru.manager.client.payload.Product;
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

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WireMockTest(httpPort = 54321)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WithMockUser(username = "j.daniels", roles = "MANAGER")
public class ProductListControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getNewProductPage_ReturnsProductPage() throws Exception {
        // when
        this.mockMvc.perform(MockMvcRequestBuilders.get("/catalogue/products/create"))
                // then
                .andExpectAll(
                        status().isOk(),
                        view().name("catalogue/products/new_product")
                );
    }

    @Test
    @WithMockUser(username = "j.daniels")
    void getNewProductPage_UnauthorizedUser_ReturnsForbidden() throws Exception {
        // when
        this.mockMvc.perform(MockMvcRequestBuilders.get("/catalogue/products/create"))
                // then
                .andExpect(status().isForbidden());
    }

    @Test
    void createProduct_RequestIsValid_ToProductPage() throws Exception {
        // given
        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/catalogue-api/v1/products"))
                .withRequestBody(equalToJson("""
                            {"title": "product", "details": "details"}
                        """))
                .willReturn(WireMock.created()
//                        .withHeader(HttpHeaders.LOCATION, "/catalogue-api/v1/products/1")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "id": 1,
                                    "title": "product",
                                    "details": "details"
                                }
                                """)));


        this.mockMvc.perform(MockMvcRequestBuilders.post("/catalogue/products/create")
                        .param("title", "product")
                        .param("details", "details")
                        .with(csrf()))
                // then
                .andExpectAll(
                        status().is3xxRedirection(),
                        header().string(HttpHeaders.LOCATION, "/catalogue/products/1")
                );

        WireMock.verify(WireMock.postRequestedFor(WireMock.urlPathMatching("/catalogue-api/v1/products"))
                .withRequestBody(WireMock.equalToJson("""
                        {
                            "title": "product",
                            "details": "details"
                        }""")));
    }

    @Test
    void createProduct_RequestIsInvalid_ReturnsProblemDetails() throws Exception {
        // given
        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/catalogue-api/v1/products"))
                .withRequestBody(WireMock.equalToJson("""
                        {"title": "", "details": "details"}
                        """))
                .willReturn(WireMock.badRequest()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                        .withBody("""
                                {
                                    "errors": ["Title size cannot be empty"]
                                }
                                """)));

        // when
        this.mockMvc.perform(MockMvcRequestBuilders.post("/catalogue/products/create")
                        .param("title", "")
                        .param("details", "details")
                        .with(csrf()))
                // then
                .andExpectAll(
                        status().isBadRequest(),
                        view().name("catalogue/products/new_product"),
                        model().attribute("payload", new CreateProductRequest("", "details")),
                        model().attributeExists("errors"),
                        header().doesNotExist(HttpHeaders.LOCATION)
                );

        WireMock.verify(WireMock.postRequestedFor(WireMock.urlPathMatching("/catalogue-api/v1/products"))
                .withRequestBody(WireMock.equalToJson("""
                        {"title": "", "details": "details"}
                        """)));
    }

    @Test
    void getProductsListPage_ReturnsProductListPage() throws Exception {
        // given
        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/catalogue-api/v1/products"))
                .withQueryParam("filter", WireMock.equalTo("Product"))
                .willReturn(WireMock.ok("""
                                [
                                    {"id": 1, "title": "Product #1", "details": "Product #1 details"},
                                    {"id": 2, "title": "Product #2", "details": "Product #2 details"}
                                ]""")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/catalogue/products/list")
                        .param("filter", "Product"))
                //then
                .andExpectAll(
                        status().isOk(),
                        view().name("catalogue/products/list"),
                        model().attribute("products", List.of(
                                new Product(1, "Product #1", "Product #1 details"),
                                new Product(2, "Product #2", "Product #2 details")
                        )),
                        model().attribute("filter", "Product")
                );

        WireMock.verify(WireMock.getRequestedFor(WireMock.urlPathMatching("/catalogue-api/v1/products"))
                .withQueryParam("filter", WireMock.equalTo("Product")));
    }
}
