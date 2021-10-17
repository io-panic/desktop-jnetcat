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
package com.ioleak.jnetcat.server.udp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.ioleak.jnetcat.formatter.StreamFormatOutput;

import static com.ioleak.jnetcat.server.udp.implement.Quote.MAX_PACKET_LENGTH;

public abstract class UDPClientConnection {

  private StreamFormatOutput streamFormatOutput;

  public abstract void dataRead(String readData);

  public abstract void dataSend(DatagramSocket socket, DatagramPacket request) throws IOException;

  public final void startClient(DatagramSocket clientSocket, StreamFormatOutput streamFormatOutput)
          throws IOException {
    byte[] buffer = new byte[MAX_PACKET_LENGTH];
    DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

    this.streamFormatOutput = streamFormatOutput;

    while (!Thread.currentThread().isInterrupted()) {
      clientSocket.receive(receivedPacket);
      streamFormatOutput.startReading(new ByteArrayInputStream(buffer));
      String readData = streamFormatOutput.getEndOfStreamData();

      if (!readData.isBlank()) {
        dataRead(readData);
        dataSend(clientSocket, receivedPacket);
      }
    }
  }
}
