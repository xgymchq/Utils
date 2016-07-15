package com.xgym.library.util;

import java.nio.charset.Charset;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 加密解密
 */
public final class AESUtil {
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 128;

    private AESUtil() {
    }

    /**
     * 解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return 解密后的数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] key, byte[] data) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(String keyPass, byte[] data) throws Exception {
        return decrypt(keyPass.getBytes(), data);
    }

    /**
     * 加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return 加密后的数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] key, byte[] data) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }

    public static byte[] encrypt(String keyPass, String data) throws Exception {
        return encrypt(keyPass.getBytes(), data.getBytes(Charset.forName("UTF-8")));
    }

    /**
     * 生成密钥
     *
     * @return 由 Base64 编码的密钥
     * @throws Exception
     */
    public static byte[] getSecretKey() throws Exception {
        return getSecretKey(null);
    }

    /**
     * 生成密钥
     *
     * @param seed 密钥种子
     * @return 由 Base64 编码的密钥
     * @throws Exception
     */
    public static byte[] getSecretKey(byte[] seed) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        if (seed != null) {
            secureRandom.setSeed(seed);
        }
        keyGenerator.init(KEY_SIZE, secureRandom);
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey.getEncoded();
    }
}
