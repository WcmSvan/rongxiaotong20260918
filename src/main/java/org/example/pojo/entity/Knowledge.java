package org.example.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Knowledge {
    private Integer knowledgeId;
    private String title;
    private String content;
    private String picPath;
    private String ownName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
