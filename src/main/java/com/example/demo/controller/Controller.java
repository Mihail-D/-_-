package com.example.demo.controller;

import com.example.demo.repository.OrderDto;
import com.example.demo.service.OrderService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/order")
@CrossOrigin("*")
@RestController
public class Controller {

    private final OrderService orderService;

    public Controller(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public void createOrder(@RequestBody List<Long> productIds) {
        orderService.createOrder(productIds);
    }

    @PostMapping("/{orderId}/return")
    public List<Long> returnOrder(@PathVariable Long orderId, @RequestBody Long returnedProductId) {
        return orderService.returnOrder(orderId, returnedProductId);
    }

    // Изменен маппинг для избежания конфликта
    @PostMapping("/{orderId}/issue")
    public void issueOrder(@PathVariable Long orderId) {
        orderService.issueOrder(orderId);
    }

    @GetMapping("/all")
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handle(IllegalArgumentException e) {
        return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}

/*
Дублирование маппинга для разных методов:
Методы returnOrder и issueOrder используют один и тот же URL маппинг @PostMapping("/{orderId}/return").
Это приведет к конфликту маппингов, и один из методов не будет доступен.
Решение: Изменить маппинг одного из методов, чтобы избежать конфликта.

Неконсистентное использование @Autowired:
Внедрение зависимостей через конструктор является предпочтительным способом для final полей, но использование @Autowired
на конструкторе избыточно при использовании современных версий Spring.
Решение: Убрать @Autowired аннотацию с конструктора.

Обработка исключений: В текущем контроллере есть базовая обработка исключений через @ExceptionHandler,
но она может быть не достаточно информативной или гибкой для всех случаев использования.
Решение: Рассмотреть использование ControllerAdvice для глобальной обработки исключений.*/
