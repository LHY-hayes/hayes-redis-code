package com.hayes.bash.hayesredis.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hayes.bash.hayesredis.service.RedisService;
import com.hayes.bash.hayesredis.utils.JsonUtils;
import com.hayes.bash.hayesredis.utils.PhoneNumberUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired(required = false)
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        this.redisTemplate = redisTemplate;
    }

    /**
     * 清空redis所有数据
     */
    @Override
    public void flushAll() {
        log.info(">>>RedisServiceImpl--request --> flushAll -->  ");

        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                redisConnection.flushDb();
                return null;
            }
        });

    }

    @Override
    public void flushByKeys(String key) {
        if (StringUtils.isEmpty(key)) return;
        log.info(">>>RedisServiceImpl--request --> flushByKeys1 --> {} ", key);
        redisTemplate.delete(key);

    }

    @Override
    public void flushByKeys(List<String> keys) {

        if (keys == null || keys.size() == 0) return;
        log.info(">>>RedisServiceImpl--request --> flushByKeys2 --> {} ", keys);
        redisTemplate.delete(keys);

    }


    /***** hash 操作  *****/

    @Override
    public void hashPutAll(JSONObject jsonObject) {
        /**
         * 1. 将数据转为List<Map<String,Object>> 类型
         * 2. 迭代list， 取phoneNumber当作key，map当作value
         *
         * 3. 增加一个字段： 判断该号码符合哪些规则
         */

        JSONArray phoneNumberList = jsonObject.getJSONObject("result").getJSONArray("phoneNumberList");

        List<Map<String, Object>> mapLists = JsonUtils.JSON2List(phoneNumberList.toJSONString());
        Iterator<Map<String, Object>> iterator = mapLists.iterator();

        while (iterator.hasNext()) {

            Map<String, Object> map = iterator.next();

            if (StringUtils.isEmpty(map.get("phoneNumber"))) continue;

            String ruleSetsByPhoneNumber = PhoneNumberUtils.getRuleSetsByPhoneNumber(map);
            map.put("ruleSets", ruleSetsByPhoneNumber);

            String key = map.get("phoneNumber").toString();

            log.info(">>>RedisServiceImpl--request --> hashPutAll --> key :  {} , map :{}  ", key, map);
            redisTemplate.opsForHash().putAll(key, map);
            redisTemplate.expire(key, 26L, TimeUnit.HOURS);

            // 每个号码保存到对应的set中

            String[] rules = ruleSetsByPhoneNumber.split("#");
            JSONObject setMap = new JSONObject();
            String phoneNumber = key;

            for (String rule : rules) {
                setMap.put("key", rule);
                setMap.put("phoneNumber", phoneNumber);
                setPut(setMap);
            }
        }

    }


    @Override
    public void hashPutAll(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            redisTemplate.expire(key, 26, TimeUnit.HOURS);
            String ruleSetsByPhoneNumber = PhoneNumberUtils.getRuleSetsByPhoneNumber(map);
            String[] rules = ruleSetsByPhoneNumber.split("#");
            JSONObject setMap = new JSONObject();
            String phoneNumber = key;

            for (String rule : rules) {
                setMap.put("key", rule);
                setMap.put("phoneNumber", phoneNumber);
                setPut(setMap);
            }

        } catch (Exception e) {
            log.error("hashPutAll(String key, Map<String, Object> map) .err>>", e);
        }

    }

    @Override
    public void hashPut(String key, String hkey, Object value) {
        log.info(">>>RedisServiceImpl--request --> hashPut --> key :{} , hkey:{} , value: {} ", key, hkey, value);
        redisTemplate.opsForHash().put(key, hkey, value);
        redisTemplate.expire(key, 26L, TimeUnit.HOURS);

    }

    @Override
    public Map<String, Object> hashGetAll(String key) {
        log.info(">>>RedisServiceImpl--request --> hashGetAll --> key :{}  ", key);
        return redisTemplate.opsForHash().entries(key);

    }

    @Override
    public Object hashGet(String key, String hkey) {
        log.info(">>>RedisServiceImpl--request --> hashGet --> key : {}, hkey: {}   ", key, hkey);
        return redisTemplate.opsForHash().get(key, hkey);

    }

    /***** set 操作  *****/

    @Override
    public void setPut(JSONObject jsonObject) {

        String key = jsonObject.getString("key");
        String phoneNumbers = jsonObject.getString("phoneNumber");
        redisTemplate.opsForSet().add(key, phoneNumbers);
        redisTemplate.expire(key, 26L, TimeUnit.HOURS);
    }

    @Override
    public Set<String> setGet(JSONObject jsonObject) {
        String key = jsonObject.getString("key");
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public void setRemove(String key, JSONObject jsonObject) {
        log.info(">>>RedisServiceImpl--request --> setRemove --> key :{} , value: {}", key, jsonObject.getString("phoneNumber"));
        redisTemplate.opsForSet().remove(key, jsonObject.getString("phoneNumber"));
    }


    @Override
    public Set<String> setIntersect(String segment, List<String> ruleSets) {
        long s = System.currentTimeMillis();
        Set<String> setstr;
        if (ruleSets != null && ruleSets.size() > 0) {
            setstr = redisTemplate.opsForSet().intersect(segment, ruleSets);
        } else {
            setstr = redisTemplate.opsForSet().members(segment);
        }

        log.error("查询Set交集耗时： " + (System.currentTimeMillis() - s) + " ms");
        return setstr;
    }

    @Override
    public Set<String> setIntersect(List<String> ruleSets) {
        long s = System.currentTimeMillis();

        if (ruleSets != null && ruleSets.size() > 0) {

            if (ruleSets.size() > 1) {
                Object firstRule = "";
                List<Object> list = new ArrayList<>();
                for (int i = 0; i < ruleSets.size(); i++) {

                    if (i > 0) {
                        list.add(ruleSets.get(i));
                    } else {
                        firstRule = ruleSets.get(i);
                    }
                }
                log.error("查询Set交集耗时： " + (System.currentTimeMillis() - s) + " ms");
                return redisTemplate.opsForSet().intersect(firstRule, list);
            } else {
                log.error("查询Set交集耗时： " + (System.currentTimeMillis() - s) + " ms");
                return redisTemplate.opsForSet().members(ruleSets.get(0));
            }
        }

        return null;

    }

}
