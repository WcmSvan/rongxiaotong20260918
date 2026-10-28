package org.example.service.impl;

import org.example.common.Result;
import org.example.mapper.DiscussMapper;
import org.example.mapper.KnowledgeMapper;
import org.example.pojo.entity.Discuss;
import org.example.pojo.entity.Knowledge;
import org.example.service.DiscussService;
import org.example.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
//知识模块的 讨论(评论)业务实现类
@Service
public class DiscussServiceImpl implements DiscussService {

    @Autowired
    DiscussMapper discussMapper;

    @Autowired
    KnowledgeMapper knowledgeMapper;


    //获取所有评论(讨论)
    @Override
    public Result getAllDiscussList() {
        List<Discuss> discussList = discussMapper.selectAllDiscuss();
        return Result.success(discussList);
    }

    //添加评论（讨论）
    @Override
    public Result addDiscuss(Integer knowledgeId, String content) {
        //获取当前登录的用户名
        String ownName = CurrentUserUtil.getCurrentUsername();
        //判断当前登录用户是否存在
        if (ownName == null) {
            return Result.error("未登录，请先登录");
        }
        //判断知识是否存在
        Knowledge knowledge = knowledgeMapper.selectKnowledgeById(knowledgeId);
        if (knowledge == null) {
            return Result.error("知识不存在");
        }
        //插入评论
        Discuss discuss = new Discuss();
        discuss.setKnowledgeId(knowledgeId);
        discuss.setOwnName(ownName);
        discuss.setContent(content);
        discuss.setCreateTime(LocalDateTime.now());

        int rows = discussMapper.insertSelective(discuss);
        if (rows == 0) {
            return Result.error("评论失败");
        }
        //返回成功
        return Result.success("评论成功");
    }


}



















