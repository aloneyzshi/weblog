package com.netease.qa.log.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class MD5Utils {
	
	private static final Logger logger = LoggerFactory.getLogger(MD5Utils.class);

	public static String getMD5(String input){ 
		MessageDigest digest = null; 
		try {
			digest = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e) {
			logger.error("catch NoSuchAlgorithmException, input String: " + input);
		} 
		digest.update(input.getBytes());

		byte[] hash = digest.digest();
		StringBuilder StringBuilder = new StringBuilder();
		for (int i = 0; i < hash.length; i++) {
			StringBuilder.append(getByteAsHexString(hash[i]));
		}
		return StringBuilder.toString();
	}
	
	
	/**
	 * 将一个byte转换成两个ASCII字符的16进制格式。<p>
	 * 例如：0xEF --> "EF"
	 * @param b
	 * @return b的16进制格式字符串
	 */
	private static String getByteAsHexString(byte b) {
		char[] buf = new char[2];

		buf[1] = digits[(int) (b & 0x0f)];
		b >>>= 4;
		buf[0] = digits[(int) (b & 0x0f)];

		return new String(buf);
	}
	
	
	/**
	 * 16进制的各个字符。
	 */
	private static final char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };
	
}
