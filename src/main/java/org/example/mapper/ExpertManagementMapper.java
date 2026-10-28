package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.pojo.dto.KnowledgeDTO;
import org.example.pojo.entity.Knowledge;
import org.example.pojo.entity.Question;
import org.example.pojo.entity.Reserve;

import java.util.List;

@Mapper
public interface ExpertManagementMapper {
/*
    //问题管理
 */
    //获取专家问题列表
    List<Question> getExpertQuestionList(String userName);

    //根据 问题标题或者问题内容模糊查询问题列表
    List<Question> searchQuestionList(String keyword, String userName);

    //根据id获取问题详情
    Question getQuestionById(Integer id);

    //根据id回答问题
    int answerQuestionById(@Param("id") Integer id, @Param("answer")String answer);

    //根据id删除问题
    int deleteQuestionById(Integer id);

/*
   预约管理
 */
    //获取专家预约列表
    List<Reserve> getExpertReservationList(String userName);

    //管理员查询所有专家的预约列表
    List<Reserve> getAllReservations();

    //管理员根据专家用户名模糊查询预约列表
    List<Reserve> getReservationListByExpertNameLike(String expertName);

    //根据关键词查询专家的预约列表
    List<Reserve> searchReservationList(String keyword, String userName);

    //根据id获取预约详情
    Reserve getReservationById(Integer id);

    //根据id回复预约
    int replyReservationById(Integer id, String answer);

    //管理员根据预约id删除预约
    int deleteReservationById(Integer id);

/*
    知识管理
 */
    //展示当前登录专家的知识列表
     List<Knowledge> getExpertKnowledgeList(String userName) ;

     //根据关键词查询专家的知识列表
    List<Knowledge> searchKnowledgeList(String keyword, String userName);

    //根据id获取知识详情
    int insertKnowledge(KnowledgeDTO knowledge);

    //根据id更新知识
    int updateKnowledgeById(@Param("id") Integer id, @Param("title") String title, @Param("content") String content, @Param("picpath") String picpath);

    //根据id删除知识
    int deleteKnowledgeById(Integer id);
}
