package org.example.service;

import org.example.common.Result;
import org.example.pojo.entity.OperationLog;

public interface OperationLogService {

    /**
     * 保存一条操作日志（切面调用）
     */
    Result save(OperationLog operationLog);

    /**
     * 分页条件查询操作日志（管理员）
     */
    Result page(Integer pageNum, Integer pageSize, String userName, String module, Integer status);

    /**
     * 清空全部操作日志（管理员）
     */
    Result clear();
}
