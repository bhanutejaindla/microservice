application.properties
server.port=8084

spring.datasource.url=jdbc:mysql://localhost:3306/payment_db
spring.datasource.username=root
spring.datasource.password=yourpassword

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

order.service.url=http://localhost:8083/orders


Create Payment Entity
package com.example.paymentservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Double amount;
    private String status; // PENDING, SUCCESS, FAILED

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}


Repository
package com.example.paymentservice.repository;

import com.example.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}

DTO (to receive order info)

dto/OrderDto.java

package com.example.paymentservice.dto;

public class OrderDto {
    private Long id;
    private Long userId;
    private Long productId;
    private String status;

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

🌐 6️⃣ RestTemplate Bean

config/AppConfig.java

package com.example.paymentservice.config;

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


Payment Service
package com.example.paymentservice.service;

import com.example.paymentservice.dto.OrderDto;
import com.example.paymentservice.model.Payment;
import com.example.paymentservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;

    @Value("${order.service.url}")
    private String orderServiceUrl;

    public PaymentService(PaymentRepository paymentRepository, RestTemplate restTemplate) {
        this.paymentRepository = paymentRepository;
        this.restTemplate = restTemplate;
    }

    public Payment processPayment(Long orderId, Double amount) {
        // 1️⃣ Get order info
        OrderDto order = restTemplate.getForObject(orderServiceUrl + "/" + orderId, OrderDto.class);

        // 2️⃣ Validate order exists
        if (order == null) {
            throw new RuntimeException("Order not found for ID: " + orderId);
        }

        // 3️⃣ Simulate payment logic (mock)
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);

        if (amount > 0) {
            payment.setStatus("SUCCESS");
        } else {
            payment.setStatus("FAILED");
        }

        // 4️⃣ Save payment
        paymentRepository.save(payment);

        // 5️⃣ (optional) Notify Order Service to update status later
        return payment;
    }

    public Payment getPayment(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }
}

🌍 8️⃣ Controller
package com.example.paymentservice.controller;

import com.example.paymentservice.model.Payment;
import com.example.paymentservice.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Make a payment
    @PostMapping
    public Payment makePayment(@RequestParam Long orderId, @RequestParam Double amount) {
        return paymentService.processPayment(orderId, amount);
    }

    // Get payment by ID
    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable Long id) {
        return paymentService.getPayment(id);
    }
}

🧪 9️⃣ Test in Postman

Before testing, make sure:
✅ user-service → 8081 running
✅ product-service → 8082 running
✅ order-service → 8083 running
✅ payment-service → 8084 running

🧾 Step 1: Create User

POST → http://localhost:8081/users

{
  "name": "Alice",
  "email": "alice@gmail.com",
  "password": "12345"
}

📦 Step 2: Create Product

POST → http://localhost:8082/products

{
  "name": "Laptop",
  "description": "Lenovo ThinkPad",
  "price": 85000,
  "stock": 10
}

🧺 Step 3: Create Order

POST → http://localhost:8083/orders?userId=1&productId=1

✅ Response:

{
  "orderId": 1,
  "userName": "Alice",
  "productName": "Laptop",
  "price": 85000,
  "status": "CREATED"
}

Step 4: Make Payment

POST → http://localhost:8084/payments?orderId=1&amount=85000

✅ Response:

{
  "id": 1,
  "orderId": 1,
  "amount": 85000,
  "status": "SUCCESS"
}
