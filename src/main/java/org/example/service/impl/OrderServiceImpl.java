package org.example.service.impl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.mapper.OrderMapper;
import org.example.mapper.UserMapper;
import org.example.pojo.entity.Order;
import org.example.service.OrderService;
import org.example.util.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.List;
import org.example.common.Result;

@Service
public class OrderServiceImpl implements OrderService {

    private Integer pageSize = 12;

    @Autowired
    private OrderMapper orderMapper;


    @Override
    public PageInfo<Order> searchAllNeedsByKeys(String keys, Integer pageNum) {
        Order order = new Order();
        order.setType("needs");
        order.setContent(keys);

        //分页
        PageHelper.startPage(pageNum, pageSize);
        //查询
        List<Order> orders = orderMapper.selectAllNeedsByKeys(order);
        System.out.println(orders);
        PageInfo<Order> orderPageInfo = new PageInfo<>(orders);

        return orderPageInfo;
    }



    @Override
    public PageInfo<Order> searchAllGoodsByKeys(String keys, Integer pageNum) {
        Order order = new Order();
        order.setContent(keys);
        order.setType("goods");
        //分页
        PageHelper.startPage(pageNum, pageSize);
        //查询
        List<Order> orders = orderMapper.searchAllGoodsByKeys(order);
        PageInfo<Order> orderPageInfo = new PageInfo<>(orders);
        return orderPageInfo;
    }

    /**
     * 管理员后台关键词分页搜索商品：包含已下架商品（不过滤 order_status）
     */
    @Override
    public PageInfo<Order> searchAllGoodsByKeysForAdmin(String keys, Integer pageNum) {
        Order order = new Order();
        order.setContent(keys);
        order.setType("goods");
        //分页
        PageHelper.startPage(pageNum, pageSize);
        //查询（专用 mapper，不限制 order_status）
        List<Order> orders = orderMapper.searchAllGoodsByKeysForAdmin(order);
        PageInfo<Order> orderPageInfo = new PageInfo<>(orders);
        return orderPageInfo;
    }

    @Override
    public List<Order> selectAll() {
        List<Order> orders = orderMapper.selectAll();
        return orders;
    }


    @Override
    public List<Order> selectAllcontents(String keys) {
        return orderMapper.selectAllcontents(keys);
    }

    @Override
    public List<Order> selectAllneeds() {
        return orderMapper.selectAllneeds();
    }

    @Override
    public Result addGoods(Order order) {
        // 获取当前登录用户名
        String ownName = BaseContext.getCurrentUser();
        order.setOwnName(ownName);
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        orderMapper.addGoods(order);
        return Result.success("添加商品成功");
    }

    @Override
    public Result updateGoodsService(Order order) {
        // 1. 参数校验：订单id不能为空

        // 获取当前登录用户名
        String ownName = BaseContext.getCurrentUser();
        order.setOwnName(ownName);
        if(order == null || order.getOrderId() == null){
            return Result.error("订单ID不能为空");
        }

        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());

        // 修改成功，受影响行数是1
        int row=orderMapper.updateGoodsById(order);
        if(row != 1){
            return Result.error("商品信息修改失败");
        }else{
            return Result.success("商品信息修改成功");
        }

    }

    /**
     * 管理员修改商品
     * 与普通用户修改的区别：
     * 1. 先判空再使用，避免空指针；
     * 2. 先查库校验商品是否存在；
     * 3. 不覆盖 ownName（保留原归属卖家）；
     * 4. 不修改 createTime，只刷新 updateTime。
     */
    @Override
    public Result adminUpdateGoods(Order order) {
        //1.参数校验：对象与主键不能为空（先判空后使用）
        if (order == null || order.getOrderId() == null) {
            return Result.error("订单ID不能为空");
        }
        //2.查库校验商品是否存在
        Order dbOrder = orderMapper.selectByPrimaryKey(order.getOrderId());
        if (dbOrder == null) {
            return Result.error("商品不存在，修改失败");
        }
        //3.只刷新更新时间，创建时间与归属人保持原值
        //显式置空，防止前端在请求体中伪造 ownName/createTime 被动态SQL写入
        order.setOwnName(null);
        order.setCreateTime(null);
        order.setUpdateTime(new Date());
        //4.执行更新
        int row = orderMapper.updateGoodsById(order);
        if (row != 1) {
            return Result.error("商品信息修改失败");
        }
        return Result.success("商品信息修改成功");
    }

    @Override
    public Order selectById(Integer id) {
        Order order = orderMapper.selectByPrimaryKey(id);
        return order;
    }


    @Override
    public Result delete(Integer id) {
        if (id == null) {
            return Result.error("订单ID不能为空");
        }
        int row = orderMapper.deleteByPrimaryKey(id);
        if (row != 1) {
            return Result.error("删除失败");
        }
        return Result.success("删除成功");
    }


    @Override
    public Result takeDown(String orderId) {
        if (orderId == null) {
            return Result.error("订单ID不能为空");
        }
        int row = orderMapper.takeDown(orderId);
        if (row != 1) {
            return Result.error("商品下架失败");
        }
        return Result.success("商品下架成功");
    }

    @Override
    public Result takeUp(String orderId) {
        if (orderId == null) {
            return Result.error("订单ID不能为空");
        }
        int row = orderMapper.takeUp(orderId);
        if (row != 1) {
            return Result.error("商品上架失败");
        }
        return Result.success("商品上架成功");
    }

    @Override
    public List<Order> selectMyNeedsService() {
        // 获取当前登录用户名
        String ownName = BaseContext.getCurrentUser();
        // 未登录判断，和你之前代码保持一致
        if (ownName == null || ownName.trim().isEmpty()) {
            return null;
        }
        Order order = new Order();
        order.setOwnName(ownName);
        // mapper返回单条Order
        List<Order> myOrder = orderMapper.selectMyNeeds(order);
        return myOrder;
    }

    @Override
    public Result AddMyNeedsService(Order order) {
        // 获取当前登录用户名
        String ownName = BaseContext.getCurrentUser();
        // 未登录判断
        if (ownName == null || ownName.trim().isEmpty()) {
            return Result.error("用户未登录");
        }
        order.setOwnName(ownName);
        order.setType("needs");
        order.setOrderStatus(0); // 默认上架，否则插入 NULL 触发 order_status NOT NULL 约束报 500
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        // 执行插入，返回影响行数
        int result = orderMapper.AddMyNeeds(order);
        // 判断是否插入成功
        if (result > 0) {
            return Result.success(order,"成功发布我的求购商品"); // 插入成功，order里面已经回填自增id
        }
        return Result.error("商品发布失败");
    }

    /**
     * 分页查询所有商品
     */
    @Override
    public Result selectAllGoods(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orders = orderMapper.selectAll();
        PageInfo<Order> pageInfo = new PageInfo<>(orders);
        return Result.success(pageInfo);
    }

    /**
     * 分页查询所有求购商品
     */
    @Override
    public Result selectAllNeeds(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orders = orderMapper.selectAllneeds();
        PageInfo<Order> pageInfo = new PageInfo<>(orders);
        return Result.success(pageInfo);
    }


}
