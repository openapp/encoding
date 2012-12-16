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
package org.openapplication.encoding.test;

import static org.junit.Assert.*;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openapplication.encoding.Binary;
import org.openapplication.encoding.Id;


public class IdTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAsCharsAsBytes() {
		byte[] hello = "你好hi".getBytes(Charset.forName("UTF-8"));

		char[] chars = Binary.asCharArray(hello);
		// for (char c : chars)
		// System.out.print(c);
		// System.out.println();

		byte[] bytes = Binary.asByteArray(new String(chars));
		// for (byte b : bytes)
		// System.out.print((char) (b & 0xFF));
		// System.out.println();
		// for (byte b : bytes)
		// System.out.print((b & 0xFF) + " ");
		// System.out.println();
		// for (byte b : hello)
		// System.out.print((b & 0xFF) + " ");
		// System.out.println();

		assertArrayEquals(hello, bytes);
	}

	@Test
	public void testAsBytesAsUuid() {
		UUID uuid = UUID.fromString("03d73148-e422-4c57-a25b-bd4be247ef33");

		byte[] bytes = Id.asByteArray(uuid);
		assertArrayEquals(new byte[] { 0x03, (byte) 0xd7, 0x31, 0x48,//
				(byte) 0xe4, 0x22,//
				0x4c, 0x57,//
				(byte) 0xa2, 0x5b,//
				(byte) 0xbd, 0x4b, (byte) 0xe2, 0x47, (byte) 0xef, 0x33 //
				}, bytes);

		UUID uuid2 = Id.asUuid(bytes);
		assertEquals(uuid, uuid2);
	}

	@Test
	public void testAsUuidNamespaceName() {
		UUID urlNs = UUID.fromString("6ba7b811-9dad-11d1-80b4-00c04fd430c8");
		final String url = "http://www.example.com/";
		UUID actual = UUID.fromString("fcde3c85-2270-590f-9e7c-ee003d65e0e2");

		Id.Name name = new Id.Name() {
			@Override
			public void update(MessageDigest digest) {
				digest.update(url.getBytes(Charset.forName("UTF-8")));
			}
		};

		UUID uuid = Id.asUuid(urlNs, name);

		assertEquals(actual, uuid);
	}

	@Test
	public void testEncodeDecode() {
		String hello = "α你好hi🁥";

		String encoded = Id.encode(hello).toString();
		assertEquals(hello, encoded);

		String decoded = Id.decode("%CE%B1%E4%BD%A0%E5%A5%BDhi%F0%9F%81%A5");
		assertEquals(hello, decoded);

	}

}
