package com.aeresfiru.manager.controller;


import com.aeresfiru.manager.client.ProductClient;
import com.aeresfiru.manager.client.exception.ClientBadRequestException;
import com.aeresfiru.manager.client.exception.ClientEntityNotFoundException;
import com.aeresfiru.manager.entity.Product;
import com.aeresfiru.shared.request.CreateProductRequest;
import com.aeresfiru.shared.request.UpdateProductRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.BindException;

@RequiredArgsConstructor
@Controller
@RequestMapping("/catalogue/products/{productId:\\d+}")
public class ProductController {

    private final ProductClient productClient;

    @ModelAttribute("product")
    public Product product(@PathVariable("productId") int productId) {
        return this.productClient.findProduct(productId);
    }

    @GetMapping
    public String getProduct() {
        return "catalogue/products/product";
    }

    @GetMapping("/edit")
    public String getProductEditPage() {
        return "catalogue/products/edit";
    }

    @PostMapping("/edit")
    public String updateProduct(@PathVariable("productId") int productId, UpdateProductRequest request,
                                Model model, HttpServletResponse resp) {
        try {
            var product = this.productClient.updateProduct(request, productId);
            model.addAttribute("product", product);
            return "redirect:/catalogue/products/%d".formatted(productId);
        } catch (ClientBadRequestException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("payload", request);
            model.addAttribute("problemDetail", ex.getProblemDetail());
            return "catalogue/products/edit";
        }
    }

    @PostMapping("/delete")
    public String deleteProduct(@PathVariable("productId") int productId) {
        this.productClient.deleteProduct(productId);
        return "redirect:/catalogue/products/list";
    }
}
