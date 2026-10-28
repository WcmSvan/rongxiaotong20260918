package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.entity.Purchase;

import java.util.List;

@Mapper
public interface PurchaseMapper {

    void insertPurchase(Purchase purchase);

    List<Purchase> selectorderByOwnName(Purchase purchase);


}
