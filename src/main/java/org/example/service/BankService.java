package org.example.service;

import org.example.pojo.entity.Bank;

import java.util.List;

public interface BankService {
    List<Bank> selectAllBank();

    //根据银行id查询单家银行详情
    Bank selectBankById(Integer bankId);
}
