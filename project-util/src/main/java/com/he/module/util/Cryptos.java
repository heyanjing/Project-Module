package com.he.module.util;

import java.security.MessageDigest;

/**
 * 加密
 * 支持HMAC-SHA1消息签名 及 DES/AES对称加密的工具类.
 * 支持Hex与Base64两种编码方式.
 */
public class Cryptos {

    /**
     * @param input 需要加密的内容
     * @param algorithm 加密算法
     * @param salt 盐 byte[] salt = SecureRandom.getSeed(8)
     * @param iterations 获取摘要的次数
     */
    public static byte[] digest(byte[] input, String algorithm, byte[] salt, int iterations) {
        byte[] result = null;
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(algorithm);
            if (salt != null) {
                digest.update(salt);
            }
            result = digest.digest(input);
            for (int i = 1; i < iterations; i++) {
                digest.reset();
                result = digest.digest(result);
            }
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
        return result;
    }

    // private static SecureRandom random = new SecureRandom();
    //
    // // ==================================================================================================================================
    // // BASE64
    // // ==================================================================================================================================
    // public static String encryptBase64(byte[] key) throws Exception {
    // return Base64.encodeBase64String(key);
    // }
    //
    // public static byte[] decryptBase64(String key) throws Exception {
    // return Base64.decodeBase64(key);
    // }
    //
    // // ==================================================================================================================================
    // // MD5
    // // ==================================================================================================================================
    // public static String encryptMd5(String input) {
    // byte[] encrypted = digest(input.getBytes(), Constant.Cryptos.MD5, null, 1);
    // return Encodes.encodeHex(encrypted);
    // }
    //
    // public static String encryptMd5(String input, String salt) {
    // return Md5Crypt.md5Crypt(input.getBytes(), salt);
    // }
    //
    // public static String encryptMd5(String data, String salt, String prefix) {
    // return Md5Crypt.md5Crypt(data.getBytes(), salt, prefix);
    // }
    //
    // public static byte[] encryptMd5(InputStream input) {
    // return digest(input, Constant.Cryptos.MD5);
    // }
    //
    // public static String encryptMd5Apr(String data, String salt) {
    // return Md5Crypt.apr1Crypt(data.getBytes(), salt);
    // }
    //
    // // ==================================================================================================================================
    // // sha1
    // // ==================================================================================================================================
    // public static byte[] encryptSha1(InputStream input) throws IOException {
    // return digest(input, SHA1);
    // }
    //
    // public static String encryptSha1(String input) {
    // return Encodes.encodeHex(encryptSha1ToByte(input));
    // }
    //
    // public static byte[] encryptSha1ToByte(String input) {
    // return digest(input.getBytes(), SHA1, null, 1);
    // }
    //
    // public static byte[] encryptSha1(byte[] input) {
    // return digest(input, SHA1, null, 1);
    // }
    //
    // public static byte[] encryptSha1(byte[] input, byte[] salt) {
    // return digest(input, SHA1, salt, 1);
    // }
    //
    // public static byte[] encryptSha1(byte[] input, byte[] salt, int iterations) {
    // return digest(input, SHA1, salt, iterations);
    // }
    //
    // public static String encryptSha1(String input, String salt) {
    // return encryptSha1(input, salt, 1);
    // }
    //
    // public static String encryptSha1(String input, String salt, int iterations) {
    // byte[] encrypted = digest(input.getBytes(), SHA1, salt.getBytes(), iterations);
    // return Encodes.encodeHex(encrypted);
    // }
    //
    // public static String encryptSha1WithHexSalt(String input, String salt, int iterations) {
    // byte[] salts = Encodes.decodeHex(salt);
    // byte[] encrypted = digest(input.getBytes(), SHA1, salts, iterations);
    // return Encodes.encodeHex(encrypted);
    // }
    //
    // // ==================================================================================================================================
    // // digest 对字符串进行散列, 支持md5与sha1算法.
    // // ==================================================================================================================================
    // private static byte[] digest(byte[] input, String algorithm, byte[] salt, int iterations) {
    // try {
    // MessageDigest digest = MessageDigest.getInstance(algorithm);
    // if (salt != null) {
    // digest.update(salt);
    // }
    // byte[] result = digest.digest(input);
    // for (int i = 1; i < iterations; i++) {
    // digest.reset();
    // result = digest.digest(result);
    // }
    // return result;
    // } catch (GeneralSecurityException e) {
    // throw Exceptions.newRuntimeException(e);
    // }
    // }
    //
    // private static byte[] digest(InputStream input, String algorithm) {
    // try {
    // MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
    // int bufferLength = 8 * 1024;
    // byte[] buffer = new byte[bufferLength];
    // int read = input.read(buffer, 0, bufferLength);
    // while (read > -1) {
    // messageDigest.update(buffer, 0, read);
    // read = input.read(buffer, 0, bufferLength);
    // }
    // return messageDigest.digest();
    // } catch (Exception e) {
    // throw Exceptions.newRuntimeException(e);
    // }
    // }
    //
    // /**
    // * 生成随机的Byte[]作为salt.
    // *
    // * @param numBytes byte数组的大小
    // */
    // public static byte[] generateSalt(int numBytes) {
    // Validate.isTrue(numBytes > 0, "numBytes argument must be a positive integer (1 or larger)", numBytes);
    // byte[] bytes = new byte[numBytes];
    // random.nextBytes(bytes);
    // return bytes;
    // }
    //
    // public static String generateSaltHexString(int numBytes) {
    // return Bytes.toHexString(generateSalt(numBytes));
    // }
}