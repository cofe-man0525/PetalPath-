package com.explore.xianhuaback.Result;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;    // 业务状态码
    private String msg;      // 提示信息
    private T data;          // 数据

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);        // 业务成功码
        result.setMsg("成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);        // 业务失败码
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }
}