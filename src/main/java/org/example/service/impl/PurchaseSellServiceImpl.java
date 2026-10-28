package org.example.service.impl;

import org.example.common.Result;
import org.example.mapper.SellPurchaseMapper;
import org.example.pojo.entity.SellPurchase;
import org.example.service.PurchaseSellService;
import org.example.util.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseSellServiceImpl implements PurchaseSellService {

    @Autowired
    private SellPurchaseMapper sellPurchaseMapper;

    @Override
    public Result selectSellOrderService() {
        String ownName = BaseContext.getCurrentUser();

        // 判空：如果ownName为null，说明token解析失败/未登录
        if(ownName == null || ownName.trim().isEmpty()){
            return Result.error("用户未登录");
        }else{
            Result.success("已登录");
        }

        SellPurchase purchase = new SellPurchase();
        purchase.setOwnName(ownName);
        List<SellPurchase> orderList = sellPurchaseMapper.selectorderSellOwnName(purchase);
        return Result.success(orderList,"我卖出的订单查询成功");

    }
}
