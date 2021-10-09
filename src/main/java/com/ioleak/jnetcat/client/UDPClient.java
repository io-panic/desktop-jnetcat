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

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.ioleak.jnetcat.client.exception.ClientReadMessageException;
import com.ioleak.jnetcat.client.exception.ClientSendMessageException;
import com.ioleak.jnetcat.common.Logging;
import com.ioleak.jnetcat.common.interfaces.ProcessAction;
import com.ioleak.jnetcat.common.properties.ObjectProperty;
import com.ioleak.jnetcat.common.properties.Observable;
import com.ioleak.jnetcat.options.startup.ClientParametersUDP;

public class UDPClient
        implements ProcessAction, SocketClient {

  private static final String SOCKET_NOT_INITIALIZATED = "Not correctly initializated: socket is null";

  private Observable keyListener;
  private boolean interactive;

  private DatagramSocket clientSocket;
  private final String ip;
  private final int port;
  private final int soTimeout;

  private final ObjectProperty<Boolean> connectedProperty = new ObjectProperty<>(false);

  public UDPClient(ClientParametersUDP clientParametersUDP) {
    this.ip = clientParametersUDP.getIp();
    this.port = clientParametersUDP.getPort();
    this.soTimeout = clientParametersUDP.getSoTimeout();
    this.interactive = clientParametersUDP.isInteractive();
  }

  @Override
  public void start() {
    try {
      clientSocket = new DatagramSocket();
      clientSocket.setSoTimeout(soTimeout);

      Logging.getLogger().info(String.format("Openning UDP connection: %s:%d", ip, port));

    } catch (SocketException ex) {
      Logging.getLogger().error("Socket error", ex);
    }

    if (interactive) {
      try {
        clientSocket.setSoTimeout(0);
        while (!Thread.currentThread().isInterrupted()) {
          String response = readMessage();
          System.out.print(response);
          //System.out.println(StringUtils.toStringWithLineSeparator(StringUtils.toHexWithSpaceSeparator(response)));
        }
      } catch (SocketException ex) {
        Logging.getLogger().error("Unable to reset soTimeout (indefinite) for interactive mode", ex);
      }
    } else {
      sendMessage("");
      System.out.println(readMessage());
    }
  }

  @Override
  public void sendMessage(String message) {
    checkClientInitializatedOnSend();

    try {
      InetAddress address = InetAddress.getByName(ip);

      DatagramPacket request = new DatagramPacket(message.getBytes(), message.getBytes().length, address, port);
      clientSocket.send(request);

      Logging.getLogger().info(String.format("Sending [to: %s] message: %s", address.getHostAddress(), message.replace("\n", "")));

    } catch (UnknownHostException ex) {
      throw new ClientSendMessageException("Unknown host", ex);
    } catch (IOException ex) {
      throw new ClientSendMessageException("An error occured", ex);
    }
  }

  public String readMessage() {
    byte[] buffer = new byte[512];
    DatagramPacket response = new DatagramPacket(buffer, buffer.length);

    checkClientInitializatedOnRead();

    try {
      clientSocket.receive(response);
    } catch (IOException ex) {
      throw new ClientReadMessageException("An error occured", ex);
    }

    return new String(buffer, 0, response.getLength());
  }

  @Override
  public void setKeyListener(Observable keyListener) {
    this.keyListener = keyListener;

    if (interactive && this.keyListener != null) {
      StringBuilder builder = new StringBuilder();

      keyListener.addListener((PropertyChangeEvent evt) -> {
        builder.append(evt.getNewValue());
        if (evt.getNewValue().equals('\n')) {
          sendMessage(builder.toString());
          builder.setLength(0);
        }
      });
    }
  }

  @Override
  public boolean isStateSuccessful() {
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

  private void checkClientInitializatedOnRead() {
    if (clientSocket == null) {
      throw new ClientReadMessageException(SOCKET_NOT_INITIALIZATED);
    }
  }

  private void checkClientInitializatedOnSend() {
    if (clientSocket == null) {
      throw new ClientSendMessageException(SOCKET_NOT_INITIALIZATED);
    }
  }
}
