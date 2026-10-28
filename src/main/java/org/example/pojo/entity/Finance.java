package org.example.pojo.entity;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Finance {
    private Integer financeId;
    private Integer bankId;
    private String ownName;
    private String realName;
    private String phone;
    private String idNum;
    private Integer status;//0：未贷款 1：已贷款 2：已还款
    @NotNull(message = "金额不能为空")
    private BigDecimal money;
    @NotNull(message = "利率不能为空")
    private BigDecimal rate;
    @NotNull(message = "还款周期不能为空")
    private Integer repayment;
    private Date createTime;
    private Date updateTime;

    private String combinationName1;
    private String combinationPhone1;
    private String combinationIdnum1;
    private String combinationName2;
    private String combinationPhone2;
    private String combinationIdnum2;

    private String fileInfo;

    // 非数据库表字段：LEFT JOIN tb_bank 关联带出的银行名称，仅查询时返回，新增/修改不使用
    private String bankName;
}
