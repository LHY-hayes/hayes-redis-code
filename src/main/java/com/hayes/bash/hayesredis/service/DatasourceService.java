package com.hayes.bash.hayesredis.service;

import com.alibaba.fastjson.JSONObject;
import com.hayes.bash.hayesredis.entity.PhoneNumberInfo;
import com.hayes.bash.hayesredis.exceptions.WrongException;

import java.util.List;

public interface DatasourceService {

    public List<PhoneNumberInfo> getyytNumberForOrder(JSONObject parm) throws WrongException ,Exception;



}
