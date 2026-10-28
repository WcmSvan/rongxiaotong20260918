package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.entity.Discuss;

import java.util.List;

@Mapper
public interface DiscussMapper {
    //获取所有讨论
    List<Discuss> selectAllDiscuss();

    //添加讨论
    int insertSelective(Discuss discuss);
}
