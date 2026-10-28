package org.example.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.common.Result;
import org.example.mapper.ExpertManagementMapper;
import org.example.mapper.KnowledgeMapper;
import org.example.mapper.UserMapper;
import org.example.pojo.dto.KnowledgeDTO;
import org.example.pojo.entity.Knowledge;
import org.example.pojo.entity.Question;
import org.example.pojo.entity.Reserve;
import org.example.pojo.entity.User;
import org.example.service.ExpertManagementService;
import org.example.util.BaseContext;
import org.example.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
//专家（个人）管理模块业务实现类
public class ExpertManagementServiceImpl implements ExpertManagementService {
    @Autowired
    ExpertManagementMapper expertManagementMapper;

    @Autowired
    UserMapper userMapper;
    @Autowired
    KnowledgeMapper knowledgeMapper;

    private Integer pageSize = 10;
/*
问答管理
 */
    //获取当前登录的专家问题列表
    //分页查询专家问题列表
    @Override
    public PageInfo<Question> findPageExpertQuestion(Integer pageNum) {
        String expertName = CurrentUserUtil.getCurrentUsername();
        if (expertName == null) {
            return null;
        }
        User user = userMapper.findByUsername(expertName);
        if (user == null) {
            return null;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Question> questionList = expertManagementMapper.getExpertQuestionList(user.getUserName());
        PageInfo<Question> pageInfo = new PageInfo<>(questionList);
        return pageInfo;
    }

    //根据 问题标题或者问题内容模糊查询问题列表
    //分页模糊查询专家问题列表
    @Override
    public PageInfo<Question> searchPageQuestionList(String keyword, Integer pageNum) {
        String expertName = CurrentUserUtil.getCurrentUsername();
        if (expertName == null) {
            return null;
        }
        User user = userMapper.findByUsername(expertName);
        if (user == null) {
            return null;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Question> questionList = expertManagementMapper.searchQuestionList(keyword, user.getUserName());
        PageInfo<Question> pageInfo = new PageInfo<>(questionList);
        return pageInfo;
    }

    //专家回答问题
    //根据提问者用户id 回答问题
    @Override
    public Result answerQuestion(Integer id, String answer) {

        //判断id是否为空
        if (id == null){
            return Result.error("问题id不能为空");
        }
        //判断answer是否为空
        if (answer == null || answer.isEmpty()){
            return Result.error("回答内容不能为空");
        }
        //获取当前登录专家用户名
        String expertName = CurrentUserUtil.getCurrentUsername();
        if (expertName == null) {
            return Result.error("当前用户未登录");
        }
        //查询该问题，校验问题是否存在
        Question question = expertManagementMapper.getQuestionById(id);
        if (question == null) {
            return Result.error("回答失败，不存在id为" + id + "的问题");
        }
        //校验该问题是否是提问给当前登录专家的
        if (!expertName.equals(question.getExpertName())) {
            return Result.error("回答失败，该问题不属于当前登录专家");
        }
        //把回答写入 tb_question，并把 状态置status为1 已回答
        int rows = expertManagementMapper.answerQuestionById(id, answer);
        if (rows > 0) {
            return Result.success(rows, "回答成功");
        }
        return Result.error("回答失败");
    }

    //根据id删除问题
    @Override
    public Result deleteQuestionById(Integer id) {
        //判断id是否为空
        if (id == null){
            return Result.error("删除失败，问题id不能为空");
        }
        //获取当前登录专家用户名
        String expertName = CurrentUserUtil.getCurrentUsername();
        String role = BaseContext.getCurrentRole();
        if (expertName == null) {
            return Result.error("当前用户未登录");
        }
        //查询该问题，校验问题是否存在
        Question question = expertManagementMapper.getQuestionById(id);
        if (question == null) {
            return Result.error("删除失败，不存在id为" + id + "的问题");
        }
        //校验归属：非管理员只能删除提问给当前登录专家的问题，管理员可删除任意问题
        if (!"admin".equals(role) && !expertName.equals(question.getExpertName())) {
            return Result.error("删除失败，该问题不属于当前登录专家");
        }
        //从 tb_question 中删除该问题
        int rows = expertManagementMapper.deleteQuestionById(id);
        if (rows > 0) {
            return Result.success(rows, "删除成功");
        }
        return Result.error("删除失败");
    }

/*
预约管理
 */
    //获取当前登录的专家的预约列表
    //分页查询专家预约列表
    @Override
    public PageInfo<Reserve> findPageExpertReservation(Integer pageNum) {
        String expertName = CurrentUserUtil.getCurrentUsername();
        if (expertName == null) {
            return null;
        }
        User user = userMapper.findByUsername(expertName);
        if (user == null) {
            return null;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Reserve> reserveList = expertManagementMapper.getExpertReservationList(user.getUserName());
        PageInfo<Reserve> pageInfo = new PageInfo<>(reserveList);
        return pageInfo;
    }

    //根据  预约id/提问者名称/作物名称/状态  查询预约列表
    //分页模糊查询专家预约列表
    @Override
    public PageInfo<Reserve> searchPageReservationList(String keyword, Integer pageNum) {
        String expertName = CurrentUserUtil.getCurrentUsername();
        if (expertName == null) {
            return null;
        }
        User user = userMapper.findByUsername(expertName);
        if (user == null) {
            return null;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Reserve> reserveList = expertManagementMapper.searchReservationList(keyword, user.getUserName());
        PageInfo<Reserve> pageInfo = new PageInfo<>(reserveList);
        return pageInfo;
    }

    //根据id回复预约
    @Override
    public Result replyReservation(Integer id, String answer) {
        //判断id是否为空
        if (id == null){
            return Result.error("回复失败，预约id不能为空");
        }
        //判断answer是否为空
        if (answer == null || answer.isEmpty()){
            return Result.error("回复内容不能为空");
        }
        //获取当前登录专家用户名
        String expertName = CurrentUserUtil.getCurrentUsername();
        if (expertName == null) {
            return Result.error("当前用户未登录");
        }
        //查询该预约，校验预约是否存在
        Reserve reserve = expertManagementMapper.getReservationById(id);
        if (reserve == null) {
            return Result.error("回复失败，不存在id为" + id + "的预约");
        }
        //校验该预约是否是提问给当前登录专家的
        if (!expertName.equals(reserve.getExpertName())) {
            return Result.error("回复失败，该预约不属于当前登录专家");
        }
        //把回复写入 tb_reserve，并把 状态置status为1 已回复
        int rows = expertManagementMapper.replyReservationById(id, answer);
        if (rows > 0) {
            return Result.success(rows, "回复成功");
        }
        return Result.error("回复失败");

        }
/*
知识管理
 */
    //获取当前登录专家的知识列表
    //分页查询专家知识列表
    @Override
    public PageInfo<Knowledge> findPageExpertKnowledge(Integer pageNum) {
        String expertName = CurrentUserUtil.getCurrentUsername();
        if (expertName == null) {
            return null;
        }
        User user = userMapper.findByUsername(expertName);
        if (user == null) {
            return null;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Knowledge> knowledgeList = expertManagementMapper.getExpertKnowledgeList(user.getUserName());
        PageInfo<Knowledge> pageInfo = new PageInfo<>(knowledgeList);
        return pageInfo;
    }

    //根据关键词搜索知识列表
    //分页模糊查询专家知识列表
    @Override
    public PageInfo<Knowledge> searchPageKnowledgeList(String keyword, Integer pageNum) {
        String expertName = CurrentUserUtil.getCurrentUsername();
        if (expertName == null) {
            return null;
        }
        User user = userMapper.findByUsername(expertName);
        if (user == null) {
            return null;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Knowledge> knowledgeList = expertManagementMapper.searchKnowledgeList(keyword, user.getUserName());
        PageInfo<Knowledge> pageInfo = new PageInfo<>(knowledgeList);
        return pageInfo;
    }


    //发布知识
    @Override
    public Result publishKnowledge(String title, String content, String picpath) {
        //获取当前登录专家用户名
        String expertName = CurrentUserUtil.getCurrentUsername();
        if (expertName == null) {
            return Result.error("当前用户未登录");
        }
        //获取当前专家
        User user = userMapper.findByUsername(expertName);
        if(user == null) {
            return Result.error("当前用户不存在");
        }
        //创建知识对象
        KnowledgeDTO knowledge = new KnowledgeDTO();
        knowledge.setTitle(title);
        knowledge.setContent(content);
        knowledge.setPicPath(picpath);
        knowledge.setOwnName(user.getUserName());
        knowledge.setCreateTime(LocalDateTime.now());
        knowledge.setUpdateTime(LocalDateTime.now());
        //将知识对象写入数据库
        int rows = expertManagementMapper.insertKnowledge(knowledge);
        if (rows > 0) {
            return Result.success(rows, "发布成功");
        }
        return Result.error("发布失败");
    }

    //修改知识
    @Override
    public Result updateKnowledge(Integer id, String title, String content, String picpath) {

        //根据id查询知识
        Knowledge knowledge = knowledgeMapper.selectKnowledgeById(id);
        if (knowledge == null) {
            return Result.error("修改失败，不存在id为" + id + "的知识");
        }
        //归属校验：非管理员只能修改本人发布的知识
        String currentUser = CurrentUserUtil.getCurrentUsername();
        String role = BaseContext.getCurrentRole();
        if (!"admin".equals(role)
                && (currentUser == null || !currentUser.equals(knowledge.getOwnName()))) {
            return Result.error("修改失败，无权修改他人发布的知识");
        }
        //判断title是否为空
        if (title == null || title.isEmpty()) {
            return Result.error("修改失败，标题不能为空");
        }
        //判断content是否为空
        if (content == null || content.isEmpty()) {
            return Result.error("修改失败，内容不能为空");
        }
        //图片可选：未重新上传时保留原图片路径
        if (picpath == null || picpath.isEmpty()) {
            picpath = knowledge.getPicPath();
        }

        // 把更新的知识写入数据库
        int rows = expertManagementMapper.updateKnowledgeById(id, title, content, picpath);
        if (rows > 0) {
            return Result.success(rows, "修改成功");
        }
        return Result.error("修改失败");
    }

    //删除知识
    @Override
    public Result deleteKnowledge(Integer id) {
        // 根据id查询知识
        Knowledge knowledge = knowledgeMapper.selectKnowledgeById(id);
        if (knowledge == null) {
            return Result.error("删除失败，不存在id为" + id + "的知识");
        }
        //归属校验：非管理员只能删除本人发布的知识
        String currentUser = CurrentUserUtil.getCurrentUsername();
        String role = BaseContext.getCurrentRole();
        if (!"admin".equals(role)
                && (currentUser == null || !currentUser.equals(knowledge.getOwnName()))) {
            return Result.error("删除失败，无权删除他人发布的知识");
        }
        // 把知识从数据库中删除
        int rows = expertManagementMapper.deleteKnowledgeById(id);
        if (rows > 0) {
            return Result.success(rows, "删除成功");
        }
        return Result.error("删除失败");
    }

    //管理员根据专家用户名分页模糊查询预约列表
    @Override
    public PageInfo<Reserve> findPageReservationByExpertName(String expertName, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        List<Reserve> reserveList = expertManagementMapper.getReservationListByExpertNameLike(expertName);
        PageInfo<Reserve> pageInfo = new PageInfo<>(reserveList);
        return pageInfo;
    }

    //管理员分页查询所有专家的预约列表
    @Override
    public PageInfo<Reserve> findPageAllReservations(Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        List<Reserve> reserveList = expertManagementMapper.getAllReservations();
        PageInfo<Reserve> pageInfo = new PageInfo<>(reserveList);
        return pageInfo;
    }

    //管理员根据预约id删除预约
    @Override
    public Result deleteReservationById(Integer id) {
        //判断id是否为空
        if (id == null){
            return Result.error("删除失败，预约id不能为空");
        }
        //查询该预约，校验预约是否存在
        Reserve reserve = expertManagementMapper.getReservationById(id);
        if (reserve == null) {
            return Result.error("删除失败，不存在id为" + id + "的预约");
        }
        //从 tb_reserve 中删除该预约
        int rows = expertManagementMapper.deleteReservationById(id);
        if (rows > 0) {
            return Result.success(rows, "删除成功");
        }
        return Result.error("删除失败");
    }
}