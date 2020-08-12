package com.hayes.bash.hayesredis.utils;

import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;

public class CommonUtils {

    public static List<String> subSet(Set<Object> objSet, int pageStart, int pageEnd) {


        if (CollectionUtils.isEmpty(objSet)) {
            return new ArrayList<>();
        }
        List<String> list = Arrays.asList(objSet.toArray(new String[0]));

        if (pageStart > list.size()) {
            return new ArrayList<>();
        }

        if ((pageStart + pageEnd) >= list.size()) {
            return list.subList(pageStart, list.size());
        }

        return list.subList(pageStart, pageEnd);


    }

    /**
     * 实体对象转成Map
     *
     * @param obj 实体对象
     * @return
     */
    public static Map<String, Object> object2Map(Object obj) {
        Map<String, Object> map = new HashMap<>();
        if (obj == null) {
            return map;
        }
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;


    }

    public static String md5Digest(String param) {
        EncryptUtils eu = new EncryptUtils();
        String sign = eu.md5Digest(param).toLowerCase();
        return sign;
    }

}
