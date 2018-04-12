package com.espressif.iot.crypt;

public class byteToHex {

	public static byte byte2bit(byte a, byte b) {
		String aa = byte2String(a) + byte2String(b);

		return parseByte(aa);
	}

	public static char byte2char(byte... bs) {
		String aa = "";
		for (int i = 0; i < bs.length; i++) {
			aa += byte2String(bs[i]);
		}

		return parseChar(aa);
	}

	public static char parseChar(String a) {
		byte buf[] = a.getBytes();
		int sum = 0;
		for (int i = 0; i < buf.length; i++) {
			if (buf[i] == '1') {
				sum += Math.pow(2, buf.length - 1 - i);
			}
		}

		return (char) sum;
	}

	public static byte parseByte(String a) {
		byte buf[] = a.getBytes();
		int sum = 0;
		for (int i = 0; i < buf.length; i++) {
			if (buf[i] == '1') {
				sum += Math.pow(2, buf.length - 1 - i);
			}
		}

		return (byte) sum;
	}

	public static String byte2String(byte a) {
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] == a) {
				return bit[i];
			}
		}

		return null;
	}

	public static String String2String(String a) {
		String a1 = a.substring(1, 3);
		String a2 = a.substring(3, 5);
		String a3 = a.substring(5, 7);
		String a4 = a.substring(7, 9);
		String a5 = a.substring(9, 11);

		StringBuilder sb = new StringBuilder();
		sb.append(getChar(String.valueOf(Integer.valueOf(a.substring(0, 1)) + 3)));
		sb.append(getChar(a1));
		sb.append(getChar(a2));
		sb.append(getChar(a3));
		sb.append(getChar(a4));
		sb.append(getChar(a5));
		sb.append(a.substring(11));

		return sb.toString();

	}

	public static char getChar(String a) {
		return base64EncodeChars[Integer.parseInt(a)];
	}

	private static String bit[] = { "0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111", };

	private static byte[] bytes = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static char[] base64EncodeChars = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b',
			'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
}
