package org.example.service.impl;

import jakarta.transaction.Transactional;
import org.example.common.Result;
import org.example.mapper.*;
import org.example.pojo.entity.*;
import org.example.service.PurchaseService;
import org.example.util.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class PurchaseServiceImpl implements PurchaseService {


    @Autowired
    private PurchaseMapper purchaseMapper;
    @Autowired
    private PurchaseDetailMapper purchaseDetailMapper;

    @Autowired
    private ShoppingcartMapper shoppingcartMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SellPurchaseMapper sellPurchaseMapper;

    @Transactional
    @Override
    public Result commitOrder(int addId) {
        String ownName = BaseContext.getCurrentUser();

        // 判空：如果ownName为null，说明token解析失败/未登录
        if(ownName == null || ownName.trim().isEmpty()){
            return Result.error("用户未登录");
        }


// 1. 查询当前用户所有购物车数据
        Shoppingcart queryCart = new Shoppingcart();
        queryCart.setOwnName(ownName);
        List<Shoppingcart> cartList = shoppingcartMapper.selectByShoppingOwnName(ownName);
        if(cartList == null || cartList.isEmpty()){
            return Result.error("购物车为空，无法下单");
        }

        // 2. 统计购物车总金额
        BigDecimal totalPrice = BigDecimal.ZERO;
        for(Shoppingcart cart : cartList){
            // 这里需要根据cart.getOrderId查询tb_order拿到商品价格
            // 如果你Shoppingcart实体里面存了price，就直接cart.getPrice()
            Order order = orderMapper.selectByPrimaryKey(cart.getOrderId());
            BigDecimal price = new BigDecimal(order.getPrice());
            BigDecimal count = new BigDecimal(cart.getCount());
            totalPrice = totalPrice.add(price.multiply(count));
        }

        // 3. 创建买入主表 Purchase
        Purchase purchase = new Purchase();
        purchase.setPurchaseType(1);//买入商品
        purchase.setOwnName(ownName);
        purchase.setAddress(String.valueOf(addId)); // addId 收货地址id，不变
        purchase.setPurchaseStatus(1);//状态统一为待发货
        purchase.setTotalPrice(totalPrice);
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
        purchaseMapper.insertPurchase(purchase);
        // 获取主表主键 purchaseId
        Integer purchaseId = purchase.getPurchaseId();

        // 4. 循环购物车，生成中间表PurchaseDetail，并删除购物车记录
        for(Shoppingcart cart : cartList){
            // 查询原始订单tb_order信息，拿价格
            Order order = orderMapper.selectByPrimaryKey(cart.getOrderId());

            PurchaseDetail detail = new PurchaseDetail();
            detail.setPurchaseId(purchaseId);
            detail.setOrderId(cart.getOrderId()); // ✅重点：orderId从购物车拿，不再是地址addId！
            detail.setUninPrice(new BigDecimal(order.getPrice()));
            detail.setSumPrice(new BigDecimal(order.getPrice()).multiply(new BigDecimal(cart.getCount())));
            detail.setCount(cart.getCount());


            purchaseDetailMapper.insertPurchaseDetail(detail);
            // 下单成功，删除该条购物车记录（调用你现成的deleteCartService）
            shoppingcartMapper.deleteByPrimaryKey(cart.getShoppingId());

            SellPurchase sell = new SellPurchase ();
            sell.setPurchaseId(purchaseId);
            sell.setOrderId(cart.getOrderId());
            sell.setOwnName(order.getOwnName());
            BigDecimal unitPrice = order.getPrice() == null ? BigDecimal.ZERO : new BigDecimal(order.getPrice());
            sell.setUninPrice(unitPrice);
            sell.setPurchaseType(2); // 卖出类型，2代表卖出
            sell.setSumPrice(unitPrice.multiply(new BigDecimal(cart.getCount())));
            sell.setAddress(String.valueOf(addId));
            sell.setPurchaseStatus(1); // 待发货
            sell.setCreateTime(new Date());
            sell.setUpdateTime(new Date());
            sellPurchaseMapper.insertSellPurchase(sell);
        }

        return Result.success(purchase,"订单成功生成");

    }

    @Override
    public Result selectBuyOrderService() {

        String ownName = BaseContext.getCurrentUser();

        // 判空：如果ownName为null，说明token解析失败/未登录
        if(ownName == null || ownName.trim().isEmpty()){
            return Result.error("用户未登录");
        }else{
            Result.success("已登录");
        }

        Purchase purchase = new Purchase();
        purchase.setOwnName(ownName);
        List<Purchase> orderList = purchaseMapper.selectorderByOwnName(purchase);
        return Result.success(orderList,"我买入的订单查询成功");
    }



}
