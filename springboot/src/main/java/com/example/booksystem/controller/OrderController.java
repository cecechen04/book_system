package com.example.booksystem.controller;

import com.example.booksystem.model.ApiResponse;
import com.example.booksystem.model.Order;
import com.example.booksystem.model.OrderDetail;
import com.example.booksystem.model.OrderDetailWithBook;
import com.example.booksystem.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 从购物车创建订单
    @PostMapping("/create")
    public ApiResponse<String> createOrderFromCart(@RequestBody Map<String, Integer> request) {
        Integer userId = request.get("userId");
        if (userId == null) {
            return ApiResponse.error("用户ID不能为空");
        }

        String orderNo = orderService.createOrderFromCart(userId);
        if (orderNo != null) {
            return ApiResponse.success("订单创建成功", orderNo);
        } else {
            return ApiResponse.error("订单创建失败，购物车为空或库存不足");
        }
    }

    // 获取用户订单列表
    @GetMapping("/user/{userId}")
    public ApiResponse<List<Order>> getOrdersByUserId(@PathVariable Integer userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ApiResponse.success(orders);
    }

    // 获取所有订单（管理员）
    @GetMapping
    public ApiResponse<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ApiResponse.success(orders);
    }

    // 根据状态获取订单
    @GetMapping("/status/{status}")
    public ApiResponse<List<Order>> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ApiResponse.success(orders);
    }

    // 获取订单详情
    @GetMapping("/{id}")
    public ApiResponse<Order> getOrderById(@PathVariable Integer id) {
        Order order = orderService.getOrderById(id);
        if (order != null) {
            return ApiResponse.success(order);
        } else {
            return ApiResponse.error("订单不存在");
        }
    }

    // 获取订单商品详情
    @GetMapping("/{id}/details")
    public ApiResponse<List<OrderDetail>> getOrderDetails(@PathVariable Integer id) {
        List<OrderDetail> details = orderService.getOrderDetails(id);
        return ApiResponse.success(details);
    }

    // 获取订单商品详情（包含图书信息）
    @GetMapping("/{id}/details-with-book")
    public ApiResponse<List<OrderDetailWithBook>> getOrderDetailsWithBook(@PathVariable Integer id) {
        List<OrderDetailWithBook> details = orderService.getOrderDetailsWithBook(id);
        return ApiResponse.success(details);
    }

    // 支付订单
    @PutMapping("/{id}/pay")
    public ApiResponse<String> payOrder(@PathVariable Integer id) {
        boolean success = orderService.payOrder(id);
        if (success) {
            return ApiResponse.success("支付成功");
        } else {
            return ApiResponse.error("支付失败");
        }
    }

    // 发货（管理员）
    @PutMapping("/{id}/ship")
    public ApiResponse<String> shipOrder(@PathVariable Integer id) {
        boolean success = orderService.shipOrder(id);
        if (success) {
            return ApiResponse.success("发货成功");
        } else {
            return ApiResponse.error("发货失败");
        }
    }

    // 确认收货
    @PutMapping("/{id}/deliver")
    public ApiResponse<String> deliverOrder(@PathVariable Integer id) {
        boolean success = orderService.deliverOrder(id);
        if (success) {
            return ApiResponse.success("确认收货成功");
        } else {
            return ApiResponse.error("确认收货失败");
        }
    }
}