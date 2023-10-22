package com.orbvpn.api.service.common;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
public class AesUtil {
    public static final String KEY_GENERATION_ALGORITHM = "AES";
    public static final String ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding";
    public static final Integer KEY_LENGTH_IN_BIT = 256;
    public static final Integer INITIAL_VECTOR_SIZE_IN_BYTE = 16;

    public static String generateKey() {
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance(KEY_GENERATION_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            log.error("unsupported encryption algorithm", e);
        }
        keyGenerator.init(KEY_LENGTH_IN_BIT);
        SecretKey key = keyGenerator.generateKey();
        String keyStr = null;
        try {
            keyStr = convertSecretKeyToString(key);
        } catch (NoSuchAlgorithmException e) {
            log.error("error in generate key", e);
        }
        return keyStr;
    }

    /**
     * generate initial vector
     *
     * @return
     */
    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[INITIAL_VECTOR_SIZE_IN_BYTE];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static String encrypt(String input, String key,
                                 String iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        IvParameterSpec ivParameterSpec = convertStringToIvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        SecretKey secretKey = convertStringToSecretKey(key);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    public static String decrypt(String cipherText, String key,
                                 String ivStr) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        IvParameterSpec ivParameterSpec = convertStringToIvParameterSpec(ivStr);
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        SecretKey secretKey = convertStringToSecretKey(key);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(cipherText));
        return new String(plainText);
    }

    public static String convertSecretKeyToString(SecretKey secretKey) throws NoSuchAlgorithmException {
        byte[] rawData = secretKey.getEncoded();
        String encodedKey = Base64.getEncoder().encodeToString(rawData);
        return encodedKey;
    }

    /**
     * @param encodedKey
     * @return
     */
    public static SecretKey convertStringToSecretKey(String encodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, KEY_GENERATION_ALGORITHM);//todo
        return originalKey;
    }

    public static String convertIvParameterSpecToString(IvParameterSpec ivParameterSpec) throws NoSuchAlgorithmException {
        byte[] rawData = ivParameterSpec.getIV();
        String encodedIv = Base64.getEncoder().encodeToString(rawData);
        return encodedIv;
    }

    public static IvParameterSpec convertStringToIvParameterSpec(String ivStr) {
        byte[] decodedIv = Base64.getDecoder().decode(ivStr);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(decodedIv);
        return ivParameterSpec;
    }
}
