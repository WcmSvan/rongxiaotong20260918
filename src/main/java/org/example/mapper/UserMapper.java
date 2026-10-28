package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.pojo.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * 根据用户名和手机号查询用户
     * @param userName
     * @param phone
     * @return
     */
    User findByUsernameAndPhone(String userName, String phone);

    /**
     * 插入用户
     * @param user
     * @return
     */
    int insertUser(User user);

    /**
     * 根据用户名查询用户
     * @param userName
     * @return
     */
    User findByUsername(String userName);

    /**
     * 统计使用该身份证号的用户数量；excludeUserName 非空时排除该用户（更新资料时排除自身）。
     * 用 count 存在性判断替代 selectOne，避免历史重复数据触发 TooManyResultsException。
     * @param identityNum
     * @param excludeUserName
     * @return
     */
    int countByIdentityNum(@Param("identityNum") String identityNum,
                           @Param("excludeUserName") String excludeUserName);

    /**
     * 管理员添加用户
     * @param user2
     * @return
     */
    int addUser(User user2);

    /**
     * 统计使用该手机号的用户数量；excludeUserName 非空时排除该用户（更新资料时排除自身）。
     * @param phone
     * @param excludeUserName
     * @return
     */
    int countByPhone(@Param("phone") String phone,
                     @Param("excludeUserName") String excludeUserName);

    /**
     * 更新用户信息
     * @param user2
     * @return
     */
    int updateUser(User user2);

    /**
     * 根据用户名和昵称查询用户
     * @param userName
     * @param nickName
     * @return
     */
    List<User> findByUser(@Param("userName") String userName, @Param("nickName") String nickName);

    /**
     * 查询用户列表
     * @return
     */
    List<User> selectUserList();

    /**
     * 根据角色查询用户
     * @param role
     * @return
     */
    List<User> findByRole(String role);

    /**
     * 根据用户名删除用户
     * @param userName
     * @return
     */
    int deleteUser(String userName);

    /**
     * 统计该用户名下发布的商品（收购单）数量
     * @param userName
     * @return
     */
    int countPurchaseByUserName(String userName);

    /**
     * 统计该用户名下的订单数量
     * @param userName
     * @return
     */
    int countOrderByUserName(String userName);

    /**
     * 统计该用户名下发布的知识数量
     * @param userName
     * @return
     */
    int countKnowledgeByUserName(String userName);
}
