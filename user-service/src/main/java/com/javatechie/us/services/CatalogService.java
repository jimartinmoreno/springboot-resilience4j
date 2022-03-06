package com.javatechie.us.services;

import com.javatechie.us.dto.OrderDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class CatalogService {

    @Lazy
    private final RestTemplate restTemplate;

    public static final String USER_SERVICE = "userService";

    private static final String BASEURL = "http://localhost:9191/orders";

    public CatalogService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * @param category
     * @return
     * @CircuitBreaker: This annotation can be applied to a class or a specific method. Applying it on a class is equivalent
     * to applying it on all its public methods. The annotation enables backend monitoring for all methods where it is applied.
     * Backend monitoring is performed via a circuit breaker.
     * @Retry: This annotation can be applied to a class or a specific method. Applying it on a class is equivalent to
     * applying it on all its public methods. The annotation enables backend retry for all methods where it is applied.
     * Backend retry is performed via a retry.
     */

    // The annotation enables throttling for all methods where it is applied. Throttling monitoring is performed via a rate limiter.
    // @RateLimiter(name = USER_SERVICE)

    // @Bulkhead(name = USER_SERVICE, fallbackMethod = "getAllAvailableProducts")

    // The annotation enables time limiter for all methods where it is applied.
    // @TimeLimiter(name = USER_SERVICE, fallbackMethod = "getAllAvailableProducts")
    // Se llama inmediatamente al fallback si falla
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "getAllAvailableProducts")
    // reintenta n veces y si no puede llama al fallback si falla
    @Retry(name = USER_SERVICE, fallbackMethod = "getAllAvailableProducts")
    public List<OrderDTO> getOrdersByCategory(String category) {
        log.info("getOrdersByCategory by category {}", category);
        String url = category == null ? BASEURL : BASEURL + "/" + category;
        return restTemplate.getForObject(url, ArrayList.class);
        // return restTemplate.exchange(url, HttpMethod.GET, null, List.class).getBody();
    }

    public List<OrderDTO> getAllAvailableProducts(Exception e) {
        log.info("Fallback getAllAvailableProducts {}", e.getMessage());
        return Stream.of(
                        new OrderDTO(119, "LED TV", "electronics", "white", 45000),
                        new OrderDTO(345, "Headset", "electronics", "black", 7000),
                        new OrderDTO(475, "Sound bar", "electronics", "black", 13000),
                        new OrderDTO(574, "Puma Shoes", "foot wear", "black & white", 4600),
                        new OrderDTO(678, "Vegetable chopper", "kitchen", "blue", 999),
                        new OrderDTO(532, "Oven Gloves", "kitchen", "gray", 745)
                )
                .collect(Collectors.toList());
    }
}
