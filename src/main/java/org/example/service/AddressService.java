package org.example.service;


import org.example.common.Result;
import org.example.pojo.dto.AddressDTO;

public interface AddressService {

    //添加地址接口
    Result addAddressService(AddressDTO addressDTO);

    //修改地址接口
    Result updateAddressService(AddressDTO addressDTO);

    //删除地址接口
    Result deleteAddressService(Integer id);

    //查询当前登录用户的地址列表
    Result selectAddressService();
}
