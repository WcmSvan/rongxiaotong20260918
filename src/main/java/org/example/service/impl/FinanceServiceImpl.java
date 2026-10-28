package org.example.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.mapper.FinanceMapper;
import org.example.common.Result;
import org.example.pojo.entity.Finance;
import org.example.pojo.entity.Intention;
import org.example.service.FinanceService;
import org.example.util.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FinanceServiceImpl implements FinanceService {
    @Autowired
    private FinanceMapper financeMapper;

    //管理员分页查询时每页条数
    private static final int PAGE_SIZE = 10;

    /**
     * 从 BaseContext 获取当前登录用户名，未登录则抛异常
     */
    private String requireCurrentUser() {
        String userName = BaseContext.getCurrentUser();
        if (userName == null) {
            throw new RuntimeException("请先登录");
        }
        return userName;
    }

    /**
     * 查询当前登录用户的融资意向
     */
    @Override
    public List<Intention> selectIntentionByName() {
        String userName = requireCurrentUser();
        return financeMapper.selectIntentionByName(userName);
    }

    /**
     * 添加融资意向：userName 由后端从登录态覆盖，防止伪造
     */
    @Override
    public void insertIntentionByName(Intention intention) {
        String userName = requireCurrentUser();
        // 用登录态覆盖前端传入的 userName，防止越权
        intention.setUserName(userName);
        Date now = new Date();
        intention.setCreateTime(now);
        intention.setUpdateTime(now);
        financeMapper.insertIntention(intention);
    }

    /**
     * 修改融资意向：userName 由后端从登录态覆盖，SQL 用 id+userName 双条件防越权
     */
    @Override
    public void updateIntentionByName(Intention intention) {
        String userName = requireCurrentUser();
        if (intention.getId() == null) {
            throw new RuntimeException("意向id不能为空");
        }
        // 用登录态覆盖前端传入的 userName
        intention.setUserName(userName);
        intention.setUpdateTime(new Date());
        financeMapper.updateIntention(intention);
    }

    /**
     * 根据意向id删除单条融资意向：userName 从登录态取，SQL 用 id+userName 双条件防越权
     */
    @Override
    public void deleteIntentionById(Integer id) {
        String userName = requireCurrentUser();
        financeMapper.deleteIntentionById(id, userName);
    }

    /**
     * 查询当前登录用户的推荐融资人（相同城市+相同农作物）
     */
    @Override
    public List<Intention> selectRecommendByName() {
        String userName = requireCurrentUser();
        return financeMapper.selectRecommendByName(userName);
    }

    /**
     * 根据贷款id查询贷款详情：校验记录存在性和归属，只能查本人的，管理员可查任意
     */
    @Override
    public Finance selectFinanceById(Integer financeId) {
        if (financeId == null) {
            throw new RuntimeException("贷款id不能为空");
        }
        Finance finance = financeMapper.selectFinanceById(financeId);
        if (finance == null) {
            throw new RuntimeException("贷款记录不存在");
        }
        String currentUser = requireCurrentUser();
        String role = BaseContext.getCurrentRole();
        // 非本人且非管理员，拒绝访问
        if (!currentUser.equals(finance.getOwnName()) && !"admin".equals(role)) {
            throw new RuntimeException("无权查看该贷款记录");
        }
        return finance;
    }

    /**
     * 根据用户名查询贷款申请列表（含组合贷款多条记录）
     */
    @Override
    public List<Finance> selectMyFinance() {
        String ownName = requireCurrentUser();
        return financeMapper.selectFinanceListByOwnName(ownName);
    }


    /**
     * 添加单人贷款申请：ownName 由后端从登录态设置，status 初始为 0(未贷款/待审核)
     */
    @Override
    public void addFinance(Finance finance) {
        String ownName = requireCurrentUser();
        // 用登录态覆盖 ownName，防止伪造申请人
        finance.setOwnName(ownName);
        // 初始状态：0=未贷款/待审核
        if (finance.getStatus() == null) {
            finance.setStatus(0);
        }
        Date now = new Date();
        finance.setCreateTime(now);
        finance.setUpdateTime(now);
        financeMapper.insertFinance(finance);
    }

    /**
     * 添加组合贷款申请：ownName 由后端从登录态设置，status 初始为 0
     */
    @Override
    public void addFinanceMulti(List<Finance> finances) {
        String ownName = requireCurrentUser();
        Date now = new Date();
        // 用登录态覆盖 ownName，设置初始 status 和时间，防止伪造
        for (Finance finance : finances) {
            finance.setOwnName(ownName);
            if (finance.getStatus() == null) {
                finance.setStatus(0);
            }
            finance.setCreateTime(now);
            finance.setUpdateTime(now);
        }
        financeMapper.insertFinancesMulti(finances);
    }

    /**
     * 管理员：分页查询所有贷款申请列表
     */
    @Override
    public PageInfo<Finance> findPageAllFinance(Integer pageNum) {
        //页码兜底：为空或小于1时按第1页处理
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        PageHelper.startPage(pageNum, PAGE_SIZE);
        List<Finance> list = financeMapper.selectAllFinance();
        PageInfo<Finance> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    /**
     * 管理员：根据真实姓名关键字分页模糊查询贷款申请列表
     */
    @Override
    public PageInfo<Finance> searchFinanceByRealName(String realName, Integer pageNum) {

        if (realName == null || realName.trim().isEmpty()) {
            return findPageAllFinance(pageNum);
        }
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        PageHelper.startPage(pageNum, PAGE_SIZE);
        List<Finance> list = financeMapper.selectFinanceByRealName(realName.trim());
        PageInfo<Finance> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    /**
     * 管理员：修改贷款状态（0：未贷款 1：已贷款 2：已还款）
     */
    @Override
    public Result updateFinanceStatus(Integer financeId, Integer status) {
        if (financeId == null) {
            return Result.error("贷款id不能为空");
        }
        if (status == null || status < 0 || status > 2) {
            return Result.error("贷款状态不合法，只能为0(未贷款)、1(已贷款)、2(已还款)");
        }
        //先校验记录是否存在
        Finance finance = financeMapper.selectFinanceById(financeId);
        if (finance == null) {
            return Result.error("贷款记录不存在");
        }
        int rows = financeMapper.updateFinanceStatus(financeId, status);
        if (rows > 0) {
            return Result.success("贷款状态修改成功");
        }
        return Result.error("贷款状态修改失败");
    }

    /**
     * 管理员：根据贷款id删除贷款记录
     */
    @Override
    public Result deleteFinance(Integer financeId) {
        if (financeId == null) {
            return Result.error("贷款id不能为空");
        }
        //先校验记录是否存在
        Finance finance = financeMapper.selectFinanceById(financeId);
        if (finance == null) {
            return Result.error("贷款记录不存在");
        }
        int rows = financeMapper.deleteFinanceById(financeId);
        if (rows > 0) {
            return Result.success("贷款记录删除成功");
        }
        return Result.error("贷款记录删除失败");
    }
}
