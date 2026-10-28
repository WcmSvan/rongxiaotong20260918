package org.example.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.pojo.entity.Shoppingcart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.example.common.Result;
import org.example.service.ShoppingcartService;

import java.util.List;

@RestController
@RequestMapping("/cart")
@Tag(name = "购物车接口")
public class CartController {

    @Autowired
    private ShoppingcartService shoppingcartService;

    //要传一个id,知道是哪一个商品 加入购物,因为没有认证，不知道用户名字，也直接获取一下
    @Operation(summary ="添加商品到购物车")
    @PostMapping("/addCart")
    public  Result addCart(@RequestParam Integer orderId){
        Result result =shoppingcartService.addShoppingcartService(orderId);
        return result;
    }


    //根据购物车id来删除
    @Operation(summary ="删除购物车商品")
    @DeleteMapping("/deletecart/{id}")
    public Result deleteCart(@PathVariable("id") Integer id){
        Result result = shoppingcartService.deleteCartService(id);
        return result;
    }

    @Operation(summary ="更新购物车内商品数量")
    @PutMapping("/updateCart/{id}/{count}")
    public Result updateCart(@PathVariable("id") Integer id, @PathVariable("count") Integer count){
        Result result = shoppingcartService.updateCartService(id, count);
        return result;
    }


    @Operation(summary ="展示购物车列表")
    @GetMapping("/showCart")
    public Result showCart(){
        List<Shoppingcart> list = shoppingcartService.showcartService();
        return Result.success(list);
    }


















}
