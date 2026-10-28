package org.example.service;

import com.github.pagehelper.PageInfo;
import org.example.common.Result;
import org.example.pojo.entity.Knowledge;
import org.example.pojo.entity.Question;
import org.example.pojo.entity.Reserve;

//专家（个人）管理模块 业务接口
public interface ExpertManagementService {

//问题管理
    //获取专家问题列表
    //分页查询专家问题列表
    PageInfo<Question> findPageExpertQuestion(Integer pageNum);

    //根据问题标题或者问题内容的关键词模糊查询问题列表
    //分页模糊查询专家问题列表
    PageInfo<Question> searchPageQuestionList(String keyword, Integer pageNum);

    //回答问题
    Result answerQuestion(Integer id, String answer);

    //删除问题
    Result deleteQuestionById(Integer id);

    /**
     * 管理员根据专家用户名分页模糊查询该专家的预约列表
     * @param expertName 专家用户名（模糊匹配）
     * @param pageNum 页码
     * @return 分页预约列表
     */
    PageInfo<Reserve> findPageReservationByExpertName(String expertName, Integer pageNum);

    /**
     * 管理员分页查询所有专家的预约列表
     * @param pageNum 页码
     * @return 分页预约列表
     */
    PageInfo<Reserve> findPageAllReservations(Integer pageNum);

    /**
     * 管理员根据预约id删除预约
     * @param id 预约id
     * @return 删除结果
     */
    Result deleteReservationById(Integer id);

    //预约管理
    //获取专家预约列表
    //分页查询专家预约列表
    PageInfo<Reserve> findPageExpertReservation(Integer pageNum);

    //根据关键词查询专家的预约列表
    //分页模糊查询专家预约列表
    PageInfo<Reserve> searchPageReservationList(String keyword, Integer pageNum);

    //回复预约
    Result replyReservation(Integer id, String answer);




    //知识管理
    //展示当前登录专家的知识列表
    //分页查询专家知识列表
    PageInfo<Knowledge> findPageExpertKnowledge(Integer pageNum);

    //根据关键词查询专家的知识列表
    //分页模糊查询专家知识列表
    PageInfo<Knowledge> searchPageKnowledgeList(String keyword, Integer pageNum);

    //发布知识
    Result publishKnowledge(String title, String content, String picpath);

    //修改知识
    Result updateKnowledge(Integer id, String title, String content, String picpath);

    //删除知识
    Result deleteKnowledge(Integer id);



}
