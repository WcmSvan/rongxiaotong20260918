package org.example.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import org.example.annotation.Log;
import org.example.common.Result;
import org.example.intercepter.RequireRole;
import org.example.pojo.entity.*;
import org.example.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequireRole("admin")
public class AdminManageController {

    @Autowired
    OrderService orderService;

    @Autowired
    KnowledgeService knowledgeService;

    @Autowired
    ExpertManagementService expertManagementService;

    @Autowired
    QuestionService questionService;

    @Autowired
    ExpertService expertService;

    @Autowired
    private FinanceService financeService;
    @Autowired
    private BankService bankService;

    /**
     * 商品管理相关接口
     */

    //分页查询所有商品（调用）
    @GetMapping("/selectAllGoods")
    public Result selectAllGoods(@RequestParam(defaultValue = "1") int pageNum,
                                 @RequestParam(defaultValue = "10") int pageSize){
        Result result = orderService.selectAllGoods(pageNum, pageSize);
        return result;
    }

    //管理员后台关键词分页搜索商品（包含已下架商品）
    @GetMapping("/searchGoodsByKeysForAdmin")
    public Result searchAllGoodsByKeysForAdmin(@RequestParam String keys,
                                               @RequestParam(defaultValue = "1") Integer pageNum){
        PageInfo<Order> orders = orderService.searchAllGoodsByKeysForAdmin(keys,pageNum);
        return Result.success(orders,"查询成功");
    }

    //管理员添加商品接口(调用)
    @Log(module = "商品管理", operation = "管理员添加商品")
    @PostMapping("/AddGoods")
    public Result AddGoods(@RequestBody Order order){
        Result result = orderService.addGoods(order);
        return result;
    }

    //管理员修改商品接口（调用）
    @Log(module = "商品管理", operation = "管理员修改商品")
    @PostMapping("/updateGoods")
    public Result updateGoods(@RequestBody Order order){
        return orderService.adminUpdateGoods(order);
    }

    //管理员删除商品接口
    @Log(module = "商品管理", operation = "管理员删除商品")
    @DeleteMapping("/deleteGoods")
    public Result deleteGoods(@RequestParam Integer id){
        orderService.delete(id);
        return Result.success("删除商品成功");
    }

    //管理员下架商品接口
    @Operation(summary = "商品下架")
    @Log(module = "商品管理", operation = "管理员下架商品")
    @PostMapping("/takeDownGoods/{orderId}")
    public Result takeDownGoods(@PathVariable String orderId){
        Result result = orderService.takeDown(orderId);
        return result;
    }

    //管理员上架商品接口
    @Operation(summary = "商品上架")
    @Log(module = "商品管理", operation = "管理员上架商品")
    @PostMapping("/takeUpGoods/{orderId}")
    public Result takeUpGoods(@PathVariable String orderId){
        Result result = orderService.takeUp(orderId);
        return result;
    }

    //管理员分页查询所有需求接口（type=needs）（调用）
    @GetMapping("/selectAllNeeds")
    public Result selectAllNeeds(@RequestParam(defaultValue = "1") int pageNum,
                                 @RequestParam(defaultValue = "10") int pageSize){
        Result result = orderService.selectAllNeeds(pageNum, pageSize);
        return result;
    }

    //管理员删除不合理需求接口
    @Log(module = "需求管理", operation = "管理员删除需求")
    @DeleteMapping("/deleteNeeds")
    public Result deleteNeeds(@RequestParam Integer id){
        Result result = orderService.delete(id);
        return result;
    }

    /**
     * 知识内容管理相关接口（调用）
     */
    //分页查询所有知识内容
    @GetMapping("/selectAllKnoledge/{pageNum}")
    public Result selectAllKnoledge(@PathVariable Integer pageNum){
        PageInfo<Knowledge> pageInfo = knowledgeService.findPageKnowledge(pageNum);
        return Result.success(pageInfo);
    }

    //根据知识内容标题或者内容的关键词分页搜索知识内容
    @GetMapping("/searchKnoledgeByKeys")
    public Result searchKnoledgeByKeys(@RequestParam String keyword,
                                       @RequestParam(defaultValue = "1") Integer pageNum){
        PageInfo<Knowledge> knowledge = knowledgeService.searchPageKnowledge(keyword, pageNum);
        return Result.success(knowledge,"查询成功");
    }


    //管理员添加知识内容接口
    @Log(module = "知识管理", operation = "管理员添加知识内容")
    @PostMapping("/addKnoledge")
    public Result addKnoledge(@RequestParam("title") String title,
                              @RequestParam("content") String content,
                              @RequestParam("picpath") String picpath){
        Result result = expertManagementService.publishKnowledge(title, content, picpath);
        return result;
    }

    //管理员修改知识内容接口
    @Log(module = "知识管理", operation = "管理员修改知识内容")
    @PostMapping("/updateKnoledges/{id}")
    public Result updateKnoledge(@PathVariable("id") Integer id,
                                 @RequestParam("title") String title,
                                 @RequestParam("content") String content,
                                 @RequestParam("picpath") String picpath){
        Result result = expertManagementService.updateKnowledge(id, title, content, picpath);
        return result;
    }

    //管理员删除知识内容接口
    @Log(module = "知识管理", operation = "管理员删除知识内容")
    @DeleteMapping("/DeleteKnoledge/{id}")
    public Result deleteKnoledge(@PathVariable("id") Integer id){
        Result result = expertManagementService.deleteKnowledge(id);
        return result;
    }

    /**
     * 问题管理相关接口
     */
    //分页查询所有问题
    @GetMapping("/selectAllQuestions/{pageNum}")
    public Result selectAllQuestions(@PathVariable Integer pageNum){
        PageInfo<Question> pageInfo = questionService.findPageQuestion(pageNum);
        return Result.success(pageInfo);
    }

    //根据问题标题或者内容的关键词分页搜索问题
    @GetMapping("/searchQuestionsByKeys")
    public Result searchQuestionsByKeys(@RequestParam String keyword,
                                        @RequestParam(defaultValue = "1") Integer pageNum){
        PageInfo<Question> question = questionService.searchPageQuestion(keyword, pageNum);
        return Result.success(question,"查询成功");
    }

    //管理员删除问题接口
    @Operation(summary = "管理员根据id删除问题")
    @Log(module = "问题管理", operation = "管理员删除问题")
    @DeleteMapping("/deleteQuestion/{id}")
    public Result deleteQuestion(@PathVariable Integer id) {
        Result result = expertManagementService.deleteQuestionById(id);
        return result;
    }

    /**
     * 预约管理相关接口
     * @param pageNum
     * @return
     */

    //分页查询所有专家的预约
    @Operation(summary = "管理员分页查询所有专家的预约列表")
    @GetMapping("/selectAllReservations/{pageNum}")
    public Result selectAllReservations(@PathVariable Integer pageNum) {
        PageInfo<Reserve> pageInfo = expertManagementService.findPageAllReservations(pageNum);
        return Result.success(pageInfo);
    }

    //按专家用户名模糊查询预约
    @Operation(summary = "管理员根据专家用户名分页模糊查询预约列表")
    @GetMapping("/selectReservationByExpert/{expertName}/{pageNum}")
    public Result selectReservationByExpert(@PathVariable String expertName,
                                            @PathVariable Integer pageNum) {
        PageInfo<Reserve> pageInfo = expertManagementService.findPageReservationByExpertName(expertName, pageNum);
        return Result.success(pageInfo);
    }

    //删除预约
    @Operation(summary = "管理员根据id删除预约")
    @Log(module = "预约管理", operation = "管理员删除预约")
    @DeleteMapping("/deleteReservation/{id}")
    public Result deleteReservation(@PathVariable Integer id) {
        Result result = expertManagementService.deleteReservationById(id);
        return result;
    }

    /**
     * 融资管理相关接口
     */
    //查询银行信息接口（调用）
    @GetMapping("/selectAllBanks")
    public Result selectAllBanks(){
        List<Bank> banks = bankService.selectAllBank();
        return Result.success(banks);
    }

    //查询所有贷款详情列表接口（分页，每页10条）
    @Operation(summary = "管理员分页查询所有贷款列表")
    @GetMapping("/selectAllFinances/{pageNum}")
    public Result selectAllFinances(@PathVariable Integer pageNum){
        PageInfo<Finance> pageInfo = financeService.findPageAllFinance(pageNum);
        return Result.success(pageInfo, "查询成功");
    }

    // 根据真实姓名模糊查询贷款接口（分页，每页10条）
    @Operation(summary = "管理员根据真实姓名分页模糊查询贷款")
    @GetMapping("/searchFinanceByRealName")
    public Result searchFinanceByRealName(@RequestParam String realName,
                                          @RequestParam(defaultValue = "1") Integer pageNum){
        PageInfo<Finance> pageInfo = financeService.searchFinanceByRealName(realName, pageNum);
        return Result.success(pageInfo, "查询成功");
    }

    //修改贷款状态接口（0：未贷款 1：已贷款 2：已还款）
    @Operation(summary = "管理员修改贷款状态")
    @Log(module = "贷款管理", operation = "管理员修改贷款状态")
    @PutMapping("/updateFinanceStatus")
    public Result updateFinanceStatus(@RequestParam Integer financeId,
                                      @RequestParam Integer status){
        return financeService.updateFinanceStatus(financeId, status);
    }

    //删除贷款接口
    @Operation(summary = "管理员根据id删除贷款")
    @Log(module = "贷款管理", operation = "管理员删除贷款")
    @DeleteMapping("/deleteFinance/{financeId}")
    public Result deleteFinance(@PathVariable Integer financeId){
        return financeService.deleteFinance(financeId);
    }

}
