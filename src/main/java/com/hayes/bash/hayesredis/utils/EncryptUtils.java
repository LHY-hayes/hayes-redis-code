package com.hayes.bash.hayesredis.utils;

import java.security.MessageDigest;

public class EncryptUtils {

    private String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D","E", "F"};

    public String md5Digest(String src) {
        try {
            return byteArrayToHexString(md5Digest(src.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public byte[] md5Digest(byte[] src) throws Exception {
        MessageDigest alg = MessageDigest.getInstance("MD5");
        return alg.digest(src);
    }

    public String byteArrayToHexString(byte[] b) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            result.append(byteToHexString(b[i]));
        }
        return result.toString();
    }

    private String byteToHexString(byte b) {
        int n = b;
        if (n < 0) n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }
}
