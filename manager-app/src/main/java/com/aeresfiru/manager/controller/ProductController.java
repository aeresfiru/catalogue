package com.aeresfiru.manager.controller;

import com.aeresfiru.manager.client.ProductRestClient;
import com.aeresfiru.manager.entity.Product;
import com.aeresfiru.shared.request.UpdateProductRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.NoSuchElementException;

@Controller
@RequestMapping(("catalogue/products/{productId:\\d+}"))
@RequiredArgsConstructor
public class ProductController {

    private final ProductRestClient productRestClient;

    private final MessageSource messageSource;

    @ModelAttribute("product")
    public Product product(@PathVariable("productId") int productId) {
        var result = this.productRestClient.findProduct(productId);
        if (result.isFailure()) {
            throw new NoSuchElementException("catalogue.errors.product_not_found");
        }
        return result.getValue();
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
    public String updateProduct(@PathVariable("productId") int productId,
                                UpdateProductRequest request,
                                Model model) {
        var result = this.productRestClient.updateProduct(request, productId);
        if (result.isFailure()) {
            model.addAttribute("payload", request);
            if (result.getError().getStatus() == HttpStatus.BAD_REQUEST.value()) {
                model.addAttribute("errors", result.getError().getProperties().get("errors"));
            } else {
                model.addAttribute("problemDetail", result.getError());
            }
            return "catalogue/products/edit";
        }
        model.addAttribute("product", result.getValue());
        return "redirect:/catalogue/products/%d".formatted(productId);
    }

    @PostMapping("/delete")
    public String deleteProduct(@PathVariable Integer productId, Model model) {
        var result = this.productRestClient.deleteProduct(productId);
        if (result.isFailure()) {
            model.addAttribute("error", result.getError().getTitle());
        }
        return "redirect:/catalogue/products/list";
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException ex, Model model,
                                               HttpServletResponse response, Locale locale) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", this.messageSource.getMessage(ex.getMessage(),
                new Object[0], ex.getMessage(), locale));
        return "errors/404";
    }
}
