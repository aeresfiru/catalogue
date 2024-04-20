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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
@RequestMapping("/catalogue/products/{productId:\\d+}")
public class ProductController {

    private final ProductClient productClient;

    @ModelAttribute("product")
    public Product product(@PathVariable("productId") int productId) {
        return this.productClient.findProduct(productId);
    }

    @GetMapping("/edit")
    public String getProductEditPage() {
        return "catalogue/products/edit";
    }

    @PostMapping("/edit")
    public String updateProduct(@PathVariable("productId") int productId, UpdateProductRequest request,
                                Model model, HttpServletResponse resp, RedirectAttributes attributes) {
        try {
            var product = this.productClient.updateProduct(request, productId);
            model.addAttribute("product", product);
            attributes.addFlashAttribute("updateMessage", "The product has been successfully updated.");
            return "redirect:/catalogue/products/%d/edit".formatted(productId);
        } catch (ClientBadRequestException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("payload", request);
            model.addAttribute("errors", ex.getErrors());
            return "catalogue/products/edit";
        }
    }

    @PostMapping("/delete")
    public String deleteProduct(@PathVariable("productId") int productId,
                                RedirectAttributes attributes) {
        this.productClient.deleteProduct(productId);
        attributes.addFlashAttribute("deleteMessage", "The product has been successfully deleted.");
        return "redirect:/catalogue/products/list";
    }
}
