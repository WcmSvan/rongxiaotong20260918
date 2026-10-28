package org.example.service;

import org.example.common.Result;
import org.example.pojo.dto.UserDTO;

public interface UserService {
    //用户注册接口
    Result registerService(UserDTO userDTO);

    //检查用户是否已注册
    Result isRegistered(String userName, String phone);

    //用户登录接口
    Result loginService(UserDTO userDTO);

    //管理员添加用户接口
    Result adminAddService(UserDTO userDTO);

    //管理员修改用户信息接口
    Result adminUpdateService(UserDTO userDTO);

    //用户修改个人信息接口（只能修改自己的资料）
    Result updateSelfService(UserDTO userDTO);

    //根据用户名或者昵称分页查询用户接口
    Result findByUser(String userName, String nickName, Integer pageNum, Integer pageSize);

    //根据角色查询用户接口（默认每页10条）
    Result findByRole(String role, Integer pageNum, Integer pageSize);

    //修改密码接口
    Result updatePasswordService(UserDTO userDTO);

    //分页查询所有用户接口
    Result findAllUser(Integer pageNum, Integer pageSize);

    //根据用户名删除用户接口（confirm=true时强制删除，否则先检查关联数据）
    Result deleteUserService(String userName, boolean confirm);

    //专家填写专家信息接口
    Result fillExpertInfoService(UserDTO userDTO);
}
