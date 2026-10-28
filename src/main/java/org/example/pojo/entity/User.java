package org.example.pojo.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class User {
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
}
