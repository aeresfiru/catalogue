package com.aeresfiru.feedback.service;

import com.aeresfiru.feedback.entity.FavouriteProduct;
import com.aeresfiru.feedback.repository.FavouriteProductRepository;
import com.aeresfiru.feedback.service.dto.CreateFavouriteProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FavouriteProductServiceImpl implements FavouriteProductService {

    private final FavouriteProductRepository repository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public Mono<FavouriteProduct> addProductToFavourites(CreateFavouriteProductRequest request, String userId) {
        return this.mapToFavouriteProduct(request, userId).flatMap(this.repository::save);
    }

    @Override
    public Mono<Void> removeProductFromFavourites(int productId, String userId) {
        return this.repository.deleteByProductIdAndUserId(productId, userId);
    }

    @Override
    public Mono<PageImpl<FavouriteProduct>> findFavouriteProducts(String userId, Pageable pageable) {
        return this.repository.findAllByUserId(userId, pageable)
                .collectList()
                .zipWith(this.repository.countAllByUserId(userId))
                .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

    @Override
    public Mono<FavouriteProduct> findFavouriteProductByProduct(Integer productId, String userId) {
        return this.repository.findByProductIdAndUserId(productId, userId);
    }

    @Override
    public Mono<Long> countAll() {
        return this.repository.count();
    }

    private Mono<FavouriteProduct> mapToFavouriteProduct(CreateFavouriteProductRequest req, String userId) {
        return Mono.just(new FavouriteProduct(UUID.randomUUID(), req.productId(), userId));
    }
}
