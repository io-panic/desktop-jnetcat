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
package com.ioleak.jnetcat.client;

import com.ioleak.jnetcat.common.Logging;
import com.ioleak.jnetcat.options.startup.ClientParametersUDP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class UDPClient {

  private DatagramSocket clientSocket;
  private PrintWriter out;
  private BufferedReader in;

  private String ip;
  private int port;

  public UDPClient(ClientParametersUDP clientParametersUDP) {
    this(clientParametersUDP.getIp(), clientParametersUDP.getPort());
  }

  private UDPClient(String ip, int port) {
    this.ip = ip;
    this.port = port;
  }

  public UDPClient open() {
    try {
      InetAddress address = InetAddress.getByName(ip);
      clientSocket = new DatagramSocket();

      Logging.getLogger().info("Openning connection UDP");

      DatagramPacket request = new DatagramPacket(new byte[1], 1, address, port);
      clientSocket.send(request);

    } catch (SocketTimeoutException ex) {
      System.out.println("Timeout error: " + ex.getMessage());
      ex.printStackTrace();
    } catch (IOException ex) {
      System.out.println("Client error: " + ex.getMessage());
      ex.printStackTrace();
    }

    return this;
  }

  public void read() {
    byte[] buffer = new byte[512];
    DatagramPacket response = new DatagramPacket(buffer, buffer.length);

    try {
      clientSocket.receive(response);
    } catch (IOException ex) {
      System.out.println("Client error: " + ex.getMessage());
      ex.printStackTrace();
    }

    String quote = new String(buffer, 0, response.getLength());

    System.out.println(quote);
    System.out.println();
  }

  public static void main(String[] args) {
    UDPClient udpConnect = new UDPClient("djxmmx.net", 17);
    udpConnect.open();
    udpConnect.read();
  }
}
