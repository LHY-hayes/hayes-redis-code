package com.hayes.bash.hayesredis.ctrl;

import com.alibaba.fastjson.JSONObject;
import com.hayes.bash.hayesredis.result.JsonResult;
import com.hayes.bash.hayesredis.result.ResultCode;
import com.hayes.bash.hayesredis.service.BusinessService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/acceptBusiness")
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    @PostMapping("/getNumberForOrderList")
    public JSONObject getNumberForOrderList(@RequestBody JSONObject param) {
        JSONObject result = new JSONObject();
        try {

            JSONObject numberForOrderList = businessService.getNumberForOrderList(param);
            result.put("resultCode", "200");
            result.put("resultMsg", "success");
            result.put("phoneNumberInfo", numberForOrderList);
            return result;

        } catch (Exception e) {

            result.put("resultCode", "500");
            result.put("resultMsg", e.toString());
            return result;

        }

    }

    @PostMapping("/openingCallBack")
    public JsonResult openingCallBack(@RequestBody JSONObject param) {
        try {

            if (!param.containsKey("phoneNumber") || StringUtils.isEmpty(param.getString("phoneNumber"))) {
                return JsonResult.failure(ResultCode.JSON_NO_PARAM_PHONENUMBERS);
            }

            return businessService.openingCallBack(param);

        } catch (Exception e) {

            return JsonResult.failure(String.valueOf(e.toString()));

        }
    }


    @PostMapping("/refreshAllData")
    public JsonResult getNumbers() {
        try {

            return businessService.refreshAllData();

        } catch (Exception e) {

            return JsonResult.failure(String.valueOf(e.toString()));

        }
    }


}
