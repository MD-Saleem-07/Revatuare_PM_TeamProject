package com.revature.pm.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import com.revature.pm.exception.InvalidOperationException;

public class AESUtil {

	private static final String SECRET_KEY = "MySuperSecretKey"; // 16 characters for AES-128

	public static String encrypt(String data) {
		try {
			SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, key);

			byte[] encrypted = cipher.doFinal(data.getBytes());
			return Base64.getEncoder().encodeToString(encrypted);

		} catch (Exception e) {
			throw new InvalidOperationException("Error while encrypting password");
		}
	}

	public static String decrypt(String encryptedData) {
		try {
			SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, key);

			byte[] decoded = Base64.getDecoder().decode(encryptedData);
			return new String(cipher.doFinal(decoded));

		} catch (Exception e) {
			throw new InvalidOperationException("Error while decrypting password");
		}
	}
}
