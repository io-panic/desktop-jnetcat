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
package com.ioleak.jnetcat.server.udp.implement;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.ioleak.jnetcat.common.Logging;
import com.ioleak.jnetcat.common.utils.StringUtils;
import com.ioleak.jnetcat.server.udp.UDPClientConnection;

public class Basic
        implements UDPClientConnection {

  public static final int MAX_PACKET_LENGTH = 2048;

  @Override
  public void startClient(DatagramSocket socket) throws IOException {
    byte[] buffer = new byte[MAX_PACKET_LENGTH];
    DatagramPacket request = new DatagramPacket(buffer, buffer.length);

    while (!Thread.currentThread().isInterrupted()) {
      socket.receive(request);

      String msg = getReceivedString(request);

      Logging.getLogger().info("UDP Received data : ");
      System.out.println(StringUtils.toHexWithSpaceSeparator(msg));

      for (int i = 0; i < buffer.length; i++) {
        buffer[i] = 0;
      }
      request.setLength(buffer.length);
    }

  }

  private String getReceivedString(DatagramPacket request) {
    byte[] receivedData = request.getData();
    byte[] trimmedData = new byte[request.getLength()];
    System.arraycopy(receivedData, 0, trimmedData, 0, trimmedData.length);

    return new String(trimmedData);
  }
}
