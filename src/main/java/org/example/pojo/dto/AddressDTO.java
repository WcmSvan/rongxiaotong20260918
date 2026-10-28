package org.example.pojo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class AddressDTO {
    private Integer id;//地址ID
    private String ownName;//货主名称
    private String consignee;//收货人
    private String addressDetail;//地址详情
    private String phone;//手机号
    private Boolean isDefault;//是否默认地址
}
