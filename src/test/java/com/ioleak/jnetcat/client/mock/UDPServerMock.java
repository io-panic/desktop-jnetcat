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
package com.ioleak.jnetcat.client.mock;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.ioleak.jnetcat.common.Logging;

public class UDPServerMock
        implements Runnable {

  public static final String RESPONSE_MOCK = "RESPONSE_MOCK";

  private DatagramSocket serverSocket;

  public UDPServerMock() {
    try {
      serverSocket = new DatagramSocket(0);
    } catch (IOException ex) {
      Logging.getLogger().error("Unable to start UDP server", ex);
    }
  }

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      DatagramPacket request = new DatagramPacket(new byte[1], 1);
      try {
        serverSocket.receive(request);

        String quote = RESPONSE_MOCK;
        byte[] buffer = quote.getBytes();

        InetAddress clientAddress = request.getAddress();
        int clientPort = request.getPort();

        DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);

        serverSocket.send(response);
      } catch (IOException ex) {
        Logging.getLogger().error("Unable to receive data on server", ex);
      }
    }
  }

  public int getPort() {
    return serverSocket.getLocalPort();
  }

  public void closeServer() {
    if (serverSocket != null && !serverSocket.isClosed()) {
      serverSocket.close();
    }
  }
}
