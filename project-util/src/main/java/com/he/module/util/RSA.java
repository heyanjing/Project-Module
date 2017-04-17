package com.he.module.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

import javax.crypto.Cipher;

import com.google.common.collect.Maps;

public class RSA {
    /**
     * 加密算法
     */
    public static final String  ALGORITHM           = "RSA";
    /**
     * 签名算法
     */
    public static final String  SIGNATURE_ALGORITHM = "MD5withRSA";
    /**
     * 密钥长度
     */
    public static final Integer LENGTH              = 1024;

    /**
     * 1024的密钥长度允许加密的最大明文长度
     * 待加密的字节数不能超过密钥的长度值除以 8 再减去 11
     */
    public static final Integer MAX_ENCRYPT_LENGTH  = 117;
    /**
     * 1024的密钥长度允许解密的最大加密长度
     * 加密后得到密文的字节数，正好是密钥的长度值除以 8
     */
    public static final Integer MAX_DECRYPT_LENGTH  = 128;
    private static final String PUBLIC_KEY          = "RSAPublicKey";
    private static final String PRIVATE_KEY         = "RSAPrivateKey";

    /**
     * 生成密钥对
     */
    public static Map<String, Key> genKeyPair() {
        try {
            Map<String, Key> keyMap = Maps.newHashMap();
            KeyPairGenerator keyPairGen;
            keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGen.initialize(LENGTH);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            keyMap.put(PUBLIC_KEY, publicKey);
            keyMap.put(PRIVATE_KEY, privateKey);
            return keyMap;
        } catch (NoSuchAlgorithmException e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static Key getPulicKey(Map<String, Key> map) {
        return map.get(PUBLIC_KEY);
    }

    public static Key getPrivateKey(Map<String, Key> map) {
        return map.get(PRIVATE_KEY);
    }

    /**
     * 公钥加密
     * 
     * @param data 需要加密的数据
     * @param publicKey 密钥对中的公钥
     * @return 加密后数据的字节数组
     */
    public static byte[] encryptByPublicKey(byte[] dataBytes, Key publicKey) {
        ByteArrayOutputStream out = null;
        try {
            byte[] publicKeyBytes = publicKey.getEncoded();
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            Key encryptPublicKey = keyFactory.generatePublic(x509KeySpec);

            System.err.println(keyFactory.getAlgorithm());

            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, encryptPublicKey);
            // 对数据分段加密
            out = new ByteArrayOutputStream();
            int inputLen = dataBytes.length;
            int offSet = 0;
            byte[] cache;
            int i = 0;
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_LENGTH) {
                    cache = cipher.doFinal(dataBytes, offSet, MAX_ENCRYPT_LENGTH);
                } else {
                    cache = cipher.doFinal(dataBytes, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_LENGTH;
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 私钥解密
     * 
     * @param encryptData 加密后数据的字节数组
     * @param privateKey 密钥对中的私钥
     * @return 解密后数据的字节数组
     */
    public static byte[] decryptByPrivateKey(byte[] encryptData, Key privateKey) {
        ByteArrayOutputStream out = null;
        try {
            byte[] privateKeyBytes = privateKey.getEncoded();
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            Key decryptPrivate = keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, decryptPrivate);

            // 对数据分段解密
            out = new ByteArrayOutputStream();
            int inputLen = encryptData.length;
            int offSet = 0;
            byte[] cache;
            int i = 0;
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_LENGTH) {
                    cache = cipher.doFinal(encryptData, offSet, MAX_DECRYPT_LENGTH);
                } else {
                    cache = cipher.doFinal(encryptData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_LENGTH;
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 私钥加密
     * 
     * @param data 需要加密的数据
     * @param privateKey 密钥对中的私钥
     * @return 加密后数据的字节数组
     */
    public static byte[] encryptByPrivateKey(byte[] dataBytes, Key privateKey) {
        ByteArrayOutputStream out = null;
        try {
            byte[] privateKeyBytes = privateKey.getEncoded();
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            Key encryptPrivateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, encryptPrivateKey);
            // 对数据分段加密
            out = new ByteArrayOutputStream();
            int inputLen = dataBytes.length;
            int offSet = 0;
            byte[] cache;
            int i = 0;
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_LENGTH) {
                    cache = cipher.doFinal(dataBytes, offSet, MAX_ENCRYPT_LENGTH);
                } else {
                    cache = cipher.doFinal(dataBytes, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_LENGTH;
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * 公钥解密
     * 
     * @param encryptData 加密后数据的字节数组
     * @param publicKey 密钥对中的公钥
     * @return 解密后数据的字节数组
     */
    public static byte[] decryptByPublicKey(byte[] encryptData, Key publicKey) {
        ByteArrayOutputStream out = null;
        try {
            byte[] publicKeyBytes = publicKey.getEncoded();
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            Key decryptPublicKey = keyFactory.generatePublic(x509KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, decryptPublicKey);

            // 对数据分段解密
            int inputLen = encryptData.length;
            out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_LENGTH) {
                    cache = cipher.doFinal(encryptData, offSet, MAX_DECRYPT_LENGTH);
                } else {
                    cache = cipher.doFinal(encryptData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_LENGTH;
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 私钥签名
     * 
     * @param data 需要签名数据的字节数组
     * @param privateKey 密钥对中的私钥
     * @return 数字签名的字节数组
     */
    public static byte[] signByPrivateKey(byte[] data, Key privateKey) {
        try {
            byte[] privateKeyBytes = privateKey.getEncoded();
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey signPrivateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(signPrivateKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    /**
     * 公钥验证签名
     * 
     * @param data 原始数据的字节数组
     * @param signData 已签名的数据字节数组
     * @param publicKey 密钥对中的公钥
     * @return
     */
    public static boolean verifySignByPublicKey(byte[] data, byte[] signData, Key publicKey) {
        try {
            byte[] publicKeyBytes = publicKey.getEncoded();
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PublicKey verifyPublicKey = keyFactory.generatePublic(keySpec);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(verifyPublicKey);
            signature.update(data);
            return signature.verify(signData);
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }
}
