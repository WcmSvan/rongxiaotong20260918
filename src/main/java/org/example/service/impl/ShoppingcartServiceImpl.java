package org.example.service.impl;

import org.example.common.Result;
import org.example.mapper.ShoppingcartMapper;
import org.example.pojo.entity.Shoppingcart;
import org.example.service.ShoppingcartService;
import org.example.util.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ShoppingcartServiceImpl implements ShoppingcartService {

    @Autowired
    ShoppingcartMapper shoppingcartMapper;

    @Override
    public Result addShoppingcartService(Integer id) {
        // 获取当前登录用户名
        String ownName = BaseContext.getCurrentUser();

        if(id == null || id <= 0){
            return Result.error("商品ID不合法");
        }
        if(ownName == null || ownName.trim().isEmpty()){
            return Result.error("用户名不能为空");
        }

        // 构造查询条件：ownName + orderId
        Shoppingcart query = new Shoppingcart();
        query.setOwnName(ownName);
        query.setOrderId(id);
        Shoppingcart existCart = shoppingcartMapper.selectByShopping(query);

        if(existCart != null){
            Shoppingcart cart = new Shoppingcart();
            cart.setShoppingId(existCart.getShoppingId());
            cart.setCount(existCart.getCount() + 1);
            cart.setUpdateTime(new Date());
            shoppingcartMapper.updateByPrimaryKeySelective(cart);
            return Result.success("商品数量+1");}

        // 没有记录，新增购物车
        Shoppingcart shoppingcart = new Shoppingcart();
        shoppingcart.setOrderId(id);
        shoppingcart.setOwnName(ownName);
        shoppingcart.setCount(1);
        Date now = new Date();
        shoppingcart.setCreateTime(now);
        shoppingcart.setUpdateTime(now);
        shoppingcartMapper.insertSelective(shoppingcart);

        System.out.println("添加商品到购物车：" + id + " 用户：" + ownName);
        return Result.success("成功加入购物车");
    }



    @Override
    public Result deleteCartService(Integer id) {
        if(id == null || id <= 0){
            return Result.error("购物车ID不合法");
        }
        shoppingcartMapper.deleteByPrimaryKey(id);
        return Result.success("删除成功");
    }

    @Override
    public Result updateCartService(Integer id, Integer count) {
        if(id == null || id <= 0){
            return Result.error("购物车ID不合法");
        }
        if(count == null || count <= 0){
            return Result.error("数量不合法");
        }
        Shoppingcart cart = new Shoppingcart();
        cart.setShoppingId(id);
        cart.setCount(count);
        cart.setUpdateTime(new Date());
        shoppingcartMapper.updateByPrimaryKeySelective(cart);
        return Result.success("更新成功");
    }

    @Override
    public List<Shoppingcart> showcartService() {
        // 获取当前登录用户名
        String ownName = BaseContext.getCurrentUser();
        return shoppingcartMapper.selectByShoppingOwnName(ownName);
    }


}
