package com.hayes.bash.hayesredis.result;

public enum ResultCode {


    /*** 通用部分 100 - 599***/
    // 不包含result字段
    JSON_NO_PARAM_RESULT(101, "json not found result param"),
    // 不包含key字段
    JSON_NO_PARAM_KEY(102, "json not found key param"),
    //
    JSON_NO_PARAM_PHONENUMBERS(103, "json not found phoneNumbers param"),
    // 成功请求
    SUCCESS(200, "success"),
    // 重定向
    REDIRECT(301, "redirect"),
    // 资源未找到
    NOT_FOUND(404, "page not found"),
    // 服务器错误
    SERVER_ERROR(500, "server error"),
    // 认证失败
    Auth_ERROR(401, "Authentication failed"),


    /*** 这里可以根据不同模块用不同的区级分开错误码，例如:  ***/

    /**
     *
     | 分类 | 区间    | 分类描述                            |
     |-----|---------|-----------------------------------|
     | 1** | 100-199 | 信息错误;服务器收到请求,需请求者继续操作 |
     | 2** | 200-299 | 成功;接受成功并处理成功          |
     | 3** | 300-399 | 重定向;需要进一步操作已完成请求      |
     | 4** | 400-499 | 客户端错误;请求语法错误或无法完成请求   |
     | 5** | 500-599 | 服务器错误;服务器在处理过程中发生错误   |
     */

    ;


    private Integer code;
    private String message;
    private long timestamp;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
