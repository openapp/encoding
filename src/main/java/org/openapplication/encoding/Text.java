package org.openapplication.encoding;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

public abstract class Text {

	private static final Charset CHARSET = Charset.forName("UTF-8");

	private static final ThreadLocal<CharsetEncoder> localEncoder = new ThreadLocal<CharsetEncoder>() {
		@Override
		protected CharsetEncoder initialValue() {
			return CHARSET.newEncoder()
					.onMalformedInput(CodingErrorAction.REPLACE)
					.onUnmappableCharacter(CodingErrorAction.REPLACE);
		}
	};

	private static final ThreadLocal<CharsetDecoder> localDecoder = new ThreadLocal<CharsetDecoder>() {
		@Override
		protected CharsetDecoder initialValue() {
			return CHARSET.newDecoder()
					.onMalformedInput(CodingErrorAction.REPLACE)
					.onUnmappableCharacter(CodingErrorAction.REPLACE);
		}
	};

	public static CharBuffer toCharBuffer(ByteBuffer text) {
		try {
			return localDecoder.get().decode(text);
		} catch (CharacterCodingException e) {
			throw new Error(e);
		}
	}

	public static ByteBuffer toByteBuffer(CharSequence text) {
		return toByteBuffer(CharBuffer.wrap(text));
	}

	public static ByteBuffer toByteBuffer(CharBuffer text) {
		try {
			return localEncoder.get().encode(text);
		} catch (CharacterCodingException e) {
			throw new Error(e);
		}
	}

	public static Text valueOf(ByteBuffer text) {
		final ByteBuffer byteBuffer = text.duplicate();
		return new Text() {
			@Override
			public CharBuffer toCharBuffer() {
				return toCharBuffer(byteBuffer.duplicate());
			}

			@Override
			public ByteBuffer toByteBuffer() {
				return byteBuffer.duplicate();
			}
		};
	}

	public static Text valueOf(CharBuffer text) {
		final CharBuffer charBuffer = text.duplicate();
		return new Text() {
			@Override
			public CharBuffer toCharBuffer() {
				return charBuffer.duplicate();
			}

			@Override
			public ByteBuffer toByteBuffer() {
				return toByteBuffer(charBuffer.duplicate());
			}
		};
	}

	public static Text valueOf(CharSequence text) {
		final CharBuffer charBuffer = CharBuffer.wrap(text);
		return new Text() {
			@Override
			public CharBuffer toCharBuffer() {
				return charBuffer.duplicate();
			}

			@Override
			public ByteBuffer toByteBuffer() {
				return toByteBuffer(charBuffer.duplicate());
			}
		};
	}

	private Text() {
	}

	public abstract CharBuffer toCharBuffer();

	public abstract ByteBuffer toByteBuffer();

	public void writeTo(OutputStream out) throws IOException {
		ByteBuffer in = toByteBuffer();
		out.write(in.array(), in.position(), in.remaining());
	}

	@Override
	public String toString() {
		return toCharBuffer().toString();
	}

}
