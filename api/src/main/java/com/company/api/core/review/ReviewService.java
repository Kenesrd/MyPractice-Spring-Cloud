package com.company.api.core.review;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ReviewService {
    /**
     * Sample usage: "curl $HOST/$PORT/review?productId=1"
     *
     * @param productId Id of the product
     * @return the review of the product
     */

    @GetMapping(
            value = "/review",
            produces = "application/json"
    )
    List<Review> getReviews(@RequestParam int productId);
}
