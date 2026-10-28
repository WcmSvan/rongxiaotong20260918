package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.common.Result;
import org.example.pojo.entity.Bank;
import org.example.pojo.entity.Finance;
import org.example.pojo.entity.Intention;
import org.example.service.BankService;
import org.example.service.FinanceService;
import jakarta.validation.Valid;
import org.example.intercepter.RequireRole;
import org.example.util.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 融资模块
 * */
@Tag(name = "融资模块接口")
@RestController
@RequestMapping("/finance")
@CrossOrigin
public class FinanceController {
    @Autowired
    private FinanceService financeService;
    @Autowired
    private BankService bankService;

    //查询所有银行
    @Operation(summary = "查询所有银行")
    @GetMapping("/selectbank")
    public Result selectbank() {
        List<Bank> banks = bankService.selectAllBank();
        return Result.success(banks);
    }

    //查询当前登录用户的融资意向
    @Operation(summary = "查询我的融资意向")
    @GetMapping("/selectIntentionByName")
    public Result selectIntentionByName() {
        // userName 由 Service 层从 BaseContext 获取，前端无需传
        List<Intention> intentions = financeService.selectIntentionByName();
        return Result.success(intentions);
    }

    //添加融资意向
    @Operation(summary = "添加融资意向")
    @PostMapping("/insertIntentionByName")
    public Result insertIntentionByName(@Valid @RequestBody Intention intention){
        // userName 由 Service 层从 BaseContext 覆盖，防止越权
        financeService.insertIntentionByName(intention);
        return Result.success("添加成功");
    }
    
    //根据用户名修改融资意向
    @Operation(summary = "根据用户名修改融资意向")
    @PutMapping("/updateIntentionByName")
    public Result updateIntentionByName(@Valid @RequestBody Intention intention){
        // userName 由 Service 层从 BaseContext 覆盖，SQL 用 id+userName 双条件防越权
        financeService.updateIntentionByName(intention);
        return Result.success("修改成功");
    }

    //根据意向id删除单条融资意向
    @Operation(summary = "根据意向id删除单条融资意向")
    @DeleteMapping("/deleteIntentionById")
    public Result deleteIntentionById(@RequestParam Integer id) {
        // userName 由 Service 层从 BaseContext 获取，只删除当前登录用户自己的意向
        financeService.deleteIntentionById(id);
        return Result.success("删除成功");
    }

    //查询推荐融资人
    @Operation(summary = "查询推荐融资人")
    @GetMapping("/selectRecommendByName")
    public Result selectRecommendByName(){
        // userName 由 Service 层从 BaseContext 获取
        List<Intention> intentions = financeService.selectRecommendByName();
        return Result.success(intentions);
    }

    //根据银行id查询该银行的详情
    @Operation(summary = "查询该银行的详情")
    @GetMapping("/selectFinaceUser/{bankId}")
    public Result selectFinaceUser(@PathVariable("bankId") Integer bankId) {
        Bank bank = bankService.selectBankById(bankId);
        if (bank == null) {
            return Result.error("该银行不存在");
        }
        return Result.success(bank);
    }

    //根据贷款id查询贷款详情（只能查本人的，管理员除外）
    @Operation(summary = "根据id查询贷款详情")
    @GetMapping("/selectFinanceById/{financeId}")
    public Result selectFinanceById(@PathVariable("financeId") Integer financeId) {
        Finance finance = financeService.selectFinanceById(financeId);
        return Result.success(finance);
    }

    //查询当前登录用户的贷款申请列表（含组合贷款多条记录）
    @Operation(summary = "查询我的贷款列表")
    @GetMapping("/selectMyFinance")
    public Result selectMyFinance() {
        // ownName 由 Service 层从 BaseContext 获取，前端无需传
        List<Finance> list = financeService.selectMyFinance();
        return Result.success(list);
    }


    //添加单人贷款申请
    @Operation(summary = "添加单人贷款申请")
    @PostMapping("/addFinanceSingle")
    public Result add(@Valid @RequestBody Finance finance) {
        // ownName 由 Service 层从 BaseContext 设置，防止伪造申请人
        financeService.addFinance(finance);
        return Result.success("贷款申请提交成功");
    }

    //添加组合贷款申请
    @Operation(summary = "添加组合贷款")
    @PostMapping("/addFinanceMulti")
    public Result addFinanceMulti(@Valid @RequestBody List<Finance> finances) {
        // ownName 由 Service 层从 BaseContext 设置，防止伪造申请人
        financeService.addFinanceMulti(finances);
        return Result.success("贷款申请提交成功");
    }
}
