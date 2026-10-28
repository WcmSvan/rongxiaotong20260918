package org.example.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.common.Result;
import org.example.pojo.entity.Question;
import org.example.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Tag(name = "农业问答模块接口")
@RestController
@RequestMapping("/expert")
public class QuestionController {

    //注入一个业务层对象，用于调用业务层方法
    @Autowired
    QuestionService questionService;


    //获取全部农业问答问题列表
    //分页查询农业问答问题列表（每页10条）
    @GetMapping("/questionlist/page/{pageNum}")
    public Result getQuestionListByPage(@PathVariable Integer pageNum) {
        PageInfo<Question> pageInfo = questionService.findPageQuestion(pageNum);
        return Result.success(pageInfo);
    }

    //根据关键词搜索农业问答问题
    //根据关键词分页搜索农业问答问题（每页10条）
    @GetMapping("/searchquestion/page/{pageNum}")
    public Result searchQuestionByPage(@RequestParam("keyword") String keyword,
                                       @PathVariable Integer pageNum) {
        PageInfo<Question> pageInfo = questionService.searchPageQuestion(keyword, pageNum);
        return Result.success(pageInfo);
    }

    //根据id获取问题详情
    @GetMapping("/selectQuestion/{id}")
    public Result selectQuestion(@PathVariable Integer id){
        Result result = questionService.getQuestionById(id);
        return result;
    }




}
