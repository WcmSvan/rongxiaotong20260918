package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.common.Result;
import org.example.intercepter.RequireRole;
import org.example.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志管理接口（仅管理员可访问）
 */
@RestController
@RequestMapping("/admin/log")
@RequireRole("admin")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    //分页条件查询操作日志：可按操作人（模糊）、模块（精确）、状态（1成功/0失败）筛选
    @Operation(summary = "分页条件查询操作日志")
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       @RequestParam(required = false) String userName,
                       @RequestParam(required = false) String module,
                       @RequestParam(required = false) Integer status) {
        return operationLogService.page(pageNum, pageSize, userName, module, status);
    }

    //清空全部操作日志
    @Operation(summary = "清空操作日志")
    @DeleteMapping("/clear")
    public Result clear() {
        return operationLogService.clear();
    }
}
