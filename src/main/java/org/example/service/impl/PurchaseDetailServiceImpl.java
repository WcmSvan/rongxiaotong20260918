package org.example.service.impl;
import org.example.common.Result;
import org.example.mapper.PurchaseMapper;
import org.example.pojo.entity.Purchase;
import org.example.pojo.entity.PurchaseDetail;
import org.example.pojo.vo.PurchaseVO;
import org.example.service.PurchaseDetailService;
import org.example.service.PurchaseService;
import org.example.util.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.example.mapper.PurchaseDetailMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseDetailServiceImpl implements PurchaseDetailService {
    @Autowired
    private PurchaseDetailMapper purchaseDetailMapper;

    @Override
    public void add(PurchaseDetail purchaseDetail) {
        purchaseDetailMapper.insertSelective(purchaseDetail);
    }

    @Override
    public Result selectOrderDetailService() {
        // 2.获取当前登录用户名，未登录直接返回
        String ownName = BaseContext.getCurrentUser();
        if (ownName == null || ownName.trim().isEmpty()) {
            return Result.error("用户未登录，请先登录");
        }

        List<PurchaseVO> purchaseVOList = purchaseDetailMapper.selectOrderDetail(ownName);
        if (purchaseVOList == null || purchaseVOList.isEmpty()) {
            return Result.error("订单不存在或无权查看该订单");
        }

        return Result.success(purchaseVOList, "我买入的订单详情查询成功");
    }
}
