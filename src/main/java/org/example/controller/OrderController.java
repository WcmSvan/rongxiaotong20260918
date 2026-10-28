package org.example.controller;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.example.common.Result;
import org.example.pojo.entity.Order;
import org.example.service.PurchaseDetailService;
import org.example.service.PurchaseService;
import org.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/order")
@CrossOrigin

public class OrderController {
    @Autowired
    private OrderService orderService;

    //查询所有商品
    @GetMapping("/All")
    public List<Order> selectAll() {
        List<Order> orders = orderService.selectAll();
        return orders;
    }

    //查询所有的求购需求
    @GetMapping("/selectAllneeds")
    public List<Order> selectAllneeds(){
        List<Order> orders = orderService.selectAllneeds();
        return orders;
    }


    //添加商品，传来一个对象，包含添加的所有信息
    //@RequestBody这个注解表示将请求体中的json字符串转换为java对象
    @PostMapping("/addGoods")
    public Result addGoods(@RequestBody Order order){
        Result result = orderService.addGoods(order);
        return result;
    }


    //根据id修改商品
    @PostMapping("/updateGoods")
    public Result updateGoods(@RequestBody Order order){
        orderService.updateGoodsService(order);
        Order dbOrder = orderService.selectById(order.getOrderId());
        return Result.success(dbOrder,"修改商品成功");
    }



    @Operation(summary = "根据id删除商品")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") Integer id) {
        orderService.delete(id);
        return Result.success("删除成功");
    }



    @Operation(summary = "根据id查询商品")
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable("id") Integer id) {
        Order order = orderService.selectById(id);
        return Result.success(order, "查询成功");
    }


    @Operation(summary = "分页条件搜索所有商品")
    @GetMapping("/searchAllGoodsByKeys")
    public Result searchAllGoodsByKeys(
            @RequestParam String keys,
            @RequestParam Integer pageNum) {
        PageInfo<Order> orders = orderService.searchAllGoodsByKeys(keys,pageNum);
        return Result.success(orders, "查询成功");
    }


    @Operation(summary = "分页条件搜索需求商品")
    @GetMapping("/searchAllNeedsByKeys")
    public Result searchAllNeedsByKeys( @RequestParam String keys,
                                        @RequestParam Integer pageNum) {
        PageInfo<Order> orders = orderService.searchAllNeedsByKeys(keys,pageNum);
        return Result.success(orders, "查询成功");
    }

    @Operation(summary = "查询我的求购需求商品")
    @GetMapping("/selectMyNeeds")
    public Result selectMyNeeds(){
        List<Order> orders = orderService.selectMyNeedsService();
        return Result.success(orders, "查询成功");
    }

    @Operation(summary = "添加我的求购需求商品")
    @PostMapping("/AddtMyNeeds")
    public Result AddMyNeeds(@RequestBody @Valid Order order){
        Result orders = orderService.AddMyNeedsService(order);
        return Result.success(orders, "商品添加成功！");
    }
}
