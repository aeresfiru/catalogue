package com.aeresfiru.manager.controller;

import com.aeresfiru.manager.client.ProductClient;
import com.aeresfiru.manager.client.exception.ClientBadRequestException;
import com.aeresfiru.shared.request.CreateProductRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@RequestMapping("/catalogue/products")
public class ProductListController {

    private final ProductClient productClient;

    @GetMapping("/create")
    public String getNewProductPage() {
        return "catalogue/products/new_product";
    }

    @PostMapping("/create")
    public String createProduct(CreateProductRequest request, Model model, HttpServletResponse resp) {
        try {
            var result = this.productClient.createProduct(request);
            return "redirect:/catalogue/products/" + result.id();
        } catch (ClientBadRequestException ex) {
            model.addAttribute("payload", request);
            model.addAttribute("problemDetail", ex.getProblemDetail());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "catalogue/products/new_product";
        }
    }

    @GetMapping("/list")
    public String getProductsList(Model model, @RequestParam(name = "filter", required = false) String filter) {
        var products = this.productClient.findAllProducts(filter);
        model.addAttribute("products", products);
        model.addAttribute("filter", filter);
        return "catalogue/products/list";
    }
}
