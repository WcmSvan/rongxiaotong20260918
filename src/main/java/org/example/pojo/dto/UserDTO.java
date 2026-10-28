package org.example.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

//用来接收前端传递过来的注册参数
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserDTO {
    private String userName;// 用户名
    private String password;// 密码
    private String nickName;// 昵称
    private String phone;// 手机号
    private String identityNum;// 身份证号
    private String address;// 地址
    private String role;// 角色
    private LocalDateTime createTime;// 创建时间
    private LocalDateTime updateTime;// 更新时间
    private Integer integral;// 积分
    private Integer credit;// 信誉
    private String avatar;// 头像
    private String realName;// 真实姓名
    private String profession;// 专家职业（role=expert 时填，写入 tb_expert）
    private String position;// 专家职务（role=expert 时填，写入 tb_expert）
    private String belong;// 专家所属单位（role=expert 时填，写入 tb_expert）
    private String confirmPassword;// 确认密码
    private String newPassword;// 新密码

    public String getConfirmPassword() {
        return confirmPassword;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
