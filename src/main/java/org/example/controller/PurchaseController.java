package org.example.controller;
import io.swagger.v3.oas.annotations.Operation;
import org.example.common.Result;
import org.example.service.PurchaseDetailService;
import org.example.service.PurchaseSellService;
import org.example.service.PurchaseService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/purchase")

public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private PurchaseSellService purchaseSellService;

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Operation(summary ="生成订单")
    @PostMapping("/commitOrder/{addId}")
    public Result commitOrder(@PathVariable int addId) {
        return purchaseService.commitOrder(addId);
    }

    @Operation(summary ="我买入的订单")
    @GetMapping("/buyOrder")
    public Result selectBuyOrder() {
        return purchaseService.selectBuyOrderService();
    }


    @Operation(summary ="我卖出的订单")
    @GetMapping("/sellOrder")
    public Result selectSellOrder() {
        return purchaseSellService.selectSellOrderService();
    }

    @Operation(summary ="我买入的订单详情")
    @PostMapping("/buyOrderDetail")
    public Result selectOrderDetail() {
        // Service 返回的已是 Result，直接返回，避免二次包装导致前端拿到嵌套对象
        return purchaseDetailService.selectOrderDetailService();
    }



}
