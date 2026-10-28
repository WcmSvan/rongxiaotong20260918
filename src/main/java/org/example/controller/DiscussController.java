package org.example.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.common.Result;
import org.example.service.DiscussService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "农业知识 下的讨论（评论）模块接口")
@RestController
@RequestMapping("/expert")
public class DiscussController {
    @Autowired
    DiscussService discussService;

    //获取讨论列表
    @GetMapping("/discusslist")
    public Result getDiscussList() {
        Result result = discussService.getAllDiscussList();
        return result;
    }

    //发布评论,用query传参
    @PostMapping("/addDiscuss")
    public Result addDiscuss(@RequestParam Integer knowledgeId,
                             @RequestParam String content) {
        Result result = discussService.addDiscuss(knowledgeId, content);
        return result;
    }

}
