package org.example.pojo.dto;

import lombok.Data;

@Data
public class ReserveQueryDTO {
    private Integer id;
    private String expertName;
    private String questioner;
    private String area;
    private String address;
    private String plantName;
    private String soilCondition;
    private String plantCondition;
    private String plantDetail;
    private String phone;
    private String message;
    private String answer;
    private Integer status;

    // 分页参数
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}