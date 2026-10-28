package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.entity.Address;

import java.util.List;

@Mapper
public interface AddressMapper {

    /**
     * 添加地址
     * @param address
     * @return
     */
    int insertAddress(Address address);

    /**
     * 修改地址
     * @param address
     * @return
     */
    int updateAddress(Address address);

    /**
     * 删除地址
     * @param id
     * @return
     */
    int deleteAddress(Integer id);

    /**
     * 根据用户名称查询地址
     * @param ownName
     * @return
     */
    List<Address> selectAddress(String ownName);

    /**
     * 根据id查询地址（用于修改/删除前校验归属权）
     * @param id
     * @return
     */
    Address findById(Integer id);
}
