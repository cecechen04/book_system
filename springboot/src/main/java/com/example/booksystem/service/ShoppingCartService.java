package com.example.booksystem.service;

import com.example.booksystem.dao.ShoppingCartDao;
import com.example.booksystem.model.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartService {

    @Autowired
    private ShoppingCartDao shoppingCartDao;

    @Autowired
    private BookService bookService;

    public List<ShoppingCart> getCartByUserId(Integer userId) {
        return shoppingCartDao.findByUserId(userId);
    }

    public boolean addToCart(Integer userId, Integer bookId, Integer quantity) {
        // 检查库存
        if (!bookService.hasEnoughStock(bookId, quantity)) {
            return false;
        }

        // 检查购物车中是否已有该商品
        ShoppingCart existingItem = shoppingCartDao.findByUserIdAndBookId(userId, bookId);
        
        if (existingItem != null) {
            // 更新数量
            int newQuantity = existingItem.getQuantity() + quantity;
            if (!bookService.hasEnoughStock(bookId, newQuantity)) {
                return false;
            }
            return shoppingCartDao.updateQuantity(userId, bookId, newQuantity) > 0;
        } else {
            // 新增商品
            ShoppingCart cart = new ShoppingCart(userId, bookId, quantity);
            return shoppingCartDao.save(cart) > 0;
        }
    }

    public boolean updateCartQuantity(Integer userId, Integer bookId, Integer quantity) {
        if (quantity <= 0) {
            return removeFromCart(userId, bookId);
        }
        
        // 检查库存
        if (!bookService.hasEnoughStock(bookId, quantity)) {
            return false;
        }
        
        return shoppingCartDao.updateQuantity(userId, bookId, quantity) > 0;
    }

    public boolean removeFromCart(Integer userId, Integer bookId) {
        return shoppingCartDao.delete(userId, bookId) > 0;
    }

    public boolean clearCart(Integer userId) {
        return shoppingCartDao.deleteByUserId(userId) > 0;
    }
}