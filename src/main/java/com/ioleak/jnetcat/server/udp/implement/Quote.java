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
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import com.ioleak.jnetcat.server.udp.UDPClientConnection;

public class Quote
        implements UDPClientConnection {

  @Override
  public void startClient(DatagramSocket socket) throws IOException {
    try {

      while (true) {
        listenClient(socket);
      }

    } catch (SocketTimeoutException ex) {
      System.out.println("Timeout error: " + ex.getMessage());
      ex.printStackTrace();
    } catch (IOException ex) {
      System.out.println("Client error: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  private void listenClient(DatagramSocket socket)
          throws IOException {

    byte[] buffer = new byte[256];

    DatagramPacket request = new DatagramPacket(buffer, buffer.length);
    socket.receive(request);

    System.out.println("UDP Received data : ");
    for (byte data : buffer) {
      System.out.print(String.format("%02X ", data));
    }
  }

  private void sendToClient(DatagramSocket socket, DatagramPacket request, byte[] data)
          throws IOException {

    InetAddress clientAddress = request.getAddress();
    int clientPort = request.getPort();

    DatagramPacket response = new DatagramPacket(data, data.length, clientAddress, clientPort);
    socket.send(response);
  }
}
