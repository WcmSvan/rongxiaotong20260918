package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.entity.Shoppingcart;

import java.util.List;

@Mapper
public interface ShoppingcartMapper {

    Shoppingcart selectByShopping(Shoppingcart query);

    void updateByPrimaryKeySelective(Shoppingcart cart);

    void insertSelective(Shoppingcart shoppingcart);

    void deleteByPrimaryKey(Integer id);


    List<Shoppingcart> selectByShoppingOwnName(String ownName);
}
