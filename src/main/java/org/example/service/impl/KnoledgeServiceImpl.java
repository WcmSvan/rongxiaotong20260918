package org.example.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.common.Result;
import org.example.mapper.KnowledgeMapper;
import org.example.mapper.UserMapper;
import org.example.pojo.dto.KnowledgeDTO;
import org.example.pojo.entity.Knowledge;
import org.example.pojo.entity.User;
import org.example.service.KnowledgeService;
import org.example.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//农业知识模块 业务实现类
@Service //表示这个类受Spring管理 同时表示这是一个服务层的类
public class KnoledgeServiceImpl implements KnowledgeService {

    @Autowired
    KnowledgeMapper knowledgeMapper;

    private Integer pageSize = 12;
    //获取所有知识
    //分页查询知识列表
    @Override
    public PageInfo<Knowledge> findPageKnowledge(Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        List<Knowledge> knowledgeList = knowledgeMapper.findAllKnowledge();
        PageInfo<Knowledge> pageInfo = new PageInfo<>(knowledgeList);
        return pageInfo;
    }

    //搜索知识
    //分页搜索知识
    @Override
    public PageInfo<Knowledge> searchPageKnowledge(String keyword, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        List<Knowledge> knowledgeList = knowledgeMapper.selectKnowledgeByKeyword(keyword);
        PageInfo<Knowledge> pageInfo = new PageInfo<>(knowledgeList);
        return pageInfo;
    }

    //根据id获取知识详情
    @Override
    public Result getKnowledgeById(Integer id) {
        // 根据id获取知识详情
        if (id == null) { // 如果id为空，返回错误信息
            return Result.error("id不能为空");
        }
        Knowledge knowledge = knowledgeMapper.selectKnowledgeById(id);
        return Result.success(knowledge);
    }
}
