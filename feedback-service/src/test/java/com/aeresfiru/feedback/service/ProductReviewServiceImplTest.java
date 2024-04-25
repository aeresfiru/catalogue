package com.aeresfiru.feedback.service;

import com.aeresfiru.feedback.entity.ProductReview;
import com.aeresfiru.feedback.repository.ProductReviewRepository;
import com.aeresfiru.feedback.service.dto.CreateProductReviewRequest;
import com.aeresfiru.feedback.service.mapper.ProductReviewMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ProductReviewServiceImplTest {

    @Mock
    ProductReviewRepository productReviewRepository;

    @Spy
    ProductReviewMapper mapper;

    @InjectMocks
    ProductReviewServiceImpl service;

    @Test
    void createProductReview_ReturnsCreatedProductReview() {
        // given
        var request = new CreateProductReviewRequest(1, 5, "review");
        var userId = "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c";

        doAnswer(invocation -> Mono.justOrEmpty(invocation.getArguments()[0])).when(this.productReviewRepository)
                .save(any());

        // when
        StepVerifier.create(this.service.createProductReview(request, userId))
                // then
                .expectNextMatches(productReview -> productReview.getProductId() == 1
                        && productReview.getRating() == 5
                        && productReview.getUserId().equals(userId)
                        && productReview.getReview().equals("review")
                        && productReview.getId() != null)
                .verifyComplete();
    }

    @Test
    void findProductReviewsByProduct_ReturnsProductReviews() {
        // given
        doReturn(Flux.fromIterable(List.of(
                new ProductReview(UUID.fromString("2dbcec0b-686a-4a96-be5a-795b4de19872"), 1, 5,
                        "review#1", "904c6170-f88b-487c-bd1a-4380a637276e"),
                new ProductReview(UUID.fromString("b45be946-6e85-43f8-bcc8-c6927f382d36"), 1, 4,
                        "review#2", "b45be946-6e85-43f8-bcc8-c6927f382d36"),
                new ProductReview(UUID.fromString("ddca1b37-6d92-49aa-be1a-ad9e1d7dd3ec"), 3, 5,
                        "review#3", "904c6170-f88b-487c-bd1a-4380a637276e")
        ))).when(this.productReviewRepository).findAllByProductId(1, PageRequest.of(0, 10));
        doReturn(Mono.just(2L)).when(this.productReviewRepository).countByProductId(1);

        // when
        StepVerifier.create(this.service.findAllProductReviews(1, PageRequest.of(0, 10)))
                // then
                .expectNext(new PageImpl<>(List.of(
                        new ProductReview(UUID.fromString("2dbcec0b-686a-4a96-be5a-795b4de19872"), 1, 5,
                                "review#1", "904c6170-f88b-487c-bd1a-4380a637276e"),
                        new ProductReview(UUID.fromString("b45be946-6e85-43f8-bcc8-c6927f382d36"), 1, 4,
                                "review#2", "b45be946-6e85-43f8-bcc8-c6927f382d36"),
                        new ProductReview(UUID.fromString("ddca1b37-6d92-49aa-be1a-ad9e1d7dd3ec"), 3, 5,
                                "review#3", "904c6170-f88b-487c-bd1a-4380a637276e")),
                        PageRequest.of(0, 10), 3)
                )
                .verifyComplete();
    }
}
