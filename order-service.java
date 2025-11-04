application.properties


server.port=8083

spring.datasource.url=jdbc:mysql://localhost:3306/order_db
spring.datasource.username=root
spring.datasource.password=yourpassword

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# URLs of other microservices
user.service.url=http://localhost:8081/users
product.service.url=http://localhost:8082/products



Create Order Entity
package com.example.orderservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long productId;
    private String status;   // e.g. CREATED, PAID, DELIVERED

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}


Repository Layer
package com.example.orderservice.repository;

import com.example.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}


dto/UserDto.java

package com.example.orderservice.dto;

public class UserDto {
    private Long id;
    private String name;
    private String email;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

dto/ProductDto.java

package com.example.orderservice.dto;

public class ProductDto {
    private Long id;
    private String name;
    private Double price;
    private Integer stock;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}


dto/OrderResponse.java

package com.example.orderservice.dto;

public class OrderResponse {
    private Long orderId;
    private String userName;
    private String productName;
    private Double price;
    private String status;

    public OrderResponse(Long orderId, String userName, String productName, Double price, String status) {
        this.orderId = orderId;
        this.userName = userName;
        this.productName = productName;
        this.price = price;
        this.status = status;
    }

    // Getters & Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

🌐 6️⃣ Config — RestTemplate Bean

Create a configuration class to make RestTemplate available:

package com.example.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

Service Layer
package com.example.orderservice.service;

import com.example.orderservice.dto.*;
import com.example.orderservice.model.Order;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${product.service.url}")
    private String productServiceUrl;

    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    public OrderResponse createOrder(Long userId, Long productId) {
        // 1️⃣ Fetch User Info
        UserDto user = restTemplate.getForObject(userServiceUrl + "/" + userId, UserDto.class);

        // 2️⃣ Fetch Product Info
        ProductDto product = restTemplate.getForObject(productServiceUrl + "/" + productId, ProductDto.class);

        // 3️⃣ Create Order Record
        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setStatus("CREATED");
        orderRepository.save(order);

        // 4️⃣ Build Response
        return new OrderResponse(
                order.getId(),
                user.getName(),
                product.getName(),
                product.getPrice(),
                order.getStatus()
        );
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElse(null);
    }
}

Controller Layer
package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.model.Order;
import com.example.orderservice.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Create new order
    @PostMapping
    public OrderResponse createOrder(@RequestParam Long userId, @RequestParam Long productId) {
        return orderService.createOrder(userId, productId);
    }

    // Get order by ID
    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }
}

Step 1: Create a User

POST → http://localhost:8081/users

{
  "name": "Alice",
  "email": "alice@gmail.com",
  "password": "12345"
}


Response:

{
  "id": 1,
  "name": "Alice",
  "email": "alice@gmail.com"
}

📦 Step 2: Add a Product

POST → http://localhost:8082/products

{
  "name": "Laptop",
  "description": "Lenovo ThinkPad",
  "price": 85000,
  "stock": 10
}


Response:

{
  "id": 1,
  "name": "Laptop",
  "price": 85000
}

🧾 Step 3: Create an Order

POST → http://localhost:8083/orders?userId=1&productId=1

✅ Response:

{
  "orderId": 1,
  "userName": "Alice",
  "productName": "Laptop",
  "price": 85000,
  "status": "CREATED"
}