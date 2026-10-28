package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.pojo.entity.Question;

import java.util.List;
@Mapper
public interface QuestionMapper {

    //展示所有咨询
    List<Question> getQuestionList();

    //根据咨询标题查询咨询
    List<Question> searchQuestion(String keyword);

    //根据咨询id获取咨询详情
    Question getQuestionById(Integer id);
}
