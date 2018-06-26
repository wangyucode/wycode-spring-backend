package cn.wycode.web.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密相关  MD5,SHA1,SHA256,AES
 * author: wycode.cn
 * date: 2018-4-18 15:06:45
 */
public class EncryptionUtil {

    public static final String MD5 = "MD5";
    public static final String SHA_1 = "SHA-1";
    public static final String SHA_256 = "SHA-256";


    /**
     * 由于MD5 与SHA-1均是从MD4 发展而来，它们的结构和强度等特�?�有很多相似之处
     * SHA-1与MD5 的最大区别在于其摘要比MD5 摘要�? 32 比特�?1byte=8bit，相当于�?4byte，转�?16进制后比MD5�?8个字符）�?
     * 对于强行攻击，：MD5 �?2128 数量级的操作，SHA-1 �?2160数量级的操作�?
     * 对于相同摘要的两个报文的难度：MD5�? 264 是数量级的操作，SHA-1 �?280 数量级的操作�?
     * 因�?�，SHA-1 对强行攻击的强度更大�?
     * 但由于SHA-1 的循环步骤比MD5 多（80:64）且要处理的缓存大（160 比特:128 比特），SHA-1 的运行�?�度比MD5 慢�??
     *
     * @param source    �?要加密的字符�?
     * @param algorithm 加密算法 （MD5 ,SHA-1,SHA-256�?
     * @return 加密结果
     */
    public static String getHash(String source, String algorithm) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        md5.update(source.getBytes());
        return byteArrayToString(md5.digest());
    }

    public static String byteArrayToString(byte[] array){
        StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            sb.append(String.format("%02X", b)); // 10进制�?16进制，X 表示以十六进制形式输出，02 表示不足两位前面�?0输出
        }
        return sb.toString();
    }


}
