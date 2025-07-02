package com.example.booksystem.service;

import com.example.booksystem.dao.OrderDao;
import com.example.booksystem.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private BookService bookService;

    @Transactional
    public String createOrderFromCart(Integer userId) {
        // 获取购物车商品
        List<ShoppingCart> cartItems = shoppingCartService.getCartByUserId(userId);
        if (cartItems.isEmpty()) {
            return null;
        }

        // 计算总金额并检查库存
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (ShoppingCart item : cartItems) {
            Book book = bookService.getBookById(item.getBookId());
            if (book == null || !bookService.hasEnoughStock(item.getBookId(), item.getQuantity())) {
                return null;
            }
            totalAmount = totalAmount.add(book.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // 生成订单号
        String orderNo = generateOrderNo();

        // 创建订单
        Order order = new Order(orderNo, userId, totalAmount, "pending");
        int orderId = orderDao.saveOrder(order);

        // 创建订单详情并更新库存
        for (ShoppingCart item : cartItems) {
            Book book = bookService.getBookById(item.getBookId());
            OrderDetail detail = new OrderDetail(orderId, item.getBookId(), item.getQuantity(), book.getPrice());
            orderDao.saveOrderDetail(detail);
            
            // 扣减库存
            bookService.reduceStock(item.getBookId(), item.getQuantity());
        }

        // 清空购物车
        shoppingCartService.clearCart(userId);

        return orderNo;
    }

    public List<Order> getOrdersByUserId(Integer userId) {
        return orderDao.findByUserId(userId);
    }

    public List<Order> getAllOrders() {
        return orderDao.findAll();
    }

    public List<Order> getOrdersByStatus(String status) {
        return orderDao.findByStatus(status);
    }

    public Order getOrderById(Integer id) {
        return orderDao.findById(id);
    }

    public List<OrderDetail> getOrderDetails(Integer orderId) {
        return orderDao.findDetailsByOrderId(orderId);
    }

    public List<OrderDetailWithBook> getOrderDetailsWithBook(Integer orderId) {
        return orderDao.findDetailsWithBookByOrderId(orderId);
    }

    public boolean payOrder(Integer orderId) {
        return orderDao.updateOrderStatus(orderId, "paid") > 0;
    }

    public boolean shipOrder(Integer orderId) {
        return orderDao.updateOrderStatus(orderId, "shipped") > 0;
    }

    public boolean deliverOrder(Integer orderId) {
        return orderDao.updateOrderStatus(orderId, "delivered") > 0;
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = String.valueOf((int)(Math.random() * 1000));
        String orderNo = "ORD" + timestamp + String.format("%03d", Integer.parseInt(randomSuffix));
        
        // 确保订单号唯一
        while (orderDao.existsByOrderNo(orderNo)) {
            randomSuffix = String.valueOf((int)(Math.random() * 1000));
            orderNo = "ORD" + timestamp + String.format("%03d", Integer.parseInt(randomSuffix));
        }
        
        return orderNo;
    }
}