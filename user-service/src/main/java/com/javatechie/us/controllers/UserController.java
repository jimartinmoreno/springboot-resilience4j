package com.javatechie.us.controllers;

import com.javatechie.us.dto.OrderDTO;
import com.javatechie.us.services.CatalogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user-service")
@Slf4j
public class UserController {
    private final CatalogService catalogService;

    public UserController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/displayOrders")
    public List<OrderDTO> displayOrders(@RequestParam("category") String category) {
        log.info("displayOrders by category {}", category);
        return catalogService.getOrdersByCategory(category);
    }
}