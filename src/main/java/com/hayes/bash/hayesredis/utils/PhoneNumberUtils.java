package com.hayes.bash.hayesredis.utils;

import java.util.Map;

public class PhoneNumberUtils {

    public static String getRuleSetsByPhoneNumber(Map<String, Object> map) {

        StringBuilder ruleSets = new StringBuilder();
        /**
         * 列举所有规则, 一一进行校验
         */

        String phoneNumber = map.get("phoneNumber").toString();
        /** 1. 现存规则 **/
        String rule = map.get("rule").toString();
        // 1） -- AAA,AAAA,ABAB,BCD,520,1314,4,AAA6,AAAB,ABBA,no4    只保存这些规则
        // String[] ph_ruls = "AAA,AAAA,ABAB,BCD,520,1314,4,AAA6,AAAB,ABBA,no4".split(",");
        // Set<String> ph_ruls_sets =  new HashSet<String>(Arrays.asList(ph_ruls));
        // if (ph_ruls_sets.contains(rule)) ruleSets.append(rule+"#") ;
        // 2）-- 保存rule配置的规则
        ruleSets.append(rule + "#");


        /** 2. 自定义规则 **/
        // -- %521,%920,%921,%1212,%9421,%0006,%1023,%1117,%7337,%8128
        if (endLike(phoneNumber, "%521")) ruleSets.append("%521#");
        if (endLike(phoneNumber, "%920")) ruleSets.append("%920#");
        if (endLike(phoneNumber, "%921")) ruleSets.append("%921#");
        if (endLike(phoneNumber, "%1212")) ruleSets.append("%1212#");
        if (endLike(phoneNumber, "%9421")) ruleSets.append("%9421#");
        if (endLike(phoneNumber, "%0006")) ruleSets.append("%0006#");
        if (endLike(phoneNumber, "%1023")) ruleSets.append("%1023#");
        if (endLike(phoneNumber, "%1117")) ruleSets.append("%1117#");
        if (endLike(phoneNumber, "%7337")) ruleSets.append("%7337#");
        if (endLike(phoneNumber, "%8128")) ruleSets.append("%8128#");

        /** 3. 年代规则 **/
        //-- %195_ ,%196_ ,%197_,%198_,%199_,%200_,%201_
        if (endLike(phoneNumber, "%195_")) ruleSets.append("%195_#");
        if (endLike(phoneNumber, "%196_")) ruleSets.append("%196_#");
        if (endLike(phoneNumber, "%197_")) ruleSets.append("%197_#");
        if (endLike(phoneNumber, "%198_")) ruleSets.append("%198_#");
        if (endLike(phoneNumber, "%199_")) ruleSets.append("%199_#");
        if (endLike(phoneNumber, "%200_")) ruleSets.append("%200_#");
        if (endLike(phoneNumber, "%201_")) ruleSets.append("%201_#");


        /** 4. 城市列表 **/
        String cityNumber = map.get("cityNumber").toString();
        ruleSets.append(cityNumber + "#");

        /** 5. 手机尾号 和 号段 无需配置 --- **/
        ruleSets.append(phoneNumber.substring(0, 7) + "#");
        ruleSets.append(phoneNumber.substring(7) + "#");


        /** 6. 号码价格区间   - **/
        /** 100以下，100-500 ， 500-1000， 1000-5000，5000-10000，1000以上 **/
        /** price_A，price_B ， price_C， price_D，price_E，price_F **/

        int price = Integer.parseInt(map.get("price").toString());
        if (price < 100) ruleSets.append("price_A#");
        if (price >= 100 && price < 500) ruleSets.append("price_B#");
        if (price >= 500 && price < 1000) ruleSets.append("price_C#");
        if (price >= 1000 && price < 5000) ruleSets.append("price_D#");
        if (price >= 5000 && price < 10000) ruleSets.append("price_E#");
        if (price >= 10000) ruleSets.append("price_F#");

        return ruleSets.substring(0, ruleSets.length() - 1);

    }

    private static boolean endLike(String phoneNumber, String reg) {

        if (reg.startsWith("%")) {
            reg = reg.replaceAll("%", "");
        }

        if (reg.endsWith("_")) {
            String p2 = phoneNumber.substring(0, phoneNumber.length() - 1);
            String reg2 = reg.replaceAll("_", "");
            return p2.endsWith(reg2);
        }
        return phoneNumber.endsWith(reg);
    }


}
