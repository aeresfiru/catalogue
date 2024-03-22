package com.aeresfiru.manager.controller;


import com.aeresfiru.manager.client.ProductRestClient;
import com.aeresfiru.shared.request.CreateProductRequest;
import com.aeresfiru.shared.request.UpdateProductRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@RequestMapping("/catalogue/products")
public class ProductController {

    private final ProductRestClient productRestClient;

    @GetMapping("/create")
    public String getNewProductPage() {
        return "catalogue/products/new_product";
    }

    @PostMapping("/create")
    public String createProduct(CreateProductRequest request, Model model, HttpServletResponse resp) {
        var result = this.productRestClient.createProduct(request);
        if (result.isFailure()) {
            model.addAttribute("payload", request);
            model.addAttribute("problemDetail", result.getError());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "catalogue/products/new_product";
        }
        return "redirect:/catalogue/products/" + result.getValue().id();
    }

    @GetMapping("/list")
    public String getProductsList(Model model, @RequestParam(name = "filter", required = false) String filter) {
        var products = this.productRestClient.findAllProducts(filter);
        model.addAttribute("products", products);
        model.addAttribute("filter", filter);
        return "catalogue/products/list";
    }

    @GetMapping("/{productId:\\d+}")
    public String getProduct(@PathVariable int productId, Model model, HttpServletResponse resp) {
        var result = this.productRestClient.findProduct(productId);
        if (result.isFailure()) {
            return handleProductNotFound(model, resp, result.getError());
        }
        model.addAttribute("product", result.getValue());
        return "catalogue/products/product";
    }

    @GetMapping("/{productId:\\d+}/edit")
    public String getProductEditPage(@PathVariable int productId, Model model, HttpServletResponse resp) {
        var result = this.productRestClient.findProduct(productId);
        if (result.isFailure()) {
            return handleProductNotFound(model, resp, result.getError());
        }
        model.addAttribute("product", result.getValue());
        return "catalogue/products/edit";
    }

    @PostMapping("/{productId:\\d+}/edit")
    public String updateProduct(@PathVariable int productId, UpdateProductRequest request,
                                Model model, HttpServletResponse resp) {
        var product = this.productRestClient.findProduct(productId);
        if (product.isFailure()) {
            return handleProductNotFound(model, resp, product.getError());
        }
        var result = this.productRestClient.updateProduct(request, productId);
        if (result.isFailure()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("product", product.getValue());
            model.addAttribute("payload", request);
            model.addAttribute("problemDetail", result.getError());
            return "catalogue/products/edit";
        }
        model.addAttribute("product", result.getValue());
        return "redirect:/catalogue/products/%d".formatted(productId);
    }

    @PostMapping("/{productId:\\d+}/delete")
    public String deleteProduct(@PathVariable int productId, Model model) {
        var result = this.productRestClient.deleteProduct(productId);
        if (result.isFailure()) {
            model.addAttribute("problemDetail", result.getError());
        }
        return "redirect:/catalogue/products/list";
    }

    private static String handleProductNotFound(Model model, HttpServletResponse resp, ProblemDetail problemDetail) {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        model.addAttribute("problemDetail", problemDetail);
        return "errors/404";
    }
}
