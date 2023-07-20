package com.pallycon.cpix.util;

import java.util.Base64;

public class StringUtil {
	public static byte[] hexStringToByteArray(String hexadecimalString) {
		int len = hexadecimalString.length();
		byte[] byteArray = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			byteArray[i / 2] = (byte) ((Character.digit(hexadecimalString.charAt(i), 16) << 4)
				+ Character.digit(hexadecimalString.charAt(i + 1), 16));
		}
		return byteArray;
	}

	public static String byteArrayToHexString(byte[] byteArray) {
		StringBuilder sb = new StringBuilder();
		for (byte b : byteArray) {
			sb.append(String.format("%02X", b));
		}
		return sb.toString();
	}

	public static String decodeBase64(String base64String) {
		byte[] decodedBytes = Base64.getDecoder().decode(base64String);
		return new String(decodedBytes);
	}
}
