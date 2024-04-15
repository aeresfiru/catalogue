package com.aeresfiru.manager.controller;


import com.aeresfiru.manager.client.ProductClient;
import com.aeresfiru.manager.client.exception.ClientBadRequestException;
import com.aeresfiru.manager.client.payload.Product;
import com.aeresfiru.manager.client.payload.UpdateProductRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
            model.addAttribute("errors", ex.getErrors());
            return "catalogue/products/edit";
        }
    }

    @PostMapping("/delete")
    public String deleteProduct(@PathVariable("productId") int productId) {
        this.productClient.deleteProduct(productId);
        return "redirect:/catalogue/products/list";
    }
}
