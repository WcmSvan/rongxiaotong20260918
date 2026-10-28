package org.example.pojo.entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)

public class Order {


    private Integer orderId;

    @NotBlank(message = "标题不能为空")
    @Length(min = 2, max = 8, message = "标题长度在2-8之间")
    private String title;

    @NotBlank(message = "价格不能为空")
    @Pattern(regexp = "^(([1-9]{1}\\d*)|(0{1}))(\\.\\d{1,2})?$", message = "请输入合理的价格，由数字组成，最多两位小数")
    private String price;

    @NotBlank(message = "内容不能为空")
    @Length(min = 10, max = 150, message = "内容长度在10-150之间")
    private String content;

    private Integer orderStatus;
    private String type;
    private String picture;
    private String ownName;
    private String cooperationName;
    private Date createTime;
    private Date updateTime;

    @Length(min = 10, max = 30)
    private String address;


}
