package org.example.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

public class Result{
    private Integer code;//状态码

    private String message;//返回给前端的提示信息

    private Object data;//返回给前端的数据

    private Boolean flag;//业务是否成功（与前端约定的标志位）

    //封装成功时调用的方法
    public static Result success(Object data){
        Result r = new Result();
        r.setCode(200);
        r.setMessage("操作成功");
        r.setData(data);
        r.setFlag(true);
        return r;
    }
    //重载
    public static Result success(Object data,String message){
        Result r = new Result();
        r.setCode(200);
        r.setMessage(message);
        r.setData(data);
        r.setFlag(true);
        return r;
    }

    public static Result success(String message){
        Result r = new Result();
        r.setCode(200);
        r.setMessage(message);
        r.setFlag(true);
        return r;
    }

    //无参成功
    public static Result success(){
        Result r = new Result();
        r.setCode(200);
        r.setMessage("操作成功");
        r.setFlag(true);
        return r;
    }

    //封装失败时调用的方法
    public static Result error(String message){
        Result r = new Result();
        r.setCode(201);
        r.setMessage(message);
        r.setFlag(false);
        return r;
    }

    //无参失败
    public static Result error(){
        Result r = new Result();
        r.setCode(201);
        r.setMessage("操作失败");
        r.setFlag(false);
        return r;
    }

}
