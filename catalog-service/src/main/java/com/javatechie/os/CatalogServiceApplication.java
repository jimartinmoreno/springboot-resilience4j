package com.javatechie.os;

import com.javatechie.os.entity.Order;
import com.javatechie.os.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class CatalogServiceApplication {

    @Autowired
    private OrderRepository orderRepository;

    /*
        The PostConstruct annotation is used on a method that needs to be executed after dependency injection is done to
        perform any initialization. This method must be invoked before the class is put into service.
    */
    @PostConstruct
    public void initOrdersTable() {
        orderRepository.saveAll(
                Stream.of(
                                new Order("mobile", "electronics", "white", 20000),
                                new Order("T-Shirt", "clothes", "black", 999),
                                new Order("Jeans", "clothes", "blue", 1999),
                                new Order("Laptop", "electronics", "gray", 50000),
                                new Order("digital watch", "electronics", "black", 2500),
                                new Order("Fan", "electronics", "black", 50000)
                        )
                        .collect(Collectors.toList()));
    }

    public static void main(String[] args) {
        SpringApplication.run(CatalogServiceApplication.class, args);
    }

}
