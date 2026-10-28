package org.example.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.common.Result;
import org.example.pojo.dto.QuestionDTO;
import org.example.pojo.dto.ReserveQueryDTO;
import org.example.pojo.entity.Expert;
import org.example.service.ExpertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "专家介绍模块接口")
@RestController
@RequestMapping("/expert")
public class ExpertController {

    //注入一个业务层对象，用于调用业务层方法
    @Autowired
    ExpertService expertService;

    //获取专家列表（展示在 专家助力--专家介绍 下）
    //分页查询专家列表（每页10条）
    @GetMapping("/expertlist/page/{pageNum}")
    public Result getExpertListByPage(@PathVariable Integer pageNum) {
        PageInfo<Expert> pageInfo = expertService.findPageExpert(pageNum);
        return Result.success(pageInfo);
    }

    //根据用户名获取专家详细信息（展示在 专家助力--专家介绍--专家详情 下）
    @GetMapping("/expertinfo/{expertUserName}")
    public Result getExpertInfo(@PathVariable String expertUserName) {
        return expertService.getExpertInfo(expertUserName);
    }

     //新增 直接咨询  问题（展示在 专家助力--专家介绍--专家详情（直接咨询按钮） 下）
    @PostMapping("/addconsultation/{expertName}")
    public Result addConsultation(@PathVariable String expertName,
                                  @RequestBody QuestionDTO questionDTO) {
        questionDTO.setExpertUserName(expertName);
        return expertService.addConsultation(expertName, questionDTO);
    }

    //新增 线下预约（展示在 专家助力--专家介绍--专家详情（线下预约按钮） 下）
    @PostMapping("/offlinequery/{expertName}")
    public Result offline(@PathVariable String expertName,
                          @RequestBody ReserveQueryDTO resrtvequeryDTO) {
        resrtvequeryDTO.setExpertName(expertName);
        return expertService.addOfflineQuery(expertName, resrtvequeryDTO);
    }




}
