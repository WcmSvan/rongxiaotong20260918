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
public class Discuss {
    private int discussId;
    private int knowledgeId;
    private String ownName;
    private String content;
    private LocalDateTime createTime;
}
