package com.aeresfiru.feedback.service;

import com.aeresfiru.feedback.entity.FavouriteProduct;
import com.aeresfiru.feedback.repository.FavouriteProductRepository;
import com.aeresfiru.feedback.service.dto.CreateFavouriteProductRequest;
import com.aeresfiru.feedback.service.mapper.FavouriteProductMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavouriteProductServiceImplTest {

    @Mock
    FavouriteProductRepository favouriteProductRepository;

    @Spy
    FavouriteProductMapper mapper;

    @InjectMocks
    FavouriteProductServiceImpl service;

    @Test
    void addProductToFavourites_ReturnsCreatedFavouriteProduct() {
        // given
        var request = new CreateFavouriteProductRequest(1);

        doAnswer(invocation -> Mono.justOrEmpty(invocation.getArguments()[0]))
                .when(this.favouriteProductRepository).save(any());

        // when
        StepVerifier.create(this.service.addProductToFavourites(request, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"))
                // then
                .expectNextMatches(favouriteProduct -> favouriteProduct.getProductId() == 1
                        && favouriteProduct.getUserId().equals("5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")
                        && favouriteProduct.getId() != null)
                .verifyComplete();
    }

    @Test
    void deleteByProductIdAndUserId_ReturnsEmptyMono() {
        // given
        var userId = "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c";
        int productId = 1;

        doReturn(Mono.empty()).when(this.favouriteProductRepository).deleteByProductIdAndUserId(productId, userId);

        // when
        StepVerifier.create(this.service.removeProductFromFavourites(productId, userId))
                // then
                .verifyComplete();

        verify(this.favouriteProductRepository).deleteByProductIdAndUserId(productId, userId);
    }

    @Test
    void findFavouriteProductByProduct_ReturnsFavouriteProduct() {
        // given
        var favouriteProductId = "2dbcec0b-686a-4a96-be5a-795b4de19872";
        int productId = 1;
        var userId = "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c";
        doReturn(Mono.just(new FavouriteProduct(UUID.fromString(favouriteProductId), productId, userId)))
                .when(this.favouriteProductRepository)
                .findByProductIdAndUserId(productId, userId);

        // when
        StepVerifier.create(this.service.findFavouriteProductByProduct(productId, userId))
                // then
                .expectNext(new FavouriteProduct(UUID.fromString(favouriteProductId), productId, userId))
                .verifyComplete();
    }

    @Test
    void findFavouriteProducts_ReturnsFavouriteProductList() {
        // given
        doReturn(Flux.fromIterable(List.of(
                new FavouriteProduct(UUID.fromString("7c2f09af-9678-4744-91fa-77f2386361fd"), 1,
                        "2cb58cb6-5524-4c54-bd85-51b6ca5c9913"),
                new FavouriteProduct(UUID.fromString("b54c372f-499a-471c-9307-6f445c65b35f"), 1,
                        "2cb58cb6-5524-4c54-bd85-51b6ca5c9913")
        ))).when(this.favouriteProductRepository).findAllByUserId("2cb58cb6-5524-4c54-bd85-51b6ca5c9913");

        // when
        StepVerifier.create(this.service.findFavouriteProducts("2cb58cb6-5524-4c54-bd85-51b6ca5c9913"))
                // then
                .expectNext(
                        new FavouriteProduct(UUID.fromString("7c2f09af-9678-4744-91fa-77f2386361fd"), 1,
                                "2cb58cb6-5524-4c54-bd85-51b6ca5c9913"),
                        new FavouriteProduct(UUID.fromString("b54c372f-499a-471c-9307-6f445c65b35f"), 1,
                                "2cb58cb6-5524-4c54-bd85-51b6ca5c9913"))
                .verifyComplete();
    }
}
