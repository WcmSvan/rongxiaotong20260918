package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.entity.Purchase;
import org.example.pojo.entity.PurchaseDetail;
import org.example.pojo.vo.PurchaseVO;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface PurchaseDetailMapper {
    void insertSelective(PurchaseDetail purchaseDetail) ;

    List<PurchaseVO> selectOrderDetail(@Param("ownName") String ownName);

    void insertPurchaseDetail(PurchaseDetail detail);
}
