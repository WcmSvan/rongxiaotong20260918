package org.example.controller;

import org.example.annotation.Log;
import org.example.common.Result;
import org.example.intercepter.RequireRole;
import org.example.pojo.dto.UserDTO;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    //处理前端的注册请求
    @GetMapping("/checkIsRegistered")
    public Result checkIsRegistered(String userName, String phone) {
        System.out.println(userName);
        System.out.println(phone);
        Result result = userService.isRegistered(userName, phone);
        return result;
    }

    //用户注册接口
    @Log(module = "登录认证", operation = "用户注册")
    @PostMapping("/register")
    public Result register(@RequestBody UserDTO userDTO) {
        Result result = userService.registerService(userDTO);
        return result;
    }

    //用户登录接口
    @Log(module = "登录认证", operation = "用户登录")
    @PostMapping("/login")
    public Result login(@RequestBody UserDTO userDTO) {
        Result result = userService.loginService(userDTO);
        return result;
    }

    //管理员添加用户接口
    @Log(module = "用户管理", operation = "管理员添加用户")
    @RequireRole("admin")
    @PostMapping("/addUser")
    public Result adminAdd(@RequestBody UserDTO userDTO) {
        Result result = userService.adminAddService(userDTO);
        return result;
    }

    //根据用户名删除用户接口
    //confirm=false（默认）：先检查关联数据，有则返回提示让前端确认；confirm=true：确认强制删除
    @Log(module = "用户管理", operation = "管理员删除用户")
    @RequireRole("admin")
    @GetMapping("/deleteUser")
    public Result deleteUser(String userName,
                             @RequestParam(required = false, defaultValue = "false") boolean confirm) {
        Result result = userService.deleteUserService(userName, confirm);
        return result;
    }

    //管理员修改用户信息接口
    @Log(module = "用户管理", operation = "管理员修改用户信息")
    @RequireRole("admin")
    @PostMapping("/updateUser")
    public Result adminUpdate(@RequestBody UserDTO userDTO) {
        Result result = userService.adminUpdateService(userDTO);
        return result;
    }

    //用户修改个人信息接口
    @PostMapping("/updateSelf")
    public Result updateSelf(@RequestBody UserDTO userDTO) {
        Result result = userService.updateSelfService(userDTO);
        return result;
    }

    //专家填写专家信息接口
    @Log(module = "用户管理", operation = "专家填写专家信息")
    @PostMapping("/fillExpertInfo")
    public Result fillExpertInfo(@RequestBody UserDTO userDTO) {
        Result result = userService.fillExpertInfoService(userDTO);
        return result;
    }

     //根据用户名或者昵称分页查询用户接口（默认每页10条）
    @GetMapping("/findByUser")
    public Result findByUser(String userName, String nickName,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        Result result = userService.findByUser(userName, nickName, pageNum, pageSize);
        return result;
    }

    //分页查询所有用户接口（默认每页15条）
    @RequireRole("admin")
    @GetMapping("/findAllUser")
    public Result findAllUser(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "15") Integer pageSize) {
        return userService.findAllUser(pageNum, pageSize);
    }

    //根据角色查询用户接口（默认每页10条）
    @RequireRole("admin")
    @GetMapping("/findByRole")
    public Result findByRole(String role,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        Result result = userService.findByRole(role, pageNum, pageSize);
        return result;
    }



    //用户修改密码接口
    @Log(module = "用户管理", operation = "用户修改密码")
    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestBody UserDTO userDTO) {
        Result result = userService.updatePasswordService(userDTO);
        return result;
    }

}
