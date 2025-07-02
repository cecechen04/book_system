package com.example.booksystem.controller;

import com.example.booksystem.model.ApiResponse;
import com.example.booksystem.model.ShoppingCart;
import com.example.booksystem.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    // 获取用户购物车
    @GetMapping("/user/{userId}")
    public ApiResponse<List<ShoppingCart>> getCartByUserId(@PathVariable Integer userId) {
        List<ShoppingCart> cartItems = shoppingCartService.getCartByUserId(userId);
        return ApiResponse.success(cartItems);
    }

    // 添加商品到购物车
    @PostMapping("/add")
    public ApiResponse<String> addToCart(@RequestBody Map<String, Integer> request) {
        Integer userId = request.get("userId");
        Integer bookId = request.get("bookId");
        Integer quantity = request.get("quantity");
        
        if (userId == null || bookId == null || quantity == null || quantity <= 0) {
            return ApiResponse.error("参数错误");
        }

        boolean success = shoppingCartService.addToCart(userId, bookId, quantity);
        if (success) {
            return ApiResponse.success("添加成功");
        } else {
            return ApiResponse.error("添加失败，库存不足");
        }
    }

    // 更新购物车商品数量
    @PutMapping("/update")
    public ApiResponse<String> updateCartQuantity(@RequestBody Map<String, Integer> request) {
        Integer userId = request.get("userId");
        Integer bookId = request.get("bookId");
        Integer quantity = request.get("quantity");
        
        if (userId == null || bookId == null || quantity == null || quantity < 0) {
            return ApiResponse.error("参数错误");
        }

        boolean success = shoppingCartService.updateCartQuantity(userId, bookId, quantity);
        if (success) {
            return ApiResponse.success("更新成功");
        } else {
            return ApiResponse.error("更新失败，库存不足");
        }
    }

    // 从购物车移除商品
    @DeleteMapping("/remove")
    public ApiResponse<String> removeFromCart(@RequestParam Integer userId, @RequestParam Integer bookId) {
        boolean success = shoppingCartService.removeFromCart(userId, bookId);
        if (success) {
            return ApiResponse.success("移除成功");
        } else {
            return ApiResponse.error("移除失败");
        }
    }

    // 清空购物车
    @DeleteMapping("/clear/{userId}")
    public ApiResponse<String> clearCart(@PathVariable Integer userId) {
        boolean success = shoppingCartService.clearCart(userId);
        if (success) {
            return ApiResponse.success("清空成功");
        } else {
            return ApiResponse.error("清空失败");
        }
    }
}