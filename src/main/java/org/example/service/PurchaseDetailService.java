package org.example.service;

import org.example.common.Result;
import org.example.pojo.entity.PurchaseDetail;

import java.util.List;

public interface PurchaseDetailService {
    void add(PurchaseDetail purchaseDetail);


    Result selectOrderDetailService();
}
