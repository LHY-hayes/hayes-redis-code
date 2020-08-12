package com.hayes.bash.hayesredis.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

public class JsonUtils {

    /**
     * 对象转json
     *
     * @return
     */

    public static String Object2JSON(Object o) {

        if (StringUtils.isEmpty(o)) return null;

        return JSON.toJSONString(o);

    }

    /**
     * @param jsonObject
     * @return
     */
    public static List<Map<String, Object>> JSON2List(String jsonObject) {

        if (StringUtils.isEmpty(jsonObject)) return null;

        List<Map<String, Object>> list = JSON.parseObject(jsonObject,
                new TypeReference<List<Map<String, Object>>>() {
                });


        return list;

    }

    /**
     * @param jsonObject
     * @return
     */
    public static List<String> JSON2List2(String jsonObject) {

        if (StringUtils.isEmpty(jsonObject)) return null;

        List<String> list = JSON.parseObject(jsonObject,
                new TypeReference<List<String>>() {
                });


        return list;

    }


}
