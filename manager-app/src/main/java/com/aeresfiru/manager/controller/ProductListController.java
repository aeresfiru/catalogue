package com.aeresfiru.manager.controller;

import com.aeresfiru.manager.client.ProductRestClient;
import com.aeresfiru.shared.request.CreateProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/catalogue/products")
public class ProductListController {

    private final ProductRestClient productRestClient;

    private final MessageSource messageSource;

    @GetMapping("create")
    public String getNewProductPage() {
        return "catalogue/products/new_product";
    }

    @PostMapping("create")
    public String createProduct(CreateProductRequest request, Model model) {
        var result = this.productRestClient.createProduct(request);

        if (result.isFailure()) {
            model.addAttribute("payload", request);
            if (result.getError().getStatus() == HttpStatus.BAD_REQUEST.value()) {
                model.addAttribute("errors", result.getError().getProperties().get("errors"));
            } else {
                model.addAttribute("error", result.getError().getTitle());
            }
            return "catalogue/products/new_product";
        }
        return "redirect:/catalogue/products/%d".formatted(result.getValue().id());
    }

    @GetMapping("/list")
    public String getProductsList(Model model) {
        var products = this.productRestClient.findAllProducts();
        model.addAttribute("products", products);
        return "catalogue/products/list";
    }
}
