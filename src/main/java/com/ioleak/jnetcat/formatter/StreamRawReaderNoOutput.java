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
package com.ioleak.jnetcat.formatter;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.ioleak.jnetcat.common.Logging;
import com.ioleak.jnetcat.common.properties.ObjectProperty;
import com.ioleak.jnetcat.common.utils.StringUtils;
import com.ioleak.jnetcat.formatter.exception.StreamNoDataException;

public abstract class StreamRawReaderNoOutput
        implements StreamFormatOutput {

  private final ObjectProperty<Byte> dataReceived = new ObjectProperty<>();
  private final List<Byte> bufferDataReceived = new ArrayList<>();

  private StreamFormatOutput prettyFormatOutput = null;

  public StreamRawReaderNoOutput() {
    this(null);
  }

  public StreamRawReaderNoOutput(StreamFormatOutput prettyFormatOutput) {
    this.prettyFormatOutput = prettyFormatOutput;
    dataReceived.addListener(this::formatDataOutput);
  }

  @Override
  public void formatDataOutput(PropertyChangeEvent evt) {
    if (prettyFormatOutput != null) {
      prettyFormatOutput.formatDataOutput(evt);
    }
  }

  @Override
  public String getEndOfStreamData() {
    return StringUtils.getStringFromBytes(bufferDataReceived);
  }

  @Override
  public final void startReading(InputStream inputStream) throws IOException {
    int result = 0;
    boolean endOfStream = false;

    bufferDataReceived.clear();

    do {
      try {
        byte[] inputData = new byte[1024];
        readByte(blockOnReadDetectConnectionLost(inputStream));

        result = inputStream.read(inputData, 0, inputStream.available());
        int[] readBytes = IntStream.range(0, result).map(i -> inputData[i]).toArray();

        for (int aByte : readBytes) {
          readByte((byte) aByte);
        }
        
        endOfStream = (result == -1) || (inputStream.available() <= 0);

      } catch (SocketTimeoutException ex) {
        endOfStream = true;
      }
    } while (!endOfStream);

    getEndOfStreamData();

    try {
      Thread.sleep(100);
    } catch (InterruptedException ex) {
      Logging.getLogger().warn("Thread interrupted!", ex);
    }
  }

  private byte blockOnReadDetectConnectionLost(InputStream inputStream)
          throws IOException {
    int oneByte = inputStream.read();
    if (oneByte == -1) {
      throw new StreamNoDataException("Unable to read more data");
    }

    return (byte) oneByte;
  }

  private void readByte(byte oneByte) {
    bufferDataReceived.add(oneByte);
    dataReceived.set(oneByte);
  }
}
