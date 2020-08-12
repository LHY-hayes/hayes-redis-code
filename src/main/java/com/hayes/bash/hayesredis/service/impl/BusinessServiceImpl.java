package com.hayes.bash.hayesredis.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hayes.bash.hayesredis.entity.PhoneNumberInfo;
import com.hayes.bash.hayesredis.exceptions.WrongException;
import com.hayes.bash.hayesredis.result.JsonResult;
import com.hayes.bash.hayesredis.service.BusinessService;
import com.hayes.bash.hayesredis.service.DatasourceService;
import com.hayes.bash.hayesredis.service.RedisService;
import com.hayes.bash.hayesredis.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class BusinessServiceImpl implements BusinessService {

    @Autowired
    private RedisService redisService;
    @Autowired
    private DatasourceService datasourceService;

    @Override
    public JSONObject getNumberForOrderList(JSONObject param) {

        /**
         * 1。将查询条件拆分成不同的规则
         *    号段取并集，其他规则取交集
         * 2。setIntersect
         * 3. hashGetAll
         */

        String[] segments = null;
        List<String> rules = new ArrayList<>();
        if (param.containsKey("segment") && StringUtils.isNotEmpty(param.getString("segment"))) {
            segments = param.getString("segment").split(",");
        }
        if (param.containsKey("ruleType") && StringUtils.isNotEmpty(param.getString("ruleType"))) {
            String ruleType = param.getString("ruleType");
            if (param.getString("ruleType").indexOf("LIKE") > -1) {
                ruleType = "%" + param.getString("ruleType").split("-")[1];
            }
            if (param.getString("ruleType").indexOf("YEAR") > -1) {
                ruleType = "%" + param.getString("ruleType").split("-")[1] + "_";
            }
            rules.add(ruleType);
        }

        if (param.containsKey("cityNumber") && StringUtils.isNotEmpty(param.getString("cityNumber"))) {
            rules.add(param.getString("cityNumber"));
        }

        /** 100以下，100-500 ， 500-1000， 1000-5000，5000-10000，1000以上 **/
        /** price_A，price_B ， price_C， price_D，price_E，price_F **/
        String priceRule = "";
        if (param.containsKey("priceStart") && StringUtils.isNotEmpty(param.getString("priceStart")))
            priceRule += param.getString("priceStart");
        priceRule += "-";
        if (param.containsKey("priceEnd") && StringUtils.isNotEmpty(param.getString("priceEnd")))
            priceRule += param.getString("priceEnd");
        switch (priceRule) {
            case "-100":
                rules.add("price_A");
                break;
            case "100-500":
                rules.add("price_B");
                break;
            case "500-1000":
                rules.add("price_C");
                break;
            case "1000-5000":
                rules.add("price_D");
                break;
            case "5000-10000":
                rules.add("price_E");
                break;
            case "10000-":
                rules.add("price_F");
                break;
        }

        JSONObject phoneNumberInfo = new JSONObject();

        Set<Object> phoneNumberInfos = new HashSet<>();

        if (param.containsKey("phoneNumber") && StringUtils.isNotEmpty(param.getString("phoneNumber"))) {
            phoneNumberInfos.add(param.getString("phoneNumber"));
        } else {

            if (segments != null && segments.length > 0) {

                for (String segment : segments) {
                    phoneNumberInfos.addAll(redisService.setIntersect(segment, rules));
                }
            } else {
                phoneNumberInfos.addAll(redisService.setIntersect(rules));
            }
        }


        int ctPage = Integer.parseInt(param.getString("ctPage"));
        int pageSize = Integer.parseInt(param.getString("pageSize"));

        phoneNumberInfo.put("currentPage", ctPage);
        phoneNumberInfo.put("recordTotal", phoneNumberInfos.size());

        int totalpage = phoneNumberInfos.size() / pageSize;

        if (phoneNumberInfos.size() % pageSize > 0) totalpage += 1;

        phoneNumberInfo.put("totalPage", totalpage);

        List<String> phoneNumberInfoList = CommonUtils.subSet(phoneNumberInfos, (ctPage - 1) * pageSize, ctPage * pageSize);// 含头不含尾

        phoneNumberInfo.put("totalRow", phoneNumberInfoList.size());
        long s = System.currentTimeMillis();
        List<Map<String, Object>> array = new ArrayList<>();
        for (String numberInfo : phoneNumberInfoList) {

            Map<String, Object> numberInfoMap = redisService.hashGetAll(numberInfo);
            array.add(numberInfoMap);

        }
        phoneNumberInfo.put("phoneNumberList", array);
        log.error("查询Hash结果集耗时： " + (System.currentTimeMillis() - s) + " ms");

        return phoneNumberInfo;

    }

    @Override
    public JsonResult refreshAllData() {

        try {
            JSONObject param = new JSONObject();
            param.put("employeeId", "100");
            param.put("sourceSite", "YYT");
            //清redis元数据
            log.info("开始清理redis元数据。。。。。。。");
            redisService.flushAll();
            log.info("清理redis完成end");
            log.info("查询到phoneNumber数据中。。。。。");
            //查datasource元数据
            long s = System.currentTimeMillis();
            List<PhoneNumberInfo> phoneNumberInfos = datasourceService.getyytNumberForOrder(param);
            log.info("查询到phoneNumber共计：{} 条 。 耗时 :{} ms  ", phoneNumberInfos.size(), (System.currentTimeMillis() - s));
            log.info("执行插入redis中, 请等待。。。。。 ");
            int n = 0;
            s = System.currentTimeMillis();
            for (PhoneNumberInfo phoneNumberInfo : phoneNumberInfos) {
                //放入redis
                redisService.hashPutAll(phoneNumberInfo.getPhoneNumber(), CommonUtils.object2Map(phoneNumberInfo));
                n++;
                if (n % 100 == 0) {
                    log.info("已成功插入 {} 条 ,剩余 {} 条， 耗时 {} ms ", n, phoneNumberInfos.size() - n, System.currentTimeMillis() - s);
                    s = System.currentTimeMillis();
                }
            }
            log.info("执行redis插入完毕 ");

        } catch (WrongException e) {
            log.error("refreshAllData.bizerr>>", e);
            // sendMail()
        } catch (Exception e) {
            log.error("refreshAllData.err>>", e);
            // sendMail()
        }
        return JsonResult.success();
    }

    @Override
    public JsonResult openingCallBack(JSONObject param) {

        try {

            Map<String, Object> phoneNumberInfo = redisService.hashGetAll(param.getString("phoneNumber"));

            String[] ruleSets = phoneNumberInfo.get("ruleSets").toString().split("#");
            for (String ruleSet : ruleSets) {
                redisService.setRemove(ruleSet, param);
            }

            redisService.flushByKeys(phoneNumberInfo.get("phoneNumber").toString());
            return JsonResult.success();

        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.failure(e.toString());

        }
    }
}
