package com.aeresfiru.catalogue.repository;

import com.aeresfiru.catalogue.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@Sql("/sql/products.sql")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryIT {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void findAllByTitleLikeIgnoreCase_ReturnsProductList() {
        // given
        String filter = "%filter%";
        Pageable pageable = Pageable.ofSize(10);

        // when
        var products = this.productRepository.findAllByTitleLikeIgnoreCase(filter, pageable);

        // then
        assertThat(products).containsExactly(
                new Product(1, "Product #1 filter", "Product #1 details"),
                new Product(3, "Product #3 filter", "Product #3 details")
        );
    }
}
