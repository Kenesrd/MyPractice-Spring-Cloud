package se.magnus.microservices.composite.product.services;

import com.company.api.core.product.Product;
import com.company.api.core.product.ProductService;
import com.company.api.core.recommendation.Recommendation;
import com.company.api.core.recommendation.RecommendationService;
import com.company.api.core.review.Review;
import com.company.api.core.review.ReviewService;
import com.company.api.exceptions.InvalidInputException;
import com.company.api.exceptions.NotFoundException;
import com.company.util.http.HttpErrorInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Component
public class ProductCompositeIntegration implements ProductService, RecommendationService, ReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String productServiceUrl;
    private final String recommendationServiceUrl;
    private final String reviewServiceUrl;

    public ProductCompositeIntegration(
            RestTemplate restTemplate,
            ObjectMapper mapper,
            @Value("${app.product-service.host}") String productServiceHost,
            @Value("${app.product-service.port}") int productServicePort,
            @Value("localhost") String recommendationServiceHost,
            @Value("7002") int recommendationServicePort,
            @Value("localhost") String reviewServiceHost,
            @Value("7003") int reviewServicePort
    ) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product/";
        this.recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation?productId=";
        this.reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review?productId=";
    }

    @Override
    public Product getProduct(int productId) {

        try {
            String url = productServiceUrl + productId;
            LOG.debug("Will call getProduct API on URL: {}", url);

            Product product = restTemplate.getForObject(url, Product.class);

            LOG.debug("Found product with id: {}", product.getProductId());

            return product;

        } catch (HttpClientErrorException exception){
            switch (HttpStatus.resolve(exception.getStatusCode().value())){
                case NOT_FOUND -> throw new NotFoundException(getErrorMessage(exception));
                case UNPROCESSABLE_ENTITY -> throw new InvalidInputException(getErrorMessage(exception));
                default -> {
                    LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", exception.getStatusCode());
                    LOG.warn("Error body: {}", exception.getResponseBodyAsString());
                    throw exception;
                }
            }
        }
    }

    private String getErrorMessage(HttpClientErrorException exception){
        try {
           return mapper.readValue(exception.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioException){
            return exception.getMessage();
        }
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {
        try {
            String url = recommendationServiceUrl + productId;
            LOG.debug("Will call getRecommendations API on URL: {}", url);

            List<Recommendation> recommendations = restTemplate
                    .exchange(url, GET, null, new ParameterizedTypeReference<List<Recommendation>>() {
                    }).getBody();

            LOG.debug("Found {} recommendations for a product with id: {}", recommendations.size(), productId);
            return recommendations;

        } catch (Exception exception) {
            LOG.warn("Got an exception while requesting recommendations, return zero recommendations: {}", exception.getMessage());
            return new ArrayList<>();
        }

    }

    @Override
    public List<Review> getReviews(int productId) {

        try {
            String url = reviewServiceUrl + productId;

            List<Review> reviews = restTemplate
                    .exchange(url, GET, null, new ParameterizedTypeReference<List<Review>>() {
                    }).getBody();

            LOG.debug("Found {} reviews for product with id: {}", reviews.size(), productId);
            return reviews;
        } catch (Exception exception) {

            LOG.warn("Got an exception while requesting reviews, return zero reviews: {}", exception.getMessage());
            return new ArrayList<>();
        }
    }
}
