package org.example.service;

import com.github.pagehelper.PageInfo;
import org.example.common.Result;
import org.example.pojo.entity.Finance;
import org.example.pojo.entity.Intention;

import java.util.List;

public interface FinanceService {
    //查询当前登录用户的融资意向
    List<Intention> selectIntentionByName();

    //添加融资意向（userName 由后端从登录态设置）
    void insertIntentionByName(Intention intention);

    //修改融资意向（userName 由后端从登录态设置，防止越权）
    void updateIntentionByName(Intention intention);

    //根据意向id删除单条融资意向（归属用户由登录态校验）
    void deleteIntentionById(Integer id);

    //查询当前登录用户的推荐融资人（相同城市+相同农作物）
    List<Intention> selectRecommendByName();

    //根据贷款id查询贷款详情（校验归属：只能查本人的，管理员除外）
    Finance selectFinanceById(Integer financeId);

    //查询当前登录用户的贷款申请列表（含组合贷款多条记录，ownName 由 Service 层从登录态获取）
    List<Finance> selectMyFinance();

    //添加单人贷款申请（ownName 由后端从登录态设置）
    void addFinance(Finance finance);

    //添加组合贷款申请（ownName 由后端从登录态设置）
    void addFinanceMulti(List<Finance> finances);

    /**
     * 管理员：分页查询所有贷款申请列表
     * @param pageNum 页码，从1开始
     * @return 贷款申请分页数据
     */
    PageInfo<Finance> findPageAllFinance(Integer pageNum);

    /**
     * 管理员：根据真实姓名关键字分页模糊查询贷款申请列表
     * @param realName 真实姓名关键字
     * @param pageNum 页码，从1开始
     * @return 贷款申请分页数据
     */
    PageInfo<Finance> searchFinanceByRealName(String realName, Integer pageNum);

    /**
     * 管理员：修改贷款状态（0：未贷款 1：已贷款 2：已还款）
     * @param financeId 贷款记录id
     * @param status 新状态
     * @return 修改结果
     */
    Result updateFinanceStatus(Integer financeId, Integer status);

    /**
     * 管理员：根据贷款id删除贷款记录
     * @param financeId 贷款记录id
     * @return 删除结果
     */
    Result deleteFinance(Integer financeId);
}
