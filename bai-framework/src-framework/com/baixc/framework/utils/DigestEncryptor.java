package com.baixc.framework.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestEncryptor {
	static char[] HEX_DIGIT = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * 字符串SHA-1摘要算法。传入一个字符串，返回经过SHA-1加密后的一个字符串
	 * 
	 * @param strInput
	 * @return
	 */
	public static byte[] encrypt(String strInput, String algorithm)
			throws NoSuchAlgorithmException {
		return encrypt(strInput.getBytes(), algorithm);
	}

	public static byte[] encrypt(byte[] byteInput, String algorithm)
			throws NoSuchAlgorithmException {
		if (byteInput == null)
			throw new IllegalArgumentException(
					"Encrypt digest or algorithm shouldn't be null");

		MessageDigest md = MessageDigest.getInstance(algorithm);
		md.update(byteInput);
		return md.digest();
	}

//	public static boolean isEqual(byte[] digesta, byte[] digestb,
//			String algorithm) throws NoSuchAlgorithmException {
////		MessageDigest md = MessageDigest.getInstance(algorithm);
//		return MessageDigest.isEqual(digesta, digestb);
//	}

	static String binToHex(byte bin[]) {
		if (bin == null)
			throw new IllegalArgumentException(
					"Parameter bin shouldn't be null");
		StringBuffer hexStr = new StringBuffer();
		for (int i = 0; i < bin.length; i++) {
			char[] ob = new char[2];
			ob[0] = HEX_DIGIT[(bin[i] >>> 4) & 0X0F];
			ob[1] = HEX_DIGIT[bin[i] & 0X0F];
			hexStr.append(new String(ob));
		}
		return hexStr.toString();
	}
}
