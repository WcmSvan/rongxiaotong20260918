package org.example.service.impl;

import org.example.common.Result;
import org.example.mapper.AddressMapper;
import org.example.pojo.dto.AddressDTO;
import org.example.pojo.entity.Address;
import org.example.service.AddressService;
import org.example.util.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class addAddressServiceImpl implements AddressService {

    @Autowired
    AddressMapper addressMapper;

    /**
     * 添加地址
     * @param addressDTO
     * @return
     */
    @Override
    public Result addAddressService(AddressDTO addressDTO) {
        //非空判断
        if(addressDTO.getConsignee() == null || addressDTO.getConsignee().isEmpty()){
            return Result.error("收货人不能为空");
        }

        if(addressDTO.getPhone() == null ||addressDTO.getPhone().isEmpty()){
            return Result.error("手机号不能为空");
        }

        if(addressDTO.getAddressDetail() == null || addressDTO.getAddressDetail().isEmpty()){
            return Result.error("地址详情不能为空");
        }

        //获取当前登录用户
        String loginUser = BaseContext.getCurrentUser();
        if(loginUser == null){
            return Result.error("用户未登录");
        }

        Address address = new Address();
        address.setOwnName(loginUser);
        address.setConsignee(addressDTO.getConsignee());
        address.setPhone(addressDTO.getPhone());
        address.setAddressDetail(addressDTO.getAddressDetail());


        int row = addressMapper.insertAddress(address);
        if(row != 1) {
            return Result.error("添加地址失败");
        }
            return Result.success("添加地址成功");

    }

    /**
     * 修改地址
     * @param addressDTO
     * @return
     */
    @Override
    public Result updateAddressService(AddressDTO addressDTO) {
        //1.id 非空校验
        if (addressDTO.getId() == null) {
            return Result.error("地址ID不能为空");
        }
        //2.其他字段非空判断
        if ((addressDTO.getConsignee() == null || addressDTO.getConsignee().isEmpty())){
            return Result.error("收货人不能为空");
        }
        if ((addressDTO.getPhone() == null || addressDTO.getPhone().isEmpty())){
            return Result.error("手机号不能为空");
        }
        if (addressDTO.getAddressDetail() == null || addressDTO.getAddressDetail().isEmpty()){
            return Result.error("地址详情不能为空");
        }

        //3.获取当前登录用户
        String loginUser = BaseContext.getCurrentUser();
        if(loginUser == null){
            return Result.error("用户未登录");
        }

        //4.修改前先查数据库，确认地址存在且归属当前用户
        Address existing = addressMapper.findById(addressDTO.getId());
        if (existing == null) {
            return Result.error("该地址不存在");
        }
        if (!loginUser.equals(existing.getOwnName())) {
            return Result.error("无权修改他人地址");
        }

        //5.执行修改
        Address address = new Address();
        address.setId(addressDTO.getId());
        address.setOwnName(loginUser);
        address.setConsignee(addressDTO.getConsignee());
        address.setPhone(addressDTO.getPhone());
        address.setAddressDetail(addressDTO.getAddressDetail());
        address.setIsDefault(addressDTO.getIsDefault());

        int row = addressMapper.updateAddress(address);
        if(row != 1) {
            return Result.error("修改地址失败");
        }
        return Result.success("修改地址成功");
    }

    /**
     * 删除地址
     * @param id
     * @return
     */
    @Override
    public Result deleteAddressService(Integer id) {
        //非空判断
        if(id == null){
            return Result.error("地址id不能为空");
        }
        int row = addressMapper.deleteAddress(id);
        if(row != 1) {
            return Result.error("删除地址失败");
        }
        return Result.success("删除地址成功");
    }

    /**
     * 查询当前登录用户的地址列表
     * @return
     */
    @Override
    public Result selectAddressService() {
        String loginUser = BaseContext.getCurrentUser();
        if(loginUser == null){
            return Result.error("用户未登录");
        }

        //强制用当前登录用户名查询，不允许前端指定 ownName 防越权
        List<Address> addressList = addressMapper.selectAddress(loginUser);
        if (addressList == null || addressList.isEmpty()) {
            return Result.error("该用户还没有添加地址");
        }
        return Result.success(addressList);
    }
}
