package com.aeresfiru.feedback.controller;

import com.aeresfiru.feedback.controller.resource.FavouriteProductResource;
import com.aeresfiru.feedback.controller.resource.FavouriteProductResourceAssembler;
import com.aeresfiru.feedback.entity.FavouriteProduct;
import com.aeresfiru.feedback.service.FavouriteProductService;
import com.aeresfiru.feedback.service.dto.CreateFavouriteProductRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FavouriteProductControllerTest {

    @Mock
    FavouriteProductService favouriteProductService;

    @Spy
    FavouriteProductResourceAssembler favouriteProductResourceAssembler;

    @InjectMocks
    FavouriteProductController controller;

    @Test
    void findFavouriteProducts_ReturnsFavouriteProducts() {
        // given
        String favouriteProductId_1 = "7c2f09af-9678-4744-91fa-77f2386361fd";
        String favouriteProductId_2 = "b54c372f-499a-471c-9307-6f445c65b35f";
        String userId = "fe5b0b92-6212-4356-9a52-5f438e747b2a";
        var token = new JwtAuthenticationToken(
                Jwt.withTokenValue("e30.e30")
                        .headers(headers -> headers.put("foo", "bar"))
                        .claim("sub", userId)
                        .build()
        );

        doReturn(Flux.fromIterable(List.of(
                new FavouriteProduct(UUID.fromString(favouriteProductId_1), 1, userId),
                new FavouriteProduct(UUID.fromString(favouriteProductId_2), 3, userId)))
        ).when(this.favouriteProductService).findFavouriteProducts(userId);

        // when
        StepVerifier.create(this.controller.findFavouriteProducts(Mono.just(token)))
                // then
                .expectNext(
                        new FavouriteProductResource(favouriteProductId_1, 1, userId),
                        new FavouriteProductResource(favouriteProductId_2, 3, userId)
                ).verifyComplete();

        verify(this.favouriteProductService).findFavouriteProducts(userId);
        verifyNoMoreInteractions(this.favouriteProductService);
    }

    @Test
    void addProductToFavourites_ReturnsCreatedFavouriteProduct() {
        // given
        String favouriteProductId = "7c2f09af-9678-4744-91fa-77f2386361fd";
        String userId = "fe5b0b92-6212-4356-9a52-5f438e747b2a";
        var token = new JwtAuthenticationToken(
                Jwt.withTokenValue("e30.e30")
                        .headers(headers -> headers.put("foo", "bar"))
                        .claim("sub", userId)
                        .build()
        );

        doReturn(Mono.just(new FavouriteProduct(UUID.fromString(favouriteProductId), 1, userId)))
                .when(this.favouriteProductService)
                .addProductToFavourites(new CreateFavouriteProductRequest(1), userId);

        // when
        StepVerifier.create(this.controller.addProductToFavourites(
                        Mono.just(new CreateFavouriteProductRequest(1)),
                        Mono.just(token),
                        UriComponentsBuilder.fromUriString("http://localhost")))
                // then
                .expectNext(ResponseEntity
                        .created(URI.create("http://localhost/feedback-api/v1/favourite-products/" + favouriteProductId))
                        .body(new FavouriteProductResource(favouriteProductId, 1, userId))
                )
                .verifyComplete();

        verify(this.favouriteProductService)
                .addProductToFavourites(new CreateFavouriteProductRequest(1), userId);
        verifyNoMoreInteractions(this.favouriteProductService);
    }

    @Test
    void removeProductFromFavourites_ReturnsNoContent() {
        // given
        String userId = "fe5b0b92-6212-4356-9a52-5f438e747b2a";
        var token = new JwtAuthenticationToken(
                Jwt.withTokenValue("e30.e30")
                        .headers(headers -> headers.put("foo", "bar"))
                        .claim("sub", userId)
                        .build()
        );
        doReturn(Mono.empty()).when(this.favouriteProductService)
                .removeProductFromFavourites(1, userId);

        // when
        StepVerifier.create(this.controller.removeProductFromFavourites(1, Mono.just(token)))
                // then
                .expectNext(ResponseEntity.noContent().build())
                .verifyComplete();

        verify(this.favouriteProductService).removeProductFromFavourites(1, userId);
    }
}
