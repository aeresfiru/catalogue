package com.aeresfiru.catalogue.repository;

import com.aeresfiru.catalogue.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends JpaRepository<Product, Integer>,
        PagingAndSortingRepository<Product, Integer> {

    Page<Product> findAllByTitleLikeIgnoreCase(String title, Pageable pageable);
}
