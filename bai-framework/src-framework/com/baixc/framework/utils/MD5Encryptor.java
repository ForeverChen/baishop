package com.baixc.framework.utils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * 检验你的实现是否正确： 
 * MD5 ("") = d41d8cd98f00b204e9800998ecf8427e
 * MD5 ("a") = 0cc175b9c0f1b6a831c399e269772661
 * MD5 ("abc") = 900150983cd24fb0d6963f7d28e17f72
 * MD5 ("message digest") = f96b697d7cb7938d525a2f31aaf161d0
 * MD5 ("abcdefghijklmnopqrstuvwxyz") = c3fcd3d76192e4007dfb496cca67e13b
 */

public class MD5Encryptor {
	
	private final static Log log = LogFactory.getLog(MD5Encryptor.class);

	/**
	 * 字符串加密方法。传入一个字符串，返回经过MD5加密后的一个字符串
	 * 
	 * @param strInput
	 * @return
	 */
	public static byte[] encrypt(String strInput) {
		return encrypt(strInput, "UTF-8");
	}
	public static byte[] encrypt(byte[] byteInput) {
		try {
			return DigestEncryptor.encrypt(byteInput, "MD5");
		} catch (NoSuchAlgorithmException nsae) {
			log.error("No such Algorithm in digest");
			return new byte[] {};
		}
	}

	public static String encryptHex(String strInput) {
		 return encryptHex(strInput, "UTF-8");
	}
	public static String encryptHex(String strInput, String charset) {
		 byte b[] = encrypt(strInput, charset);
		 return DigestEncryptor.binToHex(b);
	}
	
	public static String encryptHex(byte[] byteInput) {
		 byte b[] = encrypt(byteInput);
		 return DigestEncryptor.binToHex(b);
	}
	
	private static byte[] encrypt(String strInput, String charset) {
		try {
			return encrypt(strInput.getBytes(charset));
		} catch (UnsupportedEncodingException e) {
			return encrypt(strInput.getBytes());
		}
	}

	public static void main(String args[]) {
		System.out.println(encryptHex(""));
		System.out.println(encryptHex("a"));
		System.out.println(encryptHex("abc"));
		System.out.println(encryptHex("message digest"));
	}

}
