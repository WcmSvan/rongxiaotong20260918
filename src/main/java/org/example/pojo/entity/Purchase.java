package org.example.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Purchase {

    private Integer purchaseId;

    private String ownName;

    private Integer purchaseType;

    private BigDecimal totalPrice;

    private String address;

    private Integer purchaseStatus;

    private Date createTime;

    private Date updateTime;

    public void setOwnName(String ownName) {
        this.ownName = ownName == null ? null : ownName.trim();
    }
    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }


}