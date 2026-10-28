package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.pojo.entity.OperationLog;

import java.util.List;

@Mapper
public interface OperationLogMapper {

    /**
     * 插入一条操作日志
     */
    int insert(OperationLog operationLog);

    /**
     * 按条件分页查询操作日志（配合 PageHelper 使用）
     * @param userName 操作人用户名（模糊）
     * @param module   业务模块（精确）
     * @param status   状态：1成功 0失败，null 查全部
     */
    List<OperationLog> selectPage(@Param("userName") String userName,
                                  @Param("module") String module,
                                  @Param("status") Integer status);

    /**
     * 清空全部操作日志
     */
    int deleteAll();
}
