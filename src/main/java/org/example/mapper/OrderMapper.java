package org.example.mapper;

import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OrderMapper {


    List<Order> searchAllGoodsByKeys(Order order);

    //管理员后台关键词搜索商品：包含已下架商品（不过滤 order_status）
    List<Order> searchAllGoodsByKeysForAdmin(Order order);

    List<Order> selectAllNeedsByKeys(Order order);

    List<Order> selectAll();
    List<Order> selectAllcontents(String keys);

    List<Order> selectAllneeds();

    void addGoods(Order order);

    int updateGoodsById(Order order);

    Order selectByPrimaryKey(Integer id);

    int deleteByPrimaryKey(Integer id);

    int takeDown(String orderId);

    int takeUp(String orderId);

    List<Order> selectMyNeeds(Order order);

    int AddMyNeeds(Order order);
}
