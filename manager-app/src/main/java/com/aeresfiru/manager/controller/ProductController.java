package com.aeresfiru.manager.controller;

import com.aeresfiru.manager.entity.Product;
import com.aeresfiru.manager.service.ProductService;
import com.aeresfiru.manager.service.dto.UpdateProductRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@Controller
@RequestMapping(("catalogue/products/{productId:\\d+}"))
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    private final MessageSource messageSource;

    @ModelAttribute("product")
    public Product product(@PathVariable("productId") int productId) {
        return this.productService.findProduct(productId);
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
    public String updateProduct(@ModelAttribute(name = "product", binding = false) Product product,
                                @Valid UpdateProductRequest payload,
                                BindingResult bindingResult,
                                Model model) {
        if (!bindingResult.hasErrors()) {
            this.productService.updateProduct(payload, product.getId());
            return "redirect:/catalogue/products/%d".formatted(product.getId());
        }
        model.addAttribute("payload", payload);
        model.addAttribute("errors", getErrors(bindingResult));
        return "catalogue/products/edit";
    }

    private static List<String> getErrors(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .toList();
    }

    @PostMapping("/delete")
    public String deleteProduct(@ModelAttribute(name = "product") Product product) {
        this.productService.delete(product);
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
