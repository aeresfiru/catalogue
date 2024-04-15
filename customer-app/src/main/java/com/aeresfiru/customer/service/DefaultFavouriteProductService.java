package com.aeresfiru.customer.service;

import com.aeresfiru.customer.client.FavouriteProductClient;
import com.aeresfiru.customer.client.ProductClient;
import com.aeresfiru.customer.client.payload.CreateFavouriteProductRequest;
import com.aeresfiru.customer.client.payload.FavouriteProduct;
import com.aeresfiru.customer.client.payload.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultFavouriteProductService implements FavouriteProductService {

    private final FavouriteProductClient favouriteProductClient;
    private final ProductClient productClient;

    @Override
    public Mono<Void> removeProductFromFavourites(Integer productId) {
        return this.favouriteProductClient.removeProductFromFavourites(productId)
                .doOnError(ex -> log.error("Error removing product from favorites with ID: {}", productId, ex));
    }

    @Override
    public Mono<FavouriteProduct> addProductToFavourites(CreateFavouriteProductRequest request) {
        return this.favouriteProductClient.addProductToFavourites(request)
                .doOnError(ex -> log.error("Error adding product to favorites, req: {}", request, ex));
    }

    @Override
    public Mono<Boolean> isProductInFavourites(Integer productId) {
        return this.favouriteProductClient.findFavouriteProductByProductId(productId)
                .hasElement()
                .doOnError(ex -> log.error("Error checking if product is favorite with ID: {}", productId, ex));
    }

    @Override
    public Flux<Product> findAllFavouriteProducts(String filter, int page, int pageSize) {
        return this.favouriteProductClient.findAllFavouriteProducts()
                .flatMap(favouriteProduct -> this.productClient.findProduct(favouriteProduct.productId()))
                .filter(product -> isProductTitleContainsFilter(product, filter))
                .skip((long) page * pageSize)
                .take(pageSize)
                .doOnError(error -> log.error("Error retrieving favourite products", error));
    }

    private static boolean isProductTitleContainsFilter(Product product, String filter) {
        if (StringUtils.hasText(filter)) {
            return product.title().toLowerCase().contains(filter.toLowerCase());
        }
        return true;
    }
}
