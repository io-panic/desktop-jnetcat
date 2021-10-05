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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.ioleak.jnetcat.common.Logging;
import com.ioleak.jnetcat.common.interfaces.ProcessAction;
import com.ioleak.jnetcat.common.properties.ObjectProperty;
import com.ioleak.jnetcat.options.startup.ClientParametersUDP;

public class UDPClient
        implements ProcessAction, SocketClient {

  private DatagramSocket clientSocket;
  private final String ip;
  private final int port;
  private final int soTimeout;
  
  private final ObjectProperty<Boolean> connectedProperty = new ObjectProperty<>(false);

  public UDPClient(ClientParametersUDP clientParametersUDP) {
    this.ip = clientParametersUDP.getIp();
    this.port = clientParametersUDP.getPort();
    this.soTimeout = clientParametersUDP.getSoTimeout();
  }

  @Override
  public void start() {
    try {
      clientSocket = new DatagramSocket();
      clientSocket.setSoTimeout(soTimeout);

      Logging.getLogger().info(String.format("Openning connection UDP: %s", ip));

    } catch (SocketException ex) {
      Logging.getLogger().error("Socket error", ex);
    }
  }

  public void sendMessage(String message) {
    try {
      InetAddress address = InetAddress.getByName(ip);

      DatagramPacket request = new DatagramPacket(message.getBytes(), message.getBytes().length, address, port);
      clientSocket.send(request);

      Logging.getLogger().info(String.format("Sending [to: %s] message: %s", address.getHostAddress(), message));

    } catch (UnknownHostException ex) {
      Logging.getLogger().error("Unknown host", ex);
    } catch (IOException ex) {
      Logging.getLogger().error("Error on link", ex);
    }
  }

  public String readMessage() {
    byte[] buffer = new byte[512];
    DatagramPacket response = new DatagramPacket(buffer, buffer.length);

    try {
      clientSocket.receive(response);

    } catch (IOException ex) {
      Logging.getLogger().error("Error on link", ex);
    }

    return new String(buffer, 0, response.getLength());
  }

  @Override
  public boolean isRunning() {
    return true;
  }

  @Override
  public ObjectProperty<Boolean> connectedProperty() {
    return connectedProperty;
  }
  
  @Override
  public boolean stopActiveExecution() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean stopExecutions() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
