package com.surya.zepto.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private List<String> products = new ArrayList<>();

    @GetMapping
    public List<String> getProducts() {
        return products;
    }

    @PostMapping
    public String addProduct(@RequestBody String product) {
        products.add(product);
        return "Product Added";
    }
}
