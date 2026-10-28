package org.example.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Reserve {
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
}
