package com.hayes.bash.hayesredis.ctrl;

import com.alibaba.fastjson.JSONObject;
import com.hayes.bash.hayesredis.result.JsonResult;
import com.hayes.bash.hayesredis.result.ResultCode;
import com.hayes.bash.hayesredis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("redis")
public class RedisController extends RedisBashController{


    @Autowired
    private RedisService redisService ;

    @PostMapping("/flushAll")
    public JsonResult flushAll() {

        redisService.flushAll();

        return JsonResult.success();

    }

    /**
     * POST http://localhost:8080/redis/hash/get
     * Content-Type: application/json
     *
     * {
     *   "key": "17000000734",
     *   "hkey": "ruleSets"
     *
     * }
     * @param jsonObject
     * @return
     */
    @PostMapping("/hash/get")
    public JsonResult hget(@RequestBody JSONObject jsonObject) {

        if (! jsonObject.containsKey("key")) return JsonResult.failure(ResultCode.JSON_NO_PARAM_KEY);

        if (!jsonObject.containsKey("hkey")) {
            final Map<String, Object> entries = redisService.hashGetAll(jsonObject.getString("key"));
            return JsonResult.success(entries);
        }else {
            final Object value = redisService.hashGet(jsonObject.getString("key"),jsonObject.getString("hkey"));
            return JsonResult.success(value);

        }

    }

    /**
     *  POST http://localhost:8080/redis/hash/put
     * Content-Type: application/json
     *
     * {
     *   "result": {
     *     "phoneNumberList":[]
     *  },
     *   "resultCode": null,
     *   "resultMsg": null
     * }
     *
     * @param jsonObject
     * @return
     */

    @PostMapping("/hash/put")
    public JsonResult hput(@RequestBody JSONObject jsonObject){

        if (!jsonObject.containsKey("result")) return JsonResult.failure(ResultCode.JSON_NO_PARAM_RESULT);

        redisService.hashPutAll(jsonObject);
        return JsonResult.success();

    }

    /**
     *
     * ###
     * POST http://localhost:8080/redis/set/put
     * Content-Type: application/json
     *
     * {
     *   "key": "AAAA",
     *   "phoneNumbers": ["17000811111","17000813333","17000812222"]
     * }
     *
     * @param jsonObject
     * @return
     */
    @PostMapping("/set/put")
    public JsonResult sput(@RequestBody JSONObject jsonObject){

        if (!jsonObject.containsKey("key")) return JsonResult.failure(ResultCode.JSON_NO_PARAM_KEY);
        if (!jsonObject.containsKey("phoneNumber")) return JsonResult.failure(ResultCode.JSON_NO_PARAM_PHONENUMBERS);

        redisService.setPut(jsonObject);

        return JsonResult.success();

    }

    /**
     *
     *### get数据
     * POST http://localhost:8080/redis/set/get
     * Content-Type: application/json
     *
     * {
     *   "key": "AAAA"
     *
     * }
     *
     *
     * @param jsonObject
     * @return
     */

    @PostMapping("/set/get")
    public JsonResult sget(@RequestBody JSONObject jsonObject){

        if (!jsonObject.containsKey("key")) return JsonResult.failure(ResultCode.JSON_NO_PARAM_KEY);

        Set<String> phoneNumbers = redisService.setGet(jsonObject);

        return JsonResult.success(phoneNumbers);


    }


    @PostMapping("/set/setIntersect")
    public JsonResult setIntersect(@RequestBody JSONObject jsonObject){

        List<String> rules = jsonObject.getJSONArray("rules").toJavaList(String.class);

        Set<String> phoneNumbers = redisService.setIntersect(rules);

        return JsonResult.success(phoneNumbers);


    }








}
