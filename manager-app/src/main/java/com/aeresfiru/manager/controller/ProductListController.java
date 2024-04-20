package com.aeresfiru.manager.controller;

import com.aeresfiru.manager.client.ProductClient;
import com.aeresfiru.manager.client.exception.ClientBadRequestException;
import com.aeresfiru.manager.client.payload.CreateProductRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String createProduct(CreateProductRequest request, Model model, HttpServletResponse resp, RedirectAttributes attributes) {
        try {
            this.productClient.createProduct(request);
            attributes.addFlashAttribute("createMessage", "The product has been successfully created.");
            return "redirect:/catalogue/products/list";
        } catch (ClientBadRequestException ex) {
            model.addAttribute("payload", request);
            model.addAttribute("errors", ex.getErrors());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "catalogue/products/new_product";
        }
    }

    @GetMapping("/list")
    public String getProductsList(@RequestParam(name = "page", defaultValue = "1") int page,
                                  @RequestParam(name = "size", defaultValue = "10") int size,
                                  @RequestParam(name = "filter", required = false) String filter,
                                  Model model) {
        var productPage = this.productClient.findAllProducts(filter, page, size);
        model.addAttribute("products", productPage.content())
                .addAttribute("currentPage", page)
                .addAttribute("totalPages", productPage.totalPages())
                .addAttribute("pageSize", size)
                .addAttribute("totalElements", productPage.totalElements())
                .addAttribute("filter", filter);
        return "catalogue/products/list";
    }
}
