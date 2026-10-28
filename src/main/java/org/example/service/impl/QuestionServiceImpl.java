package org.example.service.impl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.common.Result;
import org.example.mapper.QuestionMapper;
import org.example.pojo.entity.Question;
import org.example.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//农业问答模块 业务实现类
@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    QuestionMapper questionMapper;


    private Integer pageSize = 12;

    //获取所有问题
    //分页查询问题列表
    @Override
    public PageInfo<Question> findPageQuestion(Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        List<Question> questionList = questionMapper.getQuestionList();
        PageInfo<Question> pageInfo = new PageInfo<>(questionList);
        return pageInfo;
    }

    //根据问题标题或者问题内容模糊查询问题列表
    //分页模糊查询问题列表
    @Override
    public PageInfo<Question> searchPageQuestion(String keyword, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        List<Question> questionList = questionMapper.searchQuestion(keyword);
        PageInfo<Question> pageInfo = new PageInfo<>(questionList);
        return pageInfo;
    }

    //根据id获取问题详情
    @Override
    public Result getQuestionById(Integer id) {
        if (id == null){
            return Result.error("查询失败，问题id不能为空");
        }
        Question question = questionMapper.getQuestionById(id);
        if (question == null){
            return Result.error("查询失败，不存在id为" + id + "的问题");
        }
        return Result.success(question);
    }


}


