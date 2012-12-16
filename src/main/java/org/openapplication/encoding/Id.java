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

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.openapplication.encoding.Binary.asByte;
import static org.openapplication.encoding.Binary.asChar;
import static org.openapplication.encoding.Binary.asHexByte;
import static org.openapplication.encoding.Binary.asHexChar;

public abstract class Id {

	private static final Pattern REGEX_UUID = Pattern
			.compile("[A-Za-z0-9\\-_]{21,22}|[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");

	private static final UUID NS_URI = UUID
			.fromString("6ba7b811-9dad-11d1-80b4-00c04fd430c8");

	public static boolean isUuid(CharSequence uuid) {
		if (uuid.length() < 21)
			return false;
		return REGEX_UUID.matcher(uuid).matches();
	}

	public static byte[] asByteArray(UUID uuid) {
		final long m = uuid.getMostSignificantBits();
		final long l = uuid.getLeastSignificantBits();
		final long x = 0xFF00000000000000L;
		return new byte[] {
				// Most significant 64 bits
				(byte) ((m & (x >>> 0)) >>> 56),
				(byte) ((m & (x >>> 8)) >>> 48),
				(byte) ((m & (x >>> 16)) >>> 40),
				(byte) ((m & (x >>> 24)) >>> 32),
				(byte) ((m & (x >>> 32)) >>> 24),
				(byte) ((m & (x >>> 40)) >>> 16),
				(byte) ((m & (x >>> 48)) >>> 8),
				(byte) ((m & (x >>> 56)) >>> 0),
				// Least significant 64 bits
				(byte) ((l & (x >>> 0)) >>> 56),
				(byte) ((l & (x >>> 8)) >>> 48),
				(byte) ((l & (x >>> 16)) >>> 40),
				(byte) ((l & (x >>> 24)) >>> 32),
				(byte) ((l & (x >>> 32)) >>> 24),
				(byte) ((l & (x >>> 40)) >>> 16),
				(byte) ((l & (x >>> 48)) >>> 8),
				(byte) ((l & (x >>> 56)) >>> 0) };
	}

	public static void asByteArray(UUID uuid, ByteBuffer out) {
		final long m = uuid.getMostSignificantBits();
		final long l = uuid.getLeastSignificantBits();
		final long x = 0xFF00000000000000L;
		// Most significant 64 bits
		out.put((byte) ((m & (x >>> 0)) >>> 56));
		out.put((byte) ((m & (x >>> 8)) >>> 48));
		out.put((byte) ((m & (x >>> 16)) >>> 40));
		out.put((byte) ((m & (x >>> 24)) >>> 32));
		out.put((byte) ((m & (x >>> 32)) >>> 24));
		out.put((byte) ((m & (x >>> 40)) >>> 16));
		out.put((byte) ((m & (x >>> 48)) >>> 8));
		out.put((byte) ((m & (x >>> 56)) >>> 0));
		// Least significant 64 bits
		out.put((byte) ((l & (x >>> 0)) >>> 56));
		out.put((byte) ((l & (x >>> 8)) >>> 48));
		out.put((byte) ((l & (x >>> 16)) >>> 40));
		out.put((byte) ((l & (x >>> 24)) >>> 32));
		out.put((byte) ((l & (x >>> 32)) >>> 24));
		out.put((byte) ((l & (x >>> 40)) >>> 16));
		out.put((byte) ((l & (x >>> 48)) >>> 8));
		out.put((byte) ((l & (x >>> 56)) >>> 0));
	}

	public static UUID asUuid(byte[] uuid) {
		return new UUID( // Most significant 64 bits
				(((long) uuid[0] & 0xFF) << 56)
						| (((long) (uuid[1] & 0xFF)) << 48)
						| (((long) (uuid[2] & 0xFF)) << 40)
						| (((long) (uuid[3] & 0xFF)) << 32)
						| (((long) (uuid[4] & 0xFF)) << 24)
						| (((long) (uuid[5] & 0xFF)) << 16)
						| (((long) (uuid[6] & 0xFF)) << 8)
						| (((long) (uuid[7] & 0xFF)) << 0),
				// Least significant 64 bits
				(((long) uuid[8] & 0xFF) << 56)
						| ((long) (uuid[9] & 0xFF) << 48)
						| ((long) (uuid[10] & 0xFF) << 40)
						| ((long) (uuid[11] & 0xFF) << 32)
						| ((long) (uuid[12] & 0xFF) << 24)
						| ((long) (uuid[13] & 0xFF) << 16)
						| ((long) (uuid[14] & 0xFF) << 8)
						| ((long) (uuid[15] & 0xFF) << 0));
	}

	public static UUID asUuid(ByteBuffer in) {
		return new UUID( // Most significant 64 bits
				(((long) in.get() & 0xFF) << 56)
						| (((long) (in.get() & 0xFF)) << 48)
						| (((long) (in.get() & 0xFF)) << 40)
						| (((long) (in.get() & 0xFF)) << 32)
						| (((long) (in.get() & 0xFF)) << 24)
						| (((long) (in.get() & 0xFF)) << 16)
						| (((long) (in.get() & 0xFF)) << 8)
						| (((long) (in.get() & 0xFF)) << 0),
				// Least significant 64 bits
				(((long) in.get() & 0xFF) << 56)
						| ((long) (in.get() & 0xFF) << 48)
						| ((long) (in.get() & 0xFF) << 40)
						| ((long) (in.get() & 0xFF) << 32)
						| ((long) (in.get() & 0xFF) << 24)
						| ((long) (in.get() & 0xFF) << 16)
						| ((long) (in.get() & 0xFF) << 8)
						| ((long) (in.get() & 0xFF) << 0));
	}

	public static char[] asCharArray(UUID uuid) {
		final long m = uuid.getMostSignificantBits();
		final long l = uuid.getLeastSignificantBits();
		if (2 != (l >>> 62)) // If the UUID variant isn't standard
			return Binary.asCharArray(asByteArray(uuid));
		final long x = 0xFC00000000000000L;
		return new char[] {
				// Most significant 64 bits
				asChar((byte) ((m & (x >>> 0)) >>> 58)), // 0-5
				asChar((byte) ((m & (x >>> 6)) >>> 52)), // 6-11
				asChar((byte) ((m & (x >>> 12)) >>> 46)), // 12-17
				asChar((byte) ((m & (x >>> 18)) >>> 40)), // 18-23
				asChar((byte) ((m & (x >>> 24)) >>> 34)), // 24-29
				asChar((byte) ((m & (x >>> 30)) >>> 28)), // 30-35
				asChar((byte) ((m & (x >>> 36)) >>> 22)), // 36-41
				asChar((byte) ((m & (x >>> 42)) >>> 16)), // 42-47
				asChar((byte) ((m & (x >>> 48)) >>> 10)), // 48-53
				asChar((byte) ((m & (x >>> 54)) >>> 4)), // 54-59
				asChar((byte) (((m & (x >>> 60)) << 2) // 60-63
				// Least significant 64 bits
				// Ignoring bits 64-65! For standard UUIDs, they are always 1 0,
				// so we skip them to get 126 bits of output (which can be
				// Base64 encoded without overhead)
				| ((l >>> 60) & 0x3))), // 66-67
				asChar((byte) ((l & (x >>> 4)) >>> 54)), // 68-73
				asChar((byte) ((l & (x >>> 10)) >>> 48)), // 74-79
				asChar((byte) ((l & (x >>> 16)) >>> 42)), // 80-85
				asChar((byte) ((l & (x >>> 22)) >>> 36)), // 86-91
				asChar((byte) ((l & (x >>> 28)) >>> 30)), // 92-97
				asChar((byte) ((l & (x >>> 34)) >>> 24)), // 98-103
				asChar((byte) ((l & (x >>> 40)) >>> 18)), // 104-109
				asChar((byte) ((l & (x >>> 46)) >>> 12)), // 110-115
				asChar((byte) ((l & (x >>> 52)) >>> 6)), // 116-121
				asChar((byte) ((l & (x >>> 58)) >>> 0)) // 122-127
		};
	}

	public static String asString(UUID uuid) {
		return new String(asCharArray(uuid));
	}

	public static UUID asUuid(CharSequence uuid) {
		switch (uuid.length()) {
		case 21:
			return new UUID( // Most significant 64 bits
					((long) asByte(uuid.charAt(0)) << 58)
							| ((long) asByte(uuid.charAt(1)) << 52)
							| ((long) asByte(uuid.charAt(2)) << 46)
							| ((long) asByte(uuid.charAt(3)) << 40)
							| ((long) asByte(uuid.charAt(4)) << 34)
							| ((long) asByte(uuid.charAt(5)) << 28)
							| ((long) asByte(uuid.charAt(6)) << 22)
							| ((long) asByte(uuid.charAt(7)) << 16)
							| ((long) asByte(uuid.charAt(8)) << 10)
							| ((long) asByte(uuid.charAt(9)) << 4)
							| ((long) asByte(uuid.charAt(10)) >>> 2),
					// Least significant 64 bits
					// Bits 64-65 are always 1 0 for standard UUIDs, and those
					// bits are assumed to be skipped in the input because
					// without them, there is no longer a need to incur any
					// Base64 encoding overhead (128-2=126 is divisible by 6)
					((0x8 | (0x3 & (long) asByte(uuid.charAt(10)))) << 60)
							| ((long) asByte(uuid.charAt(11)) << 54)
							| ((long) asByte(uuid.charAt(12)) << 48)
							| ((long) asByte(uuid.charAt(13)) << 42)
							| ((long) asByte(uuid.charAt(14)) << 36)
							| ((long) asByte(uuid.charAt(15)) << 30)
							| ((long) asByte(uuid.charAt(16)) << 24)
							| ((long) asByte(uuid.charAt(17)) << 18)
							| ((long) asByte(uuid.charAt(18)) << 12)
							| ((long) asByte(uuid.charAt(19)) << 6)
							| ((long) asByte(uuid.charAt(20)) << 0));
		case 22:
			return asUuid(Binary.asByteArray(uuid));
		case 36:
			return UUID.fromString(uuid.toString());
		}
		throw new IllegalArgumentException(
				"The character sequence is not a recognized UUID");
	}

	public static UUID asUuid(final URI uri) {
		if (uri.isOpaque() && "urn".equals(uri.getScheme())
				&& uri.getSchemeSpecificPart().startsWith("uuid:"))
			return UUID.fromString(uri.getSchemeSpecificPart().substring(5));
		return asUuid(NS_URI, new Name() {
			@Override
			public void update(MessageDigest digest) {
				digest.update(Text.asByteBuffer(uri.toString()));
			}
		});
	}

	public static URI asUri(UUID uuid) {
		try {
			return new URI("urn:uuid", uuid.toString(), null);
		} catch (URISyntaxException e) {
			throw new Error(e);
		}
	}

	public interface Name {
		void update(MessageDigest digest);
	}

	public static UUID asUuid(UUID namespace, Name name) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-1 message digest unavailable");
		}
		digest.update(asByteArray(namespace));
		name.update(digest);
		byte[] hash = digest.digest();
		return new UUID( // Most significant 64 bits
				(((long) (hash[0] & 0xFF)) << 56)
						| (((long) (hash[1] & 0xFF)) << 48)
						| (((long) (hash[2] & 0xFF)) << 40)
						| (((long) (hash[3] & 0xFF)) << 32)
						| (((long) (hash[4] & 0xFF)) << 24)
						| (((long) (hash[5] & 0xFF)) << 16)
						| (((long) ((hash[6] & 0xF) | 0x50)) << 8)
						| (((long) (hash[7] & 0xFF)) << 0),
				// Least significant 64 bits
				(((long) ((hash[8] & 0x3F) | 0x80)) << 56)
						| ((long) (hash[9] & 0xFF) << 48)
						| ((long) (hash[10] & 0xFF) << 40)
						| ((long) (hash[11] & 0xFF) << 32)
						| ((long) (hash[12] & 0xFF) << 24)
						| ((long) (hash[13] & 0xFF) << 16)
						| ((long) (hash[14] & 0xFF) << 8)
						| ((long) (hash[15] & 0xFF) << 0));
	}

	public static CharSequence encode(CharSequence in) {
		StringBuilder out = new StringBuilder(in.length() * 3 / 2);
		encode(in, out);
		return out;
	}

	public static void encode(CharSequence in, StringBuilder out) {
		byte[] bytes = new byte[4];
		int length = in.length();
		for (int i = 0; i < length; i++) {
			// Check whether the character is unreserved according to RFC 3987
			char c = in.charAt(i);
			int cc;
			if ((c >= 'a' && c <= 'z')
					|| (c >= 'A' && c <= 'Z') // Alpha
					|| (c >= '0' && c <= '9') // Digit
					|| c == '-' || c == '.' || c == '_' || c == '~'
					|| (c >= '\u00A0' && c <= '\uD7FF') // UCS
					|| (c >= '\uF900' && c <= '\uFDCF') // UCS
					|| (c >= '\uFDF0' && c <= '\uFFEF') // UCS
			) { // Is unreserved and is in the Basic Multilingual Plane
				out.append(c);
				continue;
			} else if (c >= Character.MIN_SURROGATE
					&& c <= Character.MAX_SURROGATE) { // Surrogate
				if (c > Character.MAX_HIGH_SURROGATE) // If not lead surrogate
					throw new IllegalArgumentException(
							"Tail surrogate without lead surrogate");
				char c2 = in.charAt(++i); // Fetch tail surrogate
				if (c2 < Character.MIN_LOW_SURROGATE
						|| c2 > Character.MAX_SURROGATE)
					throw new IllegalArgumentException("Invalid tail surrogate");
				cc = Character.MIN_SUPPLEMENTARY_CODE_POINT
						| ((c - Character.MIN_SURROGATE) << 10)
						| (c2 - Character.MIN_LOW_SURROGATE);
				if ((cc >= Character.MIN_SUPPLEMENTARY_CODE_POINT && cc <= 0x1FFFD) // UCS
						|| (cc >= 0x20000 && cc <= 0x2FFFD) // UCS
						|| (cc >= 0x30000 && cc <= 0x3FFFD) // UCS
						|| (cc >= 0x40000 && cc <= 0x4FFFD) // UCS
						|| (cc >= 0x50000 && cc <= 0x5FFFD) // UCS
						|| (cc >= 0x60000 && cc <= 0x6FFFD) // UCS
						|| (cc >= 0x70000 && cc <= 0x7FFFD) // UCS
						|| (cc >= 0x80000 && cc <= 0x8FFFD) // UCS
						|| (cc >= 0x90000 && cc <= 0x9FFFD) // UCS
						|| (cc >= 0xA0000 && cc <= 0xAFFFD) // UCS
						|| (cc >= 0xB0000 && cc <= 0xBFFFD) // UCS
						|| (cc >= 0xC0000 && cc <= 0xCFFFD) // UCS
						|| (cc >= 0xD0000 && cc <= 0xDFFFD) // UCS
						|| (cc >= 0xE1000 && cc <= 0xEFFFD) // UCS
				) { // Is unreserved and is in the Supplementary Planes
					out.append(c);
					out.append(c2);
					continue;
				} // else: Is not unreserved and is in the Supplementary Planes
			} else
				// Is not unreserved and is in the Basic Multilingual Plane
				cc = c;

			// UTF-8 encode
			int size;
			if (cc <= 0x7F) {
				size = 1;
				bytes[0] = (byte) cc;
			} else if (cc <= 0x7FF) {
				size = 2;
				bytes[0] = (byte) (0xC0 | (cc >>> 6));
				bytes[1] = (byte) (0x80 | (cc & 0x3F));
			} else if (cc <= Character.MAX_VALUE) {
				size = 3;
				bytes[0] = (byte) (0xE0 | (cc >>> 12));
				bytes[1] = (byte) (0x80 | ((cc >>> 6) & 0x3F));
				bytes[2] = (byte) (0x80 | (cc & 0x3F));
			} else { // if (cc <= 0x1FFFFF) {
				size = 4;
				bytes[0] = (byte) (0xF0 | (cc >>> 18));
				bytes[1] = (byte) (0x80 | ((cc >>> 12) & 0x3F));
				bytes[2] = (byte) (0x80 | ((cc >>> 6) & 0x3F));
				bytes[3] = (byte) (0x80 | (cc & 0x3F));
			}

			// Percent encode
			for (int j = 0; j < size; j++) {
				out.append('%');
				out.append(asHexChar((bytes[j] & 0xF0) >>> 4));
				out.append(asHexChar(bytes[j] & 0xF));
			}
		}
	}

	public static String decode(String in) {
		StringBuilder out = new StringBuilder(in.length());
		decode(in, out);
		return out.toString();
	}

	private static final char REPLACEMENT_CHARACTER = '\uFFFD';

	public static void decode(CharSequence in, StringBuilder out) {
		final int length = in.length();
		int octets = 0, remaining = 0, codepoint = 0;
		char c;
		int b;
		for (int i = 0; i < length; i++)
			switch (c = in.charAt(i)) {
			case '%':
				if (length - i < 3) {
					out.append(REPLACEMENT_CHARACTER);
					return;
				}
				try {
					b = (asHexByte(in.charAt(++i)) << 4)
							| asHexByte(in.charAt(++i));
				} catch (IllegalArgumentException e) {
					out.append(REPLACEMENT_CHARACTER);
					remaining = 0;
					continue;
				}
				if (remaining == 0) {
					if ((b >>> 7) == 0) // 0xxxxxxx, 7 bits
						out.append((char) b);
					else if ((b >>> 5) == 0x6) { // 110xxxxx, 11 bits
						remaining = (octets = 2) - 1;
						codepoint = (b & 0x1F) << 6;
					} else if ((b >>> 4) == 0xE) { // 1110xxxx, 16 bits
						remaining = (octets = 3) - 1;
						codepoint = (b & 0xF) << 12;
					} else if ((b >>> 3) == 0x1E) { // 11110xxx, 21 bits
						remaining = (octets = 4) - 1;
						codepoint = (b & 0x7) << 18;
					} else if ((b >>> 2) == 0x3E) { // 111110xx, 26 bits
						// Overlong for any code point, but try to process the
						// whole sequence which will then be replaced
						remaining = (octets = 5) - 1;
						codepoint = (b & 0x3) << 24;
					} else if ((b >>> 1) == 0x7E) { // 1111110x, 31 bits
						// Overlong for any code point, but try to process the
						// whole sequence which will then be replaced
						remaining = (octets = 6) - 1;
						codepoint = (b & 0x1) << 30;
					} else
						out.append(REPLACEMENT_CHARACTER);
				} else if ((b >>> 6) == 0x2) { // 10xxxxxx
					codepoint |= (b & 0x3F) << (6 * --remaining);
					if (remaining == 0) {
						if (codepoint <= 0x7F // octets > 1
								|| (codepoint <= 0x7FF && octets > 2)
								|| (codepoint <= Character.MAX_VALUE && octets > 3)
								|| (codepoint <= Character.MAX_CODE_POINT && octets > 4))
							// Overlong sequence (see RFC 3629)
							out.append(REPLACEMENT_CHARACTER);
						else if (codepoint < Character.MIN_SURROGATE)
							// Basic Multilingual Plane below surrogates
							out.append((char) codepoint);
						else if (codepoint <= Character.MAX_VALUE) {
							if (codepoint > Character.MAX_SURROGATE)
								// Basic Multilingual Plane above surrogates
								out.append((char) codepoint);
							else
								// Is a surrogate and to be regarded as an error
								out.append(REPLACEMENT_CHARACTER);
						} else if (codepoint <= Character.MAX_CODE_POINT) {
							// In the Supplementary Planes
							// Two UTF-16 code points are required
							codepoint -= Character.MIN_SUPPLEMENTARY_CODE_POINT;
							out.append((char) (Character.MIN_SURROGATE + (codepoint >>> 10)));
							out.append((char) (Character.MIN_LOW_SURROGATE + (codepoint & 0x3FF)));
						} else
							// Invalid codepoint > max
							out.append(REPLACEMENT_CHARACTER);
					}
				} else {
					out.append(REPLACEMENT_CHARACTER);
					remaining = 0;
				}
				break;
			case '+':
				out.append(' ');
				break;
			default:
				out.append(c);
			}
		if (remaining > 0) // If the last sequence was not completed
			out.append(REPLACEMENT_CHARACTER);
	}

	public static Id valueOf(final UUID id) {
		return new Id() {
			@Override
			public UUID toUuid() {
				return id;
			}

			@Override
			public char[] toCharArray() {
				return asCharArray(id);
			}

			@Override
			public byte[] toByteArray() {
				return asByteArray(id);
			}
		};
	}

	public static Id valueOf(byte[] id) {
		final UUID uuid = asUuid(id);
		return new Id() {
			@Override
			public UUID toUuid() {
				return uuid;
			}
		};
	}

	public static Id valueOf(CharSequence id) {
		final UUID uuid = asUuid(id);
		return new Id() {
			@Override
			public UUID toUuid() {
				return uuid;
			}
		};
	}

	public static Id valueOf(final URI id) {
		final UUID uuid = asUuid(id);
		return new Id() {
			@Override
			public UUID toUuid() {
				return uuid;
			}

			@Override
			public URI toUri() {
				return id;
			}
		};
	}

	private Id() {
	}

	public abstract UUID toUuid();

	public char[] toCharArray() {
		return asCharArray(toUuid());
	}

	public byte[] toByteArray() {
		return asByteArray(toUuid());
	}

	public URI toUri() {
		return asUri(toUuid());
	}

	@Override
	public String toString() {
		return asString(toUuid());
	}

}
