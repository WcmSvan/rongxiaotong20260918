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
public class Question {
    private Integer id;
    private String expertName;
    private String questioner;
    private String phone;
    private String plantName;
    private String title;
    private String question;
    private String answer;
    private Integer status;

}
