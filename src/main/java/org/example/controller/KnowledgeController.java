package org.example.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.common.Result;
import org.example.pojo.entity.Knowledge;
import org.example.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Tag(name = "农业知识模块接口")
@RestController
@RequestMapping("/expert")

public class KnowledgeController {
    //注入一个业务层对象，用于调用业务层方法
    @Autowired
    KnowledgeService knowledgeService;


    //获取全部农业知识列表
    //分页查询农业知识列表（每页10条）
    @GetMapping("/knowledgelist/page/{pageNum}")
    public Result getKnowledgeListByPage(@PathVariable Integer pageNum) {
        PageInfo<Knowledge> pageInfo = knowledgeService.findPageKnowledge(pageNum);
        return Result.success(pageInfo);
    }

    // 根据关键词搜索农业知识
    // 根据关键词分页搜索农业知识（每页10条）
    @GetMapping("/searchknowledge/page/{pageNum}")
    public Result searchKnowledgeByPage(@RequestParam("keyword") String keyword,
                                        @PathVariable Integer pageNum) {
        PageInfo<Knowledge> pageInfo = knowledgeService.searchPageKnowledge(keyword, pageNum);
        return Result.success(pageInfo);
    }

    //根据知识id获取知识详情
    @GetMapping("/knowledge/{id}")
    public Result getKnowledgeById(@PathVariable("id") Integer id) {
        Result result = knowledgeService.getKnowledgeById(id);
        return result;
    }


}
