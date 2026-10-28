package org.example.service;

import org.example.common.Result;

import java.util.List;

public interface PurchaseService {

    Result commitOrder(int addId);

    Result selectBuyOrderService();

}
