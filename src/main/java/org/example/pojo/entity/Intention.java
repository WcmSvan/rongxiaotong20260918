package org.example.pojo.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class Intention {
    private Integer id;
    // userName 由后端从登录态(BaseContext)设置，前端无需传，故去掉 @NotBlank
    private String userName;
    @NotBlank(message = "真实姓名不能为空")
    private String realName;
    @NotBlank(message = "地址不能为空")
    private String address;
    @NotNull(message = "金额不能为空")
    private Integer amount;
    @NotBlank(message = "用途不能为空")
    private String application;
    @NotBlank(message = "项目名称不能为空")
    private String item;
    @NotNull(message = "还款期限不能为空")
    private Integer repaymentPeriod;
    @NotNull(message = "种植面积不能为空")
    private Integer area;
    @NotBlank(message = "手机号不能为空")
    private String phone;
    private Date createTime;
    private Date updateTime;
}
