package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.entity.Expert;
import org.example.pojo.entity.Question;
import org.example.pojo.entity.Reserve;

import java.util.List;

@Mapper
public interface ExpertMapper {
    //展示所有专家
    List<Expert> getExpertList();

    //根据用户名获取专家信息
    Expert getExpertInfo(String expertUserName);

    //添加咨询
    int addConsultation(Question question);

    //添加线下咨询
    int addOfflineQuery(Reserve reserve);

    //新增专家（管理员添加用户且角色为 expert 时联动调用）
    int insertExpert(Expert expert);

    //根据用户名删除专家（管理员将专家改为非专家角色时联动调用）
    int deleteExpertByUserName(String userName);

    //按用户名更新专家的 realName / phone（管理员修改专家信息时同步）
    int updateExpertProfile(Expert expert);

}
