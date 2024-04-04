package com.aeresfiru.catalogue.repository;

import com.aeresfiru.catalogue.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findAllByTitleLikeIgnoreCase(String title);
}
