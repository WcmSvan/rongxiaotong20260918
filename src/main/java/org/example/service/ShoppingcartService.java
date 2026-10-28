package org.example.service;

import org.example.common.Result;
import org.example.pojo.entity.Shoppingcart;

import java.util.List;

public interface ShoppingcartService {
     Result addShoppingcartService(Integer id);


     Result deleteCartService(Integer id);


     Result updateCartService(Integer id, Integer count);

     List<Shoppingcart> showcartService();
}
