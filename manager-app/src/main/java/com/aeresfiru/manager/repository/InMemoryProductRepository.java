package com.aeresfiru.manager.repository;

import com.aeresfiru.manager.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryProductRepository implements ProductRepository {

    private final List<Product> productList = Collections.synchronizedList(new LinkedList<>());

    @Override
    public List<Product> findAll() {
        return List.copyOf(productList);
    }

    @Override
    public Product save(Product product) {
        this.setId(product);
        this.productList.add(product);
        return product;
    }

    @Override
    public Optional<Product> findById(Integer id) {
        return productList.stream()
                .filter(p -> Objects.equals(p.getId(), id))
                .findFirst();
    }

    @Override
    public void deleteById(Integer id) {
        this.productList.removeIf(product -> Objects.equals(id, product.getId()));
    }

    private void setId(Product product) {
        int id = this.productList.stream()
                .max(Comparator.comparingInt(Product::getId))
                .map(Product::getId)
                .orElse(0);
        product.setId(++id);
    }
}
