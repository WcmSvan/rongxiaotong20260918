package org.example.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * 我买入的订单详情VO
 * 一个主订单（tb_purchase）对应多条商品明细（tb_purchase_detail + tb_order）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseVO {

    //========主订单 tb_purchase 字段========
    private Integer purchaseId;         // 主订单id
    private String ownName;             // 购买人
    private Integer purchaseType;       // 订单类型
    private BigDecimal totalPrice;      // 订单总价
    private String address;             // 收货地址
    private Integer purchaseStatus;     // 订单状态
    private Date createTime;            // 下单时间

    private Integer detailId;          // 明细id

    private Integer orderId;           // 商品id
    private BigDecimal uninPrice;      // 下单时单价
    private BigDecimal sumPrice;       // 小计
    private Integer count;             // 购买数量

    //========tb_order 商品信息字段========
    private String title;              // 商品标题
    private String price;              // 商品价格
    private String content;            // 商品描述
    private String picture;            // 商品图片

}
