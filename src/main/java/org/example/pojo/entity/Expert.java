package org.example.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

public class Expert {
    private String userName;
    private String realName;
    private String avatar;
    private String phone;
    private String profession;
    private String position;
    private String belong;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName == null ? null : realName.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public void setProfession(String profession) {
        this.profession = profession == null ? null : profession.trim();
    }

    public void setPosition(String position) {
        this.position = position == null ? null : position.trim();
    }

    public void setBelong(String belong) {
        this.belong = belong == null ? null : belong.trim();
    }
}
