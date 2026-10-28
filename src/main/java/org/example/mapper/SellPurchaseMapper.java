package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.entity.SellPurchase;

import java.util.List;

@Mapper
public interface SellPurchaseMapper {

    List<SellPurchase> selectorderSellOwnName(SellPurchase purchase);

    void insertSellPurchase(SellPurchase sellPurchase);
}
