package org.example.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.common.Result;
import org.example.pojo.dto.QuestionDTO;
import org.example.pojo.entity.Knowledge;
import org.example.pojo.entity.Question;
import org.example.pojo.entity.Reserve;
import org.example.service.ExpertManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "专家（个人）管理模块接口")
@RestController
@RequestMapping("/expert/management")
public class ExpertManagementController {
    //注入一个业务层对象，用于调用业务层方法
    @Autowired
    ExpertManagementService expertManagementService;

/*
问答管理
*/
    //个人信息--专家管理--问答管理--展示当前登录的专家的问答列表
    //分页查询当前登录专家的问答列表（每页10条）
    @GetMapping("/QAmanagement/page/{pageNum}")
    public Result getQuestionListByPage(@PathVariable Integer pageNum) {
        PageInfo<Question> pageInfo = expertManagementService.findPageExpertQuestion(pageNum);
        if (pageInfo == null) {
            return Result.error("当前用户未登录或不存在");
        }
        return Result.success(pageInfo);
    }

    //个人信息--专家管理--问答管理--   根据 问题标题 或者 问题内容 模糊查询问题列表
    //根据关键词分页模糊查询问题列表（每页10条）
    @GetMapping("/searchQuestionByKeyword/page/{pageNum}")
    public Result searchQuestionListByPage(@RequestParam("keyword") String keyword,
                                           @PathVariable Integer pageNum) {
        PageInfo<Question> pageInfo = expertManagementService.searchPageQuestionList(keyword, pageNum);
        if (pageInfo == null) {
            return Result.error("当前用户未登录或不存在");
        }
        return Result.success(pageInfo);
    }

    //个人信息--专家管理--问答管理-- 回答 用户向当前登录专家提问 的问题
    //根据提问者用户id 回答问题
    @PostMapping("/answerquestion")
    public Result answerQuestion(@RequestBody QuestionDTO questionDTO) {
        Result result = expertManagementService.answerQuestion(questionDTO.getId(), questionDTO.getAnswer());
        return result;
    }

    //个人信息--专家管理--问答管理--根据问题id删除当前登录专家被提问的问题
    @DeleteMapping("/deletequestion")
    public Result deleteQuestion(@RequestParam("id") Integer id) {
        Result result = expertManagementService.deleteQuestionById(id);
        return result;
    }

/*
预约管理
*/
    //个人信息--专家管理--预约管理--展示当前登录专家的预约列表
    //分页查询当前登录专家的预约列表（每页10条）
    @GetMapping("/reservationmanagement/page/{pageNum}")
    public Result getReservationListByPage(@PathVariable Integer pageNum) {
        PageInfo<Reserve> pageInfo = expertManagementService.findPageExpertReservation(pageNum);
        if (pageInfo == null) {
            return Result.error("当前用户未登录或不存在");
        }
        return Result.success(pageInfo);
    }

    //个人信息--专家管理--预约管理--根据 预约id/提问者名称/作物名称/状态 查询当前登录专家的预约列表
    //根据关键词分页查询预约列表（每页10条）
    @GetMapping("/searchReservationByKeyword/page/{pageNum}")
    public Result searchReservationListByPage(@RequestParam("keyword") String keyword,
                                              @PathVariable Integer pageNum) {
        PageInfo<Reserve> pageInfo = expertManagementService.searchPageReservationList(keyword, pageNum);
        if (pageInfo == null) {
            return Result.error("当前用户未登录或不存在");
        }
        return Result.success(pageInfo);
    }


    //个人信息--专家管理--预约管理--根据预约id 回复 预约了当前登录专家的预约
    @PostMapping("/replyreservation")
    public Result replyReservation(@RequestParam("id") Integer id,
                                   @RequestParam("answer") String answer) {
        Result result = expertManagementService.replyReservation(id, answer);
        return result;
    }

/*
知识管理
*/
    //个人信息--专家管理--知识管理--展示当前登录专家发布的知识列表
    //分页查询当前登录专家的知识列表（每页10条）
    @GetMapping("/knowledgemanagement/page/{pageNum}")
    public Result getKnowledgeListByPage(@PathVariable Integer pageNum) {
        PageInfo<Knowledge> pageInfo = expertManagementService.findPageExpertKnowledge(pageNum);
        if (pageInfo == null) {
            return Result.error("当前用户未登录或不存在");
        }
        return Result.success(pageInfo);
    }


    //个人信息--专家管理--知识管理--根据关键词查询当前登录专家发布的知识列表
    //根据关键词分页查询知识列表（每页10条）
    @GetMapping("/searchKnowledgeByKeyword/page/{pageNum}")
    public Result searchKnowledgeListByPage(@RequestParam("keyword") String keyword,
                                            @PathVariable Integer pageNum) {
        PageInfo<Knowledge> pageInfo = expertManagementService.searchPageKnowledgeList(keyword, pageNum);
        if (pageInfo == null) {
            return Result.error("当前用户未登录或不存在");
        }
        return Result.success(pageInfo);
    }

    //个人信息--专家管理--知识管理--发布知识
    @PostMapping("/publishknowledge")
    public Result publishKnowledge(@RequestParam("title") String title,
                                   @RequestParam("content") String content,
                                   @RequestParam("picpath") String picpath) {
        Result result = expertManagementService.publishKnowledge(title, content, picpath);
        return result;
    }

    //个人信息--专家管理--知识管理--根据知识id修改当前登录专家发布的知识
    @PostMapping("/updateknowledge/{id}")
    public Result updateKnowledge(@PathVariable("id") Integer id,
                                  @RequestParam("title") String title,
                                  @RequestParam("content") String content,
                                  @RequestParam("picpath") String picpath) {
        System.out.println("id=" + id);
        Result result = expertManagementService.updateKnowledge(id, title, content, picpath);
        return result;
    }

    //个人信息--专家管理--知识管理--根据知识id删除当前登录专家发布的知识
    @DeleteMapping("/deleteknowledge/{id}")
    public Result deleteKnowledge(@PathVariable("id") Integer id) {
        Result result = expertManagementService.deleteKnowledge(id);
        return result;
    }
}