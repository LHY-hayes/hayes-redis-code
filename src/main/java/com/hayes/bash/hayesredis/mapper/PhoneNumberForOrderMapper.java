package com.hayes.bash.hayesredis.mapper;


import com.alibaba.fastjson.JSONObject;
import com.hayes.bash.hayesredis.entity.PhoneNumberInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PhoneNumberForOrderMapper {


    List<PhoneNumberInfo> getyytPhoneNumberInfoList(JSONObject map);

}
