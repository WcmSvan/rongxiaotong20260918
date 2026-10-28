package org.example.service.impl;

import org.example.mapper.BankMapper;
import org.example.pojo.entity.Bank;
import org.example.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankServiceImpl implements BankService {
    @Autowired
    private BankMapper bankMapper;

    @Override
    public List<Bank> selectAllBank() {

        List<Bank> banks = bankMapper.selectAllBank();

        return banks;
    }

    @Override
    public Bank selectBankById(Integer bankId) {
        Bank bank = bankMapper.selectBankById(bankId);
        return bank;
    }

}
