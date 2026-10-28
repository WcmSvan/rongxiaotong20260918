package org.example.pojo.dto;

import lombok.Data;

@Data
public class DiscussDTO {
    private Integer discussId;
    private Integer knowledgeId;
    private String ownName;
    private String content;
    private String createTime;
}
