package com.aeresfiru.manager.controller;

import com.aeresfiru.manager.entity.Product;
import com.aeresfiru.manager.service.ProductService;
import com.aeresfiru.manager.service.dto.CreateProductRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("catalogue/products")
public class ProductListController {

    private final ProductService productService;

    private final MessageSource messageSource;

    @GetMapping("list")
    public String getProductsList(Model model) {
        model.addAttribute("products", this.productService.findAllProducts());
        return "catalogue/products/list";
    }

    @GetMapping("create")
    public String getNewProductPage() {
        return "catalogue/products/new_product";
    }

    @PostMapping("create")
    public String createProduct(@Valid CreateProductRequest request, BindingResult bindingResult,
                                Model model) {
        if (!bindingResult.hasErrors()) {
            Product product = this.productService.createProduct(request);
            return "redirect:/catalogue/products/%d".formatted(product.getId());
        }
        model.addAttribute("payload", request);
        model.addAttribute("errors", getErrors(bindingResult));
        return "catalogue/products/new_product";
    }

    private static List<String> getErrors(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .toList();
    }
}