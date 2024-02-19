package se.magnus.microservices.core.recommendation.services;

import com.company.api.core.recommendation.Recommendation;
import com.company.api.core.recommendation.RecommendationService;
import com.company.api.exceptions.InvalidInputException;
import com.company.api.exceptions.NotFoundException;
import com.company.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RecommendationServiceImpl implements RecommendationService {

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    private final ServiceUtil serviceUtil;

    @Autowired
    public RecommendationServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        if (productId == 113) {
            LOG.debug("No recommendations found for productId: {} ", productId);
            return new ArrayList<>();
        }
        List<Recommendation> list = new ArrayList<>();
        list.add(new Recommendation(productId, 1, "Author-1",1,"content-1", serviceUtil.getServiceAddress()));
        list.add(new Recommendation(productId, 2, "Author-2",2,"content-2", serviceUtil.getServiceAddress()));
        list.add(new Recommendation(productId, 3, "Author-3",3,"content-3", serviceUtil.getServiceAddress()));

        LOG.debug("/recommendation reponse size: {}", list.size());

        return list;
    }
}
