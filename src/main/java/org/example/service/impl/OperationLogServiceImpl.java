package org.example.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.common.Result;
import org.example.mapper.OperationLogMapper;
import org.example.pojo.entity.OperationLog;
import org.example.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public Result save(OperationLog operationLog) {
        int rows = operationLogMapper.insert(operationLog);
        return rows > 0 ? Result.success() : Result.error("日志保存失败");
    }

    @Override
    public Result page(Integer pageNum, Integer pageSize, String userName, String module, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        List<OperationLog> list = operationLogMapper.selectPage(userName, module, status);
        return Result.success(new PageInfo<>(list), "查询成功");
    }

    @Override
    public Result clear() {
        operationLogMapper.deleteAll();
        return Result.success("操作日志已清空");
    }
}
