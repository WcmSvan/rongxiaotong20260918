package org.example.pojo.dto;

import lombok.Data;
@Data
public class QuestionDTO {
        private Integer id;
        private String title;
        private String question;
        private String plantName;
        private String phone;
        private String answer;
        private Integer status;

        // 来自 tb_question 本身的
        private String questioner;
        private String expertUserName;

        // 来自 tb_expert 联查的
        private String expertRealName;
        private String expertProfession;
        private String expertPosition;

        // 时间格式化后的字符串（前端直接展示）
        private String createTimeStr;
}
