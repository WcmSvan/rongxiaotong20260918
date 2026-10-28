package org.example.service;

import com.github.pagehelper.PageInfo;
import org.example.common.Result;
import org.example.pojo.dto.QuestionDTO;
import org.example.pojo.dto.ReserveQueryDTO;
import org.example.pojo.entity.Expert;

//专家介绍模块 业务接口
public interface ExpertService {
    //获取所有专家列表
    //分页查询专家列表
    PageInfo<Expert> findPageExpert(Integer pageNum);

    //根据专家用户名获取专家详情
    Result getExpertInfo(String expertUserName);

    //添加直接咨询
    Result addConsultation(String expertName, QuestionDTO questionDTO);

    //添加线下预约
    Result addOfflineQuery(String expertName, ReserveQueryDTO resrtvequeryDTO);


}

