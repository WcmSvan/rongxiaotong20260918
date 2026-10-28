package org.example.service;

import com.github.pagehelper.PageInfo;
import org.example.common.Result;
import org.example.pojo.dto.KnowledgeDTO;
import org.example.pojo.entity.Knowledge;

//农业知识模块 业务接口
public interface KnowledgeService {
    /**
     * 分页获取全部农业知识列表
     * @param pageNum
     * @return
     */
    PageInfo<Knowledge> findPageKnowledge(Integer pageNum);


    /**
     * 分页搜索知识
     */
    PageInfo<Knowledge> searchPageKnowledge(String keyword, Integer pageNum);


    /**
     * 根据id查询知识
     * @param id
     * @return
     */
    Result getKnowledgeById(Integer id);


}
