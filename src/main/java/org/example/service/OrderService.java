package org.example.service;

import com.github.pagehelper.PageInfo;
import org.example.pojo.entity.Order;
import org.example.common.Result;


import java.util.List;

public interface OrderService {
    PageInfo<Order> searchAllNeedsByKeys(String keys, Integer pageNum);

    PageInfo<Order> searchAllGoodsByKeys(String keys, Integer pageNum);

    /**
     * 管理员后台关键词分页搜索商品：包含已下架商品（不过滤 order_status）
     * @param keys 关键词（匹配 content 或 title）
     * @param pageNum 页码
     * @return 分页结果
     */
    PageInfo<Order> searchAllGoodsByKeysForAdmin(String keys, Integer pageNum);


    Order selectById(Integer id);

    List<Order> selectAll();
    List<Order> selectAllcontents(String keys);

    List<Order> selectAllneeds();

    Result addGoods(Order order);


    Result updateGoodsService(Order order);

    /**
     * 管理员修改商品：不覆盖商品归属人(ownName)，不修改创建时间(createTime)
     */
    Result adminUpdateGoods(Order order);

    Result delete(Integer id);

    Result takeDown(String orderId);

    Result takeUp(String orderId);

    List<Order> selectMyNeedsService();

    Result AddMyNeedsService(Order order);

    /**
     * 分页查询所有商品
     * @param pageNum
     * @param pageSize
     * @return
     */
    Result selectAllGoods(int pageNum, int pageSize);

    /**
     * 分页查询所有求购
     * @param pageNum
     * @param pageSize
     * @return
     */
    Result selectAllNeeds(int pageNum, int pageSize);
}
