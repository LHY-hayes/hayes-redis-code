package com.hayes.bash.hayesredis.result;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class JsonResult<T> implements Serializable {

    /**
     * 是否响应成功
     */
    private Boolean success;
    /**
     * 响应状态码
     */
    private Integer resultCode;
    /**
     * 相应信息
     */
    private String resultMsg;
    /**
     * 时间
     */
    private long timestamp;
    /**
     * 响应数据
     */
    private T result;

    // 构造器开始

    /**
     * 无参构造器(构造器私有，外部不可以直接创建)
     */
    private JsonResult() {
        this.success = true;
        this.resultCode = 200;
        this.resultMsg = "success";
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 有参构造器
     *
     * @param obj
     */
    private JsonResult(T obj) {
        this.success = true;
        this.resultCode = 200;
        this.resultMsg = "success";
        this.timestamp = System.currentTimeMillis();
        this.result = obj;
    }

    /**
     * 有参构造器
     *
     * @param resultCode
     */
    private JsonResult(ResultCode resultCode) {
        this.success = true;
        this.resultCode = resultCode.getCode();
        this.resultMsg = resultCode.getMessage();
        this.timestamp = resultCode.getTimestamp();
    }
    // 构造器结束

    /**
     * 通用返回成功（没有返回结果）
     *
     * @param <T>
     * @return
     */
    public static <T> JsonResult<T> success() {
        return new JsonResult(ResultCode.SUCCESS);
    }

    /**
     * 返回成功（有返回结果）
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> JsonResult<T> success(T data) {
        JsonResult jsonResult = new JsonResult(ResultCode.SUCCESS);
        jsonResult.result = data;
        return jsonResult;

    }

    /**
     * 通用返回失败
     *
     * @param resultCode
     * @param <T>
     * @return
     */
    public static <T> JsonResult<T> failure(ResultCode resultCode) {
        return new JsonResult<T>(resultCode);
    }

    /**
     * 返回失败（带错误信息）
     *
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> JsonResult<T> failure(String msg) {
        JsonResult jsonResult = new JsonResult(ResultCode.SERVER_ERROR);
        jsonResult.resultMsg = msg;
        return jsonResult;

    }


}

