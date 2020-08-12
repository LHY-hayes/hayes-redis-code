package com.hayes.bash.hayesredis.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisService {

    /** 清理操作 **/
    public void flushAll();

    public void flushByKeys(String key);

    public void flushByKeys(List<String> key);

    /** hash 操作 **/
    public void hashPutAll(JSONObject jsonObject);

    public void hashPutAll(String key , Map<String, Object> phoneNumberInfo);

    public void hashPut(String key, String hkey, Object value);

    public Map<String, Object> hashGetAll(String key);

    public Object hashGet(String key, String hkey);

    /** set 操作 **/
    public void setPut(JSONObject jsonObject);

    public Set<String> setGet(JSONObject jsonObject);

    public void setRemove(String key , JSONObject jsonObject);

    public Set<String> setIntersect(List<String> ruleSets);

    public Set<String> setIntersect(String segment , List<String> ruleSets);






}
