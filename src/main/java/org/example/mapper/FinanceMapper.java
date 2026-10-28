package org.example.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.pojo.entity.Finance;
import org.example.pojo.entity.Intention;

import java.util.List;

@Mapper
public interface FinanceMapper {
    /**
     * 根据用户名查询融资意向
     * @param name 用户名（tb_financing_intention.user_name）
     * @return 该用户的融资意向列表
     */
    List<Intention> selectIntentionByName(@Param("name") String name);

    /**
     * 新增一条融资意向
     * @param intention 融资意向信息（user_name 标识归属用户）
     * @return 受影响行数
     */
    int insertIntention(Intention intention);

    /**
     * 根据意向id和用户名修改融资意向
     * 同时带 id 和 user_name 两个条件，防止修改到其他用户的意向
     * @param intention 含 id、userName 及要更新的字段
     * @return 受影响行数（0 表示该意向不存在或不属于该用户）
     */
    int updateIntention(Intention intention);

    /**
     * 根据意向id删除单条融资意向，并校验归属用户
     * @param id 融资意向主键
     * @param userName 所属用户名，防止删除到其他用户的意向
     * @return 受影响行数（0 表示该意向不存在或不属于该用户）
     */
    int deleteIntentionById(@Param("id") Integer id, @Param("userName") String userName);

    /**
     * 根据用户名查询推荐融资人：与该用户意向拥有相同城市(address)和相同农作物(item)的其他用户意向
     * @param name 当前用户名
     * @return 推荐融资人意向列表（不含本人意向，已去重）
     */
    List<Intention> selectRecommendByName(@Param("name") String name);

    /**
     * 根据贷款id查询贷款详情
     * @param financeId 贷款记录主键
     * @return 贷款记录，null 表示不存在
     */
    Finance selectFinanceById(@Param("financeId") Integer financeId);

    /**
     * 根据申请人用户名查询贷款申请列表（含组合贷款多条记录）
     * @param ownName 申请人用户名
     * @return 贷款申请列表，按创建时间倒序
     */
    List<Finance> selectFinanceListByOwnName(@Param("ownName") String ownName);

    /**
     * 新增单人贷款申请
     * @param finance 贷款申请信息（own_name 标识申请人）
     * @return 受影响行数
     */
    int insertFinance(Finance finance);

    /**
     * 新增组合贷款申请
     * @param finances 贷款申请信息列表（own_name 标识申请人）
     * @return 受影响行数
     */
    void insertFinancesMulti(List<Finance> finances);

    /**
     * 管理员：查询所有贷款申请列表（不受用户名限制，配合 PageHelper 分页）
     * @return 全部贷款申请列表，按创建时间倒序
     */
    List<Finance> selectAllFinance();

    /**
     * 管理员：根据真实姓名模糊查询贷款申请列表（配合 PageHelper 分页）
     * @param realName 真实姓名关键字
     * @return 命中的贷款申请列表，按创建时间倒序
     */
    List<Finance> selectFinanceByRealName(@Param("realName") String realName);

    /**
     * 管理员：根据贷款id修改贷款状态（0：未贷款 1：已贷款 2：已还款）
     * @param financeId 贷款记录主键
     * @param status 新状态
     * @return 受影响行数（0 表示记录不存在）
     */
    int updateFinanceStatus(@Param("financeId") Integer financeId,
                            @Param("status") Integer status);

    /**
     * 管理员：根据贷款id删除贷款记录
     * @param financeId 贷款记录主键
     * @return 受影响行数（0 表示记录不存在）
     */
    int deleteFinanceById(@Param("financeId") Integer financeId);
}
