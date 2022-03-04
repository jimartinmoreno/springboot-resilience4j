package com.javatechie.us;

import com.javatechie.us.dto.OrderDTO;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@RestController
@RequestMapping("/user-service")
@Slf4j
public class UserServiceApplication {

    @Autowired
    @Lazy
    private RestTemplate restTemplate;

    public static final String USER_SERVICE = "userService";

    private static final String BASEURL = "http://localhost:9191/orders";

    private int attempt = 1;

    /**
     * @param category
     * @return
     * @CircuitBreaker: This annotation can be applied to a class or a specific method. Applying it on a class is equivalent
     * to applying it on all its public methods. The annotation enables backend monitoring for all methods where it is applied.
     * Backend monitoring is performed via a circuit breaker.
     *
     * @Retry: This annotation can be applied to a class or a specific method. Applying it on a class is equivalent to
     * applying it on all its public methods. The annotation enables backend retry for all methods where it is applied.
     * Backend retry is performed via a retry.
     */

    @GetMapping("/displayOrders")
    // Se llama inmediatamente al fallback si falla
    // @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "getAllAvailableProducts")
    // reintenta n veces y si no puede llama al fallback si falla
    @Retry(name = USER_SERVICE, fallbackMethod = "getAllAvailableProducts")
    //    @RateLimiter(name = USER_SERVICE)
    //    @Bulkhead(name = USER_SERVICE)
    //    @TimeLimiter(name = USER_SERVICE)
    public List<OrderDTO> displayOrders(@RequestParam("category") String category) {
        log.info("displayOrders by category {}", category);
        String url = category == null ? BASEURL : BASEURL + "/" + category;
        log.info("retry method called {} times at {}", attempt, new Date());
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


    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RegistryEventConsumer<Retry> myRetryRegistryEventConsumer() {

        return new RegistryEventConsumer<Retry>() {
            @Override
            public void onEntryAddedEvent(EntryAddedEvent<Retry> entryAddedEvent) {

            }

            @Override
            public void onEntryRemovedEvent(EntryRemovedEvent<Retry> entryRemoveEvent) {

            }

            @Override
            public void onEntryReplacedEvent(EntryReplacedEvent<Retry> entryReplacedEvent) {

            }
        };
    }
}
