package org.example.service;

import org.example.common.Result;
import org.example.pojo.dto.DiscussDTO;

//农业知识下 讨论（评论）模块 业务接口
public interface DiscussService {
    //获取所有讨论
    Result getAllDiscussList();

    //根据知识id查询知识
    Result addDiscuss(Integer knowledgeId, String content);
}
