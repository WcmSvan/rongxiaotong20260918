package org.example.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 操作日志实体，对应 tb_operation_log 表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class OperationLog {
    private Long id;
    private String userName;//操作人用户名
    private String role;//操作人角色
    private String module;//业务模块
    private String operation;//操作描述
    private String httpMethod;//请求方式 GET/POST/PUT/DELETE
    private String url;//请求地址
    private String params;//请求参数（JSON）
    private String ip;//操作IP
    private Long costTime;//耗时（毫秒）
    private Integer status;//状态：1成功 0失败
    private String errorMsg;//异常信息
    private Date createTime;//操作时间
}
