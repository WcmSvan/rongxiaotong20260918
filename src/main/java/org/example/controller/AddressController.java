package org.example.controller;

import org.example.common.Result;
import org.example.pojo.dto.AddressDTO;
import org.example.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    //添加地址接口
    @PostMapping("/addAddress")
    public Result address(@RequestBody AddressDTO addressDTO){
        Result result = addressService.addAddressService(addressDTO);
        return result;
    }

    //修改地址接口
    @PostMapping("/updateAddress")
    public Result updateAddress(@RequestBody AddressDTO addressDTO){
        Result result = addressService.updateAddressService(addressDTO);
        return  result;
    }

    //删除地址接口
    @PostMapping("/deleteAddress")
    public Result deleteAddress(Integer id){
        Result result =  addressService.deleteAddressService(id);
        return result;
    }

    //查询当前登录用户的地址列表（强制用 token 里的用户名，不允许前端指定 ownName 防越权）
    @GetMapping("/selectAddress")
    public Result selectAddress(){
        Result result = addressService.selectAddressService();
        return result;
    }

}
