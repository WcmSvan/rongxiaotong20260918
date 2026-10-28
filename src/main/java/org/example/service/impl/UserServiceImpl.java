package org.example.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.common.Result;
import org.example.mapper.ExpertMapper;
import org.example.mapper.UserMapper;
import org.example.pojo.dto.UserDTO;
import org.example.pojo.entity.Expert;
import org.example.pojo.entity.User;
import org.example.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.util.BaseContext;
import org.example.util.JwtUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    ExpertMapper expertMapper;
    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 检查用户是否已注册
     *
     * @param userName
     * @param phone
     * @return
     */
    @Override
    public Result isRegistered(String userName, String phone) {
        //判断用户名是否已注册
        if (userName == null || userName.isEmpty()) {
            return Result.error("用户名不能为空");
        }
        //判断手机号是否已注册
        if (phone == null || phone.isEmpty()) {
            return Result.error("手机号不能为空");
        }
        User user = userMapper.findByUsernameAndPhone(userName, phone);
        if (user != null) {
            return Result.error("账号已存在");
        }
        return Result.success("该账号可注册");
    }


    /**
     * 用户注册接口
     *
     * @param userDTO
     * @return
     */
    @Override
    public Result registerService(UserDTO userDTO) {
        //判断用户是否已注册
        Result r = isRegistered(userDTO.getUserName(), userDTO.getPhone());
        if (r.getCode() != 200) {
            return r;
        }
        //判断密码是否为空
        String password = userDTO.getPassword();
        if (password == null || password.length() < 6) {
            return Result.error("密码长度不能少于6位");
        }
        //2、校验：密码 和确认密码是否一致
        if (!password.equals(userDTO.getConfirmPassword())) {
            return Result.error("两次输入的密码不一致");
        }

        User user = new User();
        user.setUserName(userDTO.getUserName());
        user.setNickName(userDTO.getNickName());
        user.setPhone(userDTO.getPhone());

        //4、密码加密！！
        String encodePwd = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encodePwd);

        //封装其他参数
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        //将数据添加到数据库
        int row = userMapper.insertUser(user);
        if (row != 1) {
            return Result.error("注册失败");
        }


        return Result.success("注册成功");
    }

    /**
     * 用户登录接口
     *
     * @param userDTO
     * @return
     */
    @Override
    public Result loginService(UserDTO userDTO) {
        //非空判断
        if (userDTO.getUserName() == null || userDTO.getUserName().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
            return Result.error("密码不能为空");
        }

        //根据用户名查询用户是否存在
        User user = userMapper.findByUsername(userDTO.getUserName());
        if (user == null) {
            return Result.error("该账号还没有注册");
        }
        //判断密码是否正确
        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            return Result.error("密码错误，请重新输入密码");
        }
        //校验通过，生成携带用户名和角色的token（有效期8小时），便于后续拦截器校验角色与获取当前登录用户
        String token = JwtUtil.generateToken(user.getUserName(), user.getRole());

        //登录成功后手动写入，供操作日志切面记录本次登录的操作人与角色
        BaseContext.setCurrentUser(user.getUserName());
        BaseContext.setCurrentRole(user.getRole());

        //创建一个Map，用于存储登录成功后的数据（存储到前端localStorage）
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userName", user.getUserName());
        data.put("role", user.getRole());
        return Result.success(data, "登录成功");
    }

    /**
     * 管理员添加用户接口
     * 角色为 expert 时，事务内同步写入 tb_expert，保证两表一致
     *
     * @param userDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)  //保证两个表要么都成功要么都失败。
    public Result adminAddService(UserDTO userDTO) {
        //非空判断
        if (userDTO.getUserName() == null || userDTO.getUserName().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
            return Result.error("密码不能为空");
        }
        if (userDTO.getNickName() == null || userDTO.getNickName().isEmpty()) {
            return Result.error("昵称不能为空");
        }
        if (userDTO.getPhone() == null || userDTO.getPhone().isEmpty()) {
            return Result.error("手机号不能为空");
        }
        if (userDTO.getRole() == null || userDTO.getRole().isEmpty()) {
            return Result.error("角色不能为空");
        }
        //判断用户是否已存在
        User user = userMapper.findByUsername(userDTO.getUserName());
        if (user != null) {
            return Result.error("该账号已存在");
        }
        //身份证号非必填；填写时才查重
        if (userDTO.getIdentityNum() != null && !userDTO.getIdentityNum().isEmpty()
                && userMapper.countByIdentityNum(userDTO.getIdentityNum(), null) > 0) {
            return Result.error("该身份证号已存在");
        }
        //封装用户信息
        User user2 = new User();
        BeanUtils.copyProperties(userDTO, user2);
        user2.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user2.setCreateTime(LocalDateTime.now());
        user2.setUpdateTime(LocalDateTime.now());
        //将数据添加到数据库
        int row = userMapper.addUser(user2);
        if (row != 1) {
            return Result.error("添加失败");
        }
        //角色为 expert 时同步写入 tb_expert
        if ("expert".equals(userDTO.getRole())) {
            Expert expert = new Expert();
            expert.setUserName(user2.getUserName());
            expert.setRealName(user2.getRealName() != null ? user2.getRealName() : "");
            expert.setPhone(user2.getPhone());
            //专业
            expert.setProfession(userDTO.getProfession() != null && !userDTO.getProfession().isEmpty()
                    ? userDTO.getProfession() : "暂未填写");
            //职务
            expert.setPosition(userDTO.getPosition() != null && !userDTO.getPosition().isEmpty()
                    ? userDTO.getPosition() : "暂未填写");
            //单位
            expert.setBelong(userDTO.getBelong() != null && !userDTO.getBelong().isEmpty()
                    ? userDTO.getBelong() : "暂未填写");
            int er = expertMapper.insertExpert(expert);
            if (er != 1) {
                throw new RuntimeException("同步专家表失败");
            }
        }
        return Result.success("添加成功");
    }

    /**
     * 管理员修改用户接口
     * @param userDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result adminUpdateService(UserDTO userDTO) {
        //非空判断
        if (userDTO == null) {
            return Result.error("用户信息不能为空");
        }
        if (userDTO.getUserName() == null || userDTO.getUserName().isEmpty()) {
            return Result.error("用户名不能为空");
        }

        //判断用户是否存在
        User user = userMapper.findByUsername(userDTO.getUserName());
        if (user == null) {
            return Result.error("该用户不存在");
        }
        //记录原角色，用于判断是否需要联动 tb_expert
        String oldRole = user.getRole();
        String newRole = userDTO.getRole();

        //判断身份证号的唯一性
        if (userDTO.getIdentityNum() != null && !userDTO.getIdentityNum().isEmpty()
                && userMapper.countByIdentityNum(userDTO.getIdentityNum(), userDTO.getUserName()) > 0) {
            return Result.error("该身份证号已存在，请正确输入自己的身份证号码");
        }
        //判断手机号的唯一性
        if (userDTO.getPhone() != null && !userDTO.getPhone().isEmpty()
                && userMapper.countByPhone(userDTO.getPhone(), userDTO.getUserName()) > 0) {
            return Result.error("该手机号已存在，请正确输入自己的手机号码");
        }
        //密码修改后加密
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        //封装用户信息
        User user2 = new User();
        BeanUtils.copyProperties(userDTO, user2);
        //更新时间
        user2.setUpdateTime(LocalDateTime.now());

        //更新用户信息到数据库
        int row = userMapper.updateUser(user2);

        if (row != 1) {
            return Result.error("修改失败");
        }
        //判断修改前角色是否为专家
        boolean wasExpert = "expert".equals(oldRole);
        //判断修改后角色是否为专家
        boolean isExpertNow = newRole != null && "expert".equals(newRole);
        if (isExpertNow && !wasExpert) {
            //普通用户 → 专家：写入 tb_expert
            Expert expert = new Expert();
            expert.setUserName(user2.getUserName());
            expert.setRealName(userDTO.getRealName() != null ? userDTO.getRealName()
                    : (user.getRealName() != null ? user.getRealName() : ""));
            expert.setPhone(userDTO.getPhone() != null ? userDTO.getPhone() : user.getPhone());
            expert.setProfession(userDTO.getProfession() != null && !userDTO.getProfession().isEmpty()
                    ? userDTO.getProfession() : "暂未填写");
            expert.setPosition(userDTO.getPosition() != null && !userDTO.getPosition().isEmpty()
                    ? userDTO.getPosition() : "暂未填写");
            expert.setBelong(userDTO.getBelong() != null && !userDTO.getBelong().isEmpty()
                    ? userDTO.getBelong() : "暂未填写");
            int er = expertMapper.insertExpert(expert);
            if (er != 1) {
                throw new RuntimeException("同步专家表失败");
            }
        } else if (!isExpertNow && wasExpert) {
            //专家 → 非专家：从 tb_expert 删除
            expertMapper.deleteExpertByUserName(user2.getUserName());
        } else if (isExpertNow) {
            //专家 → 专家：同步 realName / phone
            Expert expert = new Expert();
            expert.setUserName(user2.getUserName());
            expert.setRealName(userDTO.getRealName() != null ? userDTO.getRealName() : user.getRealName());
            expert.setPhone(userDTO.getPhone() != null ? userDTO.getPhone() : user.getPhone());
            expertMapper.updateExpertProfile(expert);
        }
        return Result.success("修改成功");
    }

    /**
     * 用户修改个人信息
     * 用户名取自当前登录态，禁止修改角色、积分、信誉
     * @param userDTO
     * @return
     */
    @Override
    public Result updateSelfService(UserDTO userDTO) {
        //非空判断
        if (userDTO == null) {
            return Result.error("用户信息不能为空");
        }
        //用户名以当前登录用户为准，防止越权修改他人信息
        String loginUserName = BaseContext.getCurrentUser();
        if (loginUserName == null || loginUserName.isEmpty()) {
            return Result.error("未获取到登录用户，请重新登录");
        }

        //判断用户是否存在
        User user = userMapper.findByUsername(loginUserName);
        if (user == null) {
            return Result.error("该用户不存在");
        }

        //判断身份证号的唯一性
        if (userDTO.getIdentityNum() != null && !userDTO.getIdentityNum().isEmpty()
                && userMapper.countByIdentityNum(userDTO.getIdentityNum(), loginUserName) > 0) {
            return Result.error("该身份证号已存在，请正确输入自己的身份证号码");
        }
        //判断手机号的唯一性
        if (userDTO.getPhone() != null && !userDTO.getPhone().isEmpty()
                && userMapper.countByPhone(userDTO.getPhone(), loginUserName) > 0) {
            return Result.error("该手机号已存在，请正确输入自己的手机号码");
        }
        //密码修改后加密
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        userDTO.setUserName(loginUserName);

        //封装用户信息
        User user2 = new User();
        BeanUtils.copyProperties(userDTO, user2);
        //更新时间
        user2.setUpdateTime(LocalDateTime.now());

        //更新用户信息到数据库
        int row = userMapper.updateUser(user2);

        if (row != 1) {
            return Result.error("修改失败");
        }
        return Result.success("修改成功");
    }

    /**
     * 根据用户名或昵称查询用户（分页）
     *
     * @param userName
     * @param nickName
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public Result findByUser(String userName, String nickName, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.findByUser(userName, nickName);
        if (userList == null || userList.isEmpty()) {
            return Result.error("没有找到相关用户信息");
        }

        PageInfo<User> pageInfo = new PageInfo<>(userList);
        return Result.success(pageInfo);
    }


    /**
     * 查询所有用户（分页）
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public Result findAllUser(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.selectUserList();
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        return Result.success(pageInfo);
    }

    /**
     * 根据角色查询用户（分页）
     *
     * @param role
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public Result findByRole(String role, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.findByRole(role);
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        return Result.success(pageInfo);
    }

    /**
     * 删除用户
     *
     * @param userName
     * @param confirm
     * @return
     */
    @Override
    public Result deleteUserService(String userName, boolean confirm) {
        //判断用户名是否为空
        if(userName == null || userName.isEmpty()){
            return Result.error("删除失败，用户名不能为空");
        }
        //confirm=false时先检查关联数据：是否发布过商品、是否有订单、是否发布过知识
        if (!confirm) {
            // 查询用户信息
            User user = userMapper.findByUsername(userName);
            if (user == null) {
                return Result.error("用户不存在");
            }
            String role = user.getRole();

            // 查询订单数量（所有角色都会检查）
            int purchaseCount = userMapper.countPurchaseByUserName(userName);
            // 查询发布商品数量（所有角色都会检查）
            int orderCount = userMapper.countOrderByUserName(userName);
            // 仅专家角色查询知识数量
            int knowledgeCount = "expert".equals(role) ? userMapper.countKnowledgeByUserName(userName) : 0;

            // 创建一个存放字符串 String 类型**的动态数组集合
            List<String> messages = new ArrayList<>();
            // 所有角色：有订单则提示
            if (purchaseCount > 0) {
                messages.add("存在订单" + purchaseCount + "条");
            }
            // 所有角色：有商品则提示
            if (orderCount > 0) {
                messages.add("已发布商品" + orderCount + "条");
            }
            // 仅专家角色：有知识则额外提示
            if ("expert".equals(role) && knowledgeCount > 0) {
                messages.add("发布知识" + knowledgeCount + "条");
            }

            if (!messages.isEmpty()) {
                return Result.error("该用户" + String.join("、", messages) + "，删除后相关数据将受影响，是否确认删除？");
            }
        }
        //执行删除操作
        int row = userMapper.deleteUser(userName);
        if(row != 1){
            return Result.error("删除失败");
        }
        return Result.success("删除成功");
    }

    /**
     * 专家填写专家信息接口
     * 用于专家补充/完善自己的专业资料，与用户基础信息解耦
     *
     * @param userDTO
     * @return
     */
    @Override
    public Result fillExpertInfoService(UserDTO userDTO) {
        //1.获取当前登录用户名（由拦截器从token解析后存入BaseContext）
        String userName = BaseContext.getCurrentUser();
        if (userName == null || userName.isEmpty()) {
            return Result.error("请先登录");
        }
        if (userDTO == null) {
            return Result.error("专家信息不能为空");
        }

        //2.校验当前登录用户是否存在且为专家
        User user = userMapper.findByUsername(userName);
        if (user == null) {
            return Result.error("该用户不存在");
        }
        if (!"expert".equals(user.getRole())) {
            return Result.error("当前用户不是专家，无法填写专家信息");
        }

        //3.校验专家记录是否存在于 tb_expert
        Expert expert = expertMapper.getExpertInfo(userName);
        if (expert == null) {
            return Result.error("专家信息不存在，请联系管理员");
        }

        //4.至少填写一项专家信息
        if ((userDTO.getProfession() == null || userDTO.getProfession().isEmpty())
                && (userDTO.getPosition() == null || userDTO.getPosition().isEmpty())
                && (userDTO.getBelong() == null || userDTO.getBelong().isEmpty())) {
            return Result.error("请至少填写一项专家信息（职业/职务/所属单位）");
        }

        //5.更新到 tb_expert 表
        Expert expert2 = new Expert();
        expert2.setUserName(userName);
        expert2.setProfession(userDTO.getProfession());
        expert2.setPosition(userDTO.getPosition());
        expert2.setBelong(userDTO.getBelong());
        int row = expertMapper.updateExpertProfile(expert2);
        if (row != 1) {
            return Result.error("填写失败");
        }
        return Result.success("填写成功");
    }


    /**
     * 用户修改密码接口
     *
     * @param userDTO
     * @return
     */
    @Override
    public Result updatePasswordService(UserDTO userDTO) {
        //1.获取当前登录用户名（由拦截器从token解析后存入BaseContext）
        String userName = BaseContext.getCurrentUser();
        if (userName == null || userName.isEmpty()) {
            return Result.error("请先登录");
        }

        User user = userMapper.findByUsername(userName);
        if (user == null) {
            return Result.error("该用户不存在");
        }

        //旧密码（页面：当前密码）
        String oldPasswordInput = userDTO.getPassword();
        //新密码
        String newPassword = userDTO.getNewPassword();
        //确认新密码
        String confirmPassword = userDTO.getConfirmPassword();

        //校验旧密码
        if (!passwordEncoder.matches(oldPasswordInput, user.getPassword())) {
            return Result.error("旧密码错误");
        }
        //校验新密码长度
        if(newPassword == null || newPassword.length() <6){
            return Result.error("新密码长度不能少于6位");
        }
        //两次新密码是否一致
        if(!newPassword.equals(confirmPassword)){
            return Result.error("两次输入的新密码不一致");
        }
        //新密码不能等于旧密码
        if(passwordEncoder.matches(newPassword, user.getPassword())){
            return Result.error("新密码不能与旧密码相同");
        }

        //设置加密后的新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        //执行数据库更新
        int row = userMapper.updateUser(user);
        if(row !=1){
            return Result.error("密码修改失败");
        }
        return Result.success("密码修改成功");
    }


}
