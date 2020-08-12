package com.hayes.bash.hayesredis.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hayes.bash.hayesredis.entity.PhoneNumberInfo;
import com.hayes.bash.hayesredis.exceptions.WrongException;
import com.hayes.bash.hayesredis.mapper.PhoneNumberForOrderMapper;
import com.hayes.bash.hayesredis.service.DatasourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
@Slf4j
public class DatasourceServiceImpl implements DatasourceService {

    @Autowired
    private PhoneNumberForOrderMapper phoneNumberForOrderMapper ;

    @Override
    public List<PhoneNumberInfo> getyytNumberForOrder(JSONObject parm) throws WrongException,Exception {
        List<PhoneNumberInfo> phoneNumberList = null;
        try{
            log.info("getyytNumberForOrder.parm="+parm.toJSONString());
            JSONObject map = new JSONObject();
            if(parm.containsKey( "employeeId")){
                map.put("employeeId",parm.getString("employeeId"));
            }else{
                throw new WrongException("操作员工号不能为空");
            }
            if(parm.containsKey( "sourceSite")){
                //map.put("sourceSite",parm.getString("sourceSite"));
            }else{
                throw new WrongException("请求平台编码不能为空");
            }
            if(parm.containsKey( "phoneNumber")){
                map.put("phoneNumber",parm.getString("phoneNumber"));
            }
            if(parm.containsKey( "ctPage")){
                map.put("ctPage",parm.getString("ctPage"));
            }else{
                map.put("ctPage","1");
            }
            if(parm.containsKey( "pageSize")){
                map.put("pageSize",parm.getString("pageSize"));
            }else{
                map.put("pageSize","10");
            }
            if(parm.containsKey( "provinceNumber")){
                map.put("provinceNumber",parm.getString("provinceNumber"));
            }
            if(parm.containsKey( "cityNumber")){
                map.put("cityNumber",parm.getString("cityNumber"));
            }
            if(parm.containsKey( "segment")){
                map.put("segment",parm.getString("segment"));
            }
            if(parm.containsKey( "baseType")){
                map.put("baseType",parm.getString("baseType"));
            }
            if(parm.containsKey( "generationType")){
                map.put("generationType",parm.getString("generationType"));
            }
            if(parm.containsKey( "gradeId")){
                map.put("gradeId",parm.getString("gradeId"));
            }
            if(parm.containsKey( "ruleType")){
                //AAA AAAA 1314 520 no4 tail
                map.put("ruleType",parm.getString("ruleType"));
                if(parm.containsKey( "ruleGetType")) {
                    // like 和 year 走这个
                    map.put("ruleGetType", parm.getString("ruleGetType"));
                }
            }
            if(parm.containsKey( "priceStart")){
                map.put("priceStart",new BigDecimal(parm.getString("priceStart")).multiply(new BigDecimal("100")));
            }
            if(parm.containsKey( "priceEnd")){
                map.put("priceEnd",new BigDecimal(parm.getString("priceEnd")).multiply(new BigDecimal("100")));
            }
            map.put("state","A");
            phoneNumberList = phoneNumberForOrderMapper.getyytPhoneNumberInfoList(map);

        }catch(WrongException e){
            log.error("getyytNumberForOrder.bizerr>>",e);
            throw new WrongException(e.toString());
        }catch(Exception e){
            log.error("getyytNumberForOrder.err>>",e);
            throw new Exception(e.toString());
        }

        return phoneNumberList;
    }
}
