/*
 * Copyright (c) 2021, crashdump (<xxxx>@ioleak.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.ioleak.jnetcat.formatter.helpers;

import java.io.ByteArrayInputStream;

import com.ioleak.jnetcat.common.utils.StringUtils;
import com.ioleak.jnetcat.formatter.exception.StreamNoDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StreamRawReaderNoOutputTest {
  
  private StreamRawReaderNoOutput streamRawReaderNotOutput;
  
  @BeforeEach
  public void setUp() {
    streamRawReaderNotOutput = new StreamRawReaderNoOutputImpl();
  }

  @Test
  public void startReading_SimpleShortString_VerifyEndOfStreamData() throws Exception {
    streamRawReaderNotOutput.startReading(new ByteArrayInputStream("MAH\n OK.#éàZz1-! J$k.".getBytes()));
    String iso8859 = streamRawReaderNotOutput.getEndOfStreamData();
    assertEquals("MAH\n OK.#éàZz1-! J$k.",  StringUtils.getStringIso8859ToUtf8(iso8859));
  }

  @Test
  public void startReading_EmptyString_ExceptionThrown() throws Exception {
    assertThrows(StreamNoDataException.class, () -> streamRawReaderNotOutput.startReading(new ByteArrayInputStream("".getBytes())));
  }

  @Test
  public void startReading_ExceedDefaultSpaceString_VerifyEndOfStreamData() throws Exception {
    String randomData = StringUtils.generateRandomString(5046);
    streamRawReaderNotOutput.startReading(new ByteArrayInputStream(randomData.getBytes()));
    assertEquals(randomData, streamRawReaderNotOutput.getEndOfStreamData());
  }
  
  @Test
  public void testGetLineWidth() {
    assertEquals(-1, streamRawReaderNotOutput.getLineWidth());
  }

  public class StreamRawReaderNoOutputImpl
          extends StreamRawReaderNoOutput {
    // base class tests
  }
}
