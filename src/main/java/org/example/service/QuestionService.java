package org.example.service;

import com.github.pagehelper.PageInfo;
import org.example.common.Result;
import org.example.pojo.entity.Question;

//农业问答模块 业务接口
public interface QuestionService {
    //获取问题列表
    //分页查询问题列表
    PageInfo<Question> findPageQuestion(Integer pageNum);

    //根据问题标题或者问题内容的关键词模糊查询问题列表
    //分页模糊查询问题列表
    PageInfo<Question> searchPageQuestion(String keyword, Integer pageNum);

    //根据id获取问题详情
    Result getQuestionById(Integer id);


}
