package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.dto.KnowledgeDTO;
import org.example.pojo.entity.Knowledge;

import java.util.List;

@Mapper
public interface KnowledgeMapper {
    //展示所有知识
    List<Knowledge> findAllKnowledge();

    //根据知识id获取知识详情
    Knowledge selectKnowledgeById(Integer id);

    //根据知识标题查询知识
    List<Knowledge> selectKnowledgeByKeyword(String keyword);

}
