package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.entity.Bank;

import java.util.List;

@Mapper
public interface BankMapper {
    List<Bank> selectAllBank();

    /**
     * 根据银行id查询单家银行详情
     * @param bankId 银行主键
     * @return 银行贷款产品详情
     */
    Bank selectBankById(Integer bankId);
}
