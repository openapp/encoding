/**
 * Copyright 2012 Erik Isaksson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openapplication.encoding;

import java.nio.CharBuffer;
import java.util.regex.Pattern;

public abstract class Binary {

	private static final Pattern REGEX_BASE64 = Pattern
			.compile("[A-Za-z0-9\\-_]*");

	public static boolean isBinary(CharSequence binary) {
		if (REGEX_BASE64.matcher(binary).matches())
			switch (binary.length() % 4) {
			case 0:
				return true;
			case 1:
				return false;
			case 2:
				return (asByte(binary.charAt(binary.length() - 1)) & 0xF) == 0;
			case 3:
				return (asByte(binary.charAt(binary.length() - 1)) & 0x3) == 0;
			}
		return false;
	}

	private static final byte[] BASE64_DIGITS = new byte[] { 'A', 'B', 'C',
			'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
			'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c',
			'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
			'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2',
			'3', '4', '5', '6', '7', '8', '9', '-', '_' };

	public static char asChar(byte digit) {
		return (char) BASE64_DIGITS[digit];
	}

	public static byte asByte(char digit) {
		if (digit == '-') { // ASCII 45
			return 62; // See RFC 4648 base64url
		} else if (digit <= '9') { // ASCII 48-57 ('0'-'9')
			if (digit >= '0')
				return (byte) (digit - '0' + 52); // Base64 52-61
		} else if (digit <= 'Z') { // ASCII 65-90 ('A'-'Z')
			if (digit >= 'A')
				return (byte) (digit - 'A'); // Base64 0-25
		} else if (digit == '_') { // ASCII 95
			return 63; // See RFC 4648 base64url
		} else if (digit <= 'z') // ASCII 97-122 ('a'-'z')
			if (digit >= 'a')
				return (byte) (digit - 'a' + 26); // Base64 26-51
		throw new IllegalArgumentException(
				"Invalid Base64: forbidden character: U+"
						+ Integer.toHexString(digit));
	}

	public static char asHexChar(int digit) {
		return (char) (digit < 0xA ? (digit + '0') : (digit + 'A' - 0xA));
	}

	public static byte asHexByte(char digit) {
		if (digit <= '9') {
			if (digit >= '0')
				return (byte) (digit - '0');
		} else if (digit <= 'F') {
			if (digit >= 'A')
				return (byte) (digit - 'A' + 0xA);
		} else if (digit <= 'f')
			if (digit >= 'a')
				return (byte) (digit - 'a' + 0xA);
		throw new IllegalArgumentException(
				"Invalid hexadecimal: forbidden character: U+"
						+ Integer.toHexString(digit));
	}

	public static char[] asCharArray(byte[] binary) {
		final int size = binary.length * 8;
		final int length = size / 6;
		final int remainder = size % 6;
		final char[] chars = new char[length + (remainder == 0 ? 0 : 1)];
		for (int i = 0, j = 0; i < length; i++, j += 6) {
			int position = j / 8;
			int shift = j % 8;
			byte m = binary[position];
			if (shift <= 2)
				chars[i] = asChar((byte) (((m & 0xFF) >>> (2 - shift)) & 0x3F));
			else {
				byte l = binary[position + 1];
				chars[i] = asChar((byte) ((((m & 0xFF) << (shift - 2)) & 0x3F) | ((l & 0xFF) >>> (10 - shift))));
			}
		}
		if (remainder > 0)
			chars[length] = asChar((byte) (((binary[binary.length - 1] & 0xFF) << (6 - remainder)) & 0x3F));
		return chars;
	}

	public static String asString(byte[] binary) {
		return new String(asCharArray(binary));
	}

	public static byte[] asByteArray(CharSequence binary) {
		final int size = binary.length() * 6;
		final int length = size / 8;
		final int remainder = size % 8;
		final byte[] bytes = new byte[length];
		for (int i = 0, j = 0; i < length; i++, j += 8) {
			int position = j / 6;
			int shift = j % 6;
			char m = binary.charAt(position);
			char l = binary.charAt(position + 1);
			if (shift <= 4)
				bytes[i] = (byte) ((Binary.asByte(m) << (2 + shift)) | (Binary
						.asByte(l) >>> (4 - shift)));
			else {
				char n = binary.charAt(position + 2);
				bytes[i] = (byte) ((Binary.asByte(m) << 7)
						| (Binary.asByte(l) << 1) | (Binary.asByte(n) >>> 5));
			}
		}
		if (remainder > 0
				&& 0 != ((Binary.asByte(binary.charAt(binary.length() - 1)) << (6 - remainder)) & 0x3F))
			throw new IllegalArgumentException(
					"Invalid Base64: non-zero bits in padding");
		return bytes;
	}

	public static Binary valueOf(final byte[] binary) {
		return new Binary() {
			@Override
			public char[] toCharArray() {
				return asCharArray(binary);
			}

			@Override
			public byte[] toByteArray() {
				return binary;
			}
		};
	}

	public static Binary valueOf(final char[] binary) {
		return new Binary() {
			@Override
			public char[] toCharArray() {
				return binary;
			}

			@Override
			public byte[] toByteArray() {
				return asByteArray(CharBuffer.wrap(binary));
			}
		};
	}

	private Binary() {
	}

	public abstract char[] toCharArray();

	public abstract byte[] toByteArray();

	@Override
	public String toString() {
		return new String(toCharArray());
	}

}
