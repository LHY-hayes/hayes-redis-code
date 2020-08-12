package com.hayes.bash.hayesredis.service;

import com.alibaba.fastjson.JSONObject;
import com.hayes.bash.hayesredis.result.JsonResult;

public interface BusinessService {

    public JSONObject getNumberForOrderList(JSONObject param) ;

    public JsonResult openingCallBack(JSONObject param) ;

    public JsonResult refreshAllData() ;

}
