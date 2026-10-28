package org.example.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.common.Result;
import org.example.mapper.ExpertMapper;
import org.example.mapper.UserMapper;
import org.example.pojo.dto.QuestionDTO;
import org.example.pojo.dto.ReserveQueryDTO;
import org.example.pojo.entity.Expert;
import org.example.pojo.entity.Question;
import org.example.pojo.entity.Reserve;
import org.example.pojo.entity.User;
import org.example.service.ExpertService;
import org.example.util.CurrentUserUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//专家介绍模块 业务实现类
@Service
public class ExpertServiceImpl implements ExpertService {
    @Autowired
    ExpertMapper expertMapper;

    @Autowired
    UserMapper userMapper;

    private Integer pageSize = 12;

    //获取专家列表
    //分页查询专家列表
    @Override
    public PageInfo<Expert> findPageExpert(Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        List<Expert> experts = expertMapper.getExpertList();
        PageInfo<Expert> pageInfo = new PageInfo<>(experts);
        return pageInfo;
    }

    //根据专家用户名获取专家详细信息
    @Override
    public Result getExpertInfo(String expertUserName) {
        //判断专家用户名是否为空
        if (expertUserName == null || expertUserName.trim().isEmpty()) {
            return Result.error("专家用户名不能为空");
        }
        //判断专家用户名是否存在
        if (expertMapper.getExpertInfo(expertUserName) == null) {
            return Result.error("专家用户名不存在");
        }
        //根据专家用户名获取专家信息
        Expert expert = expertMapper.getExpertInfo(expertUserName);
        return Result.success(expert);
    }

    //新增 直接咨询  问题
    @Override
    public Result addConsultation(String expertName, QuestionDTO questionDTO) {
        //获取当前登录用户名
        String questioner = CurrentUserUtil.getCurrentUsername();
        if (questioner == null) {
            return Result.error("未登录，请先登录");
        }
        User user = userMapper.findByUsername(questioner);
        if (user == null) {
            return Result.error("当前登录用户不存在");
        }
        //把当前登录用户名作为提问者写入咨询信息
        questionDTO.setQuestioner(user.getUserName());

        //判断填写标题是否为空
        if (questionDTO.getTitle() == null || questionDTO.getTitle().trim().isEmpty()) {
            return Result.error("填写标题不能为空");
        }
        //判断填写问题是否为空
        if (questionDTO.getQuestion() == null || questionDTO.getQuestion().trim().isEmpty()) {
            return Result.error("填写问题不能为空");
        }
        //判断填写植物名称是否为空
        if (questionDTO.getPlantName() == null || questionDTO.getPlantName().trim().isEmpty()) {
            return Result.error("填写植物名称不能为空");
        }
        //判断填写手机号是否为空
        if (questionDTO.getPhone() == null || questionDTO.getPhone().trim().isEmpty()) {
            return Result.error("填写手机号不能为空");
        }
        //判断咨询的专家用户名是否为空
        if (questionDTO.getExpertUserName() == null || questionDTO.getExpertUserName().trim().isEmpty()) {
            return Result.error("咨询专家不能为空");
        }

        Question question = new Question();
        BeanUtils.copyProperties(questionDTO, question);

        question.setStatus(0);
        question.setExpertName(questionDTO.getExpertUserName());
        question.setQuestioner(questionDTO.getQuestioner());


        int rows = expertMapper.addConsultation(question);

        if (rows > 0) {
            return Result.success(rows, "咨询提交成功");
        }
        return Result.error("咨询提交失败");
    }

    //新增 预约咨询 问题
    @Override
    public Result addOfflineQuery(String expertName, ReserveQueryDTO resrtvequeryDTO) {
        //获取当前登录用户名
        String questioner = CurrentUserUtil.getCurrentUsername();
        if (questioner == null) {
            return Result.error("未登录，请先登录");
        }
        User user = userMapper.findByUsername(questioner);
        if (user == null) {
            return Result.error("当前登录用户不存在");
        }
        //把当前登录用户名作为提问者写入咨询信息
        resrtvequeryDTO.setQuestioner(user.getUserName());


        //判断填写面积是否为空
        if (resrtvequeryDTO.getArea() == null || resrtvequeryDTO.getArea().trim().isEmpty()) {
            return Result.error("填写面积不能为空");
        }
        //判断填写地址是否为空
        if (resrtvequeryDTO.getAddress() == null || resrtvequeryDTO.getAddress().trim().isEmpty()) {
            return Result.error("填写地址不能为空");
        }
        //判断填写植物名称是否为空
        if (resrtvequeryDTO.getPlantName() == null || resrtvequeryDTO.getPlantName().trim().isEmpty()) {
            return Result.error("填写农作物名称不能为空");
        }
        //判断填写手土壤条件是否为空
        if (resrtvequeryDTO.getSoilCondition() == null || resrtvequeryDTO.getSoilCondition().trim().isEmpty()) {
            return Result.error("填写土壤条件不能为空");
        }
        //判断填写农作物条件是否为空
        if (resrtvequeryDTO.getPlantCondition() == null || resrtvequeryDTO.getPlantCondition().trim().isEmpty()) {
            return Result.error("填写农作物条件不能为空");
        }
        //判断填写农作物详细信息是否为空
        if (resrtvequeryDTO.getPlantDetail() == null || resrtvequeryDTO.getPlantDetail().trim().isEmpty()) {
            return Result.error("填写农作物详细信息不能为空");
        }
        //判断填写手机号是否为空
        if (resrtvequeryDTO.getPhone() == null || resrtvequeryDTO.getPhone().trim().isEmpty()) {
            return Result.error("填写手机号不能为空");
        }

        Reserve reserve = new Reserve();
        BeanUtils.copyProperties(resrtvequeryDTO, reserve);

        reserve.setStatus(0);
        int rows = expertMapper.addOfflineQuery(reserve);
        if (rows > 0) {
            return Result.success(rows, "预约提交成功");
        }
        return Result.error("预约提交失败");
    }





}

