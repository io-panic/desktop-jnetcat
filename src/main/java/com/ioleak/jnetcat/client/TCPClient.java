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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

import com.ioleak.jnetcat.client.exception.ClientReadMessageException;
import com.ioleak.jnetcat.client.exception.ClientSendMessageException;
import com.ioleak.jnetcat.common.Logging;
import com.ioleak.jnetcat.common.interfaces.ProcessAction;
import com.ioleak.jnetcat.common.properties.ObjectProperty;
import com.ioleak.jnetcat.common.properties.Observable;
import com.ioleak.jnetcat.options.startup.ClientParametersTCP;

public class TCPClient
        implements ProcessAction, SocketClient {

  private static final String EXCEPTION_CLIENT_NOT_CONNECTED = "Client is not connected to a server: %s";

  private Observable keyListener;
  private boolean interactive;

  private Socket clientSocket;
  private final String ip;
  private final int port;
  private final int soTimeout;

  private final ObjectProperty<Boolean> connectedProperty = new ObjectProperty<>(false);

  public TCPClient(ClientParametersTCP clientParametersTCP) {
    this.ip = clientParametersTCP.getIp();
    this.port = clientParametersTCP.getPort();
    this.soTimeout = clientParametersTCP.getSoTimeout();
    this.interactive = clientParametersTCP.isInteractive();
  }

  @Override
  public void start() {
    try {
      if (connectedProperty().get()) {
        Logging.getLogger().warn(String.format("TCP connection already open on %s:%d", ip, port));
      } else {
        Logging.getLogger().info(String.format("Trying to open a TCP connection [%s:%s]", ip, port));

        clientSocket = new Socket();
        SocketAddress socketAddress = new InetSocketAddress(ip, port);
        clientSocket.connect(socketAddress, soTimeout);
        clientSocket.setSoTimeout(soTimeout);

        Logging.getLogger().info(String.format("TCP connection established on %s:%d", ip, port));
      }
    } catch (IOException ex) {
      Logging.getLogger().error(String.format("Unable to open a TCP connection on %s:%d [%s]", ip, port, ex.getMessage()));
    } finally {
      updateConnectedProperty();
    }
    
    if (interactive && connectedProperty.get()) {
      try {
        clientSocket.setSoTimeout(0);
        while (!Thread.currentThread().isInterrupted()) {
          readMessage();
        }
      } catch (SocketException ex) {
        Logging.getLogger().error("Unable to reset soTimeout (indefinite) for interactive mode", ex);
      }
    }
  }

  public void sendMessage(String msg) {
    if (!connectedProperty().get()) {
      throw new ClientSendMessageException(EXCEPTION_CLIENT_NOT_CONNECTED);
    }

    try {
      clientSocket.sendUrgentData(1);

      PrintWriter printWriter = getOutputStream(clientSocket);
      printWriter.print(msg);
      printWriter.flush();

      if (printWriter.checkError()) {
        clientSocket.close();
        throw new ClientSendMessageException(EXCEPTION_CLIENT_NOT_CONNECTED);
      }
    } catch (IOException ex) {
      Logging.getLogger().error(String.format("sendMessage error: ", ex.getMessage()));
      if (clientSocket != null) {
        try {
          clientSocket.close();
        } catch (IOException e) {
          Logging.getLogger().error("Unable to close client socket", e);
        }
      }
    } finally {
      updateConnectedProperty();
    }
  }

  public String readMessage() {
    if (!connectedProperty().get()) {
      throw new ClientReadMessageException(EXCEPTION_CLIENT_NOT_CONNECTED);
    }

    String readData = "";

    try {
      BufferedReader bufferedReader = getInputStream(clientSocket);
      readData = bufferedReader.readLine();

      if (readData == null) {
        clientSocket.close();
        throw new ClientReadMessageException(EXCEPTION_CLIENT_NOT_CONNECTED);
      }
    } catch (IOException ex) {
      Logging.getLogger().error("Unable to read message from socket", ex);
    } finally {
      updateConnectedProperty();
    }

    return readData;
  }

  @Override
  public void setKeyListener(Observable keyListener) {
    this.keyListener = keyListener;

    if (interactive && this.keyListener != null) {
      StringBuilder builder = new StringBuilder();
      
      keyListener.addListener((PropertyChangeEvent evt) -> {
        builder.append(evt.getNewValue());
        if (evt.getNewValue().equals('\n')) {
          String message = builder.toString();
          Logging.getLogger().info(String.format("Sending to server: %s", message.replace("\n", "")));
          
          sendMessage(builder.toString());
          builder.setLength(0);
        }
      });
    }
  }

  @Override
  public boolean isStateSuccessful() {
    return connectedProperty().get();
  }

  public ObjectProperty<Boolean> connectedProperty() {
    return connectedProperty;
  }

  @Override
  public boolean stopActiveExecution() {
    boolean closed = false;

    try {
      if (clientSocket != null) {
        clientSocket.close();
        closed = true;
      }

      Logging.getLogger().info(String.format("TCP connection closed on %s:%d", ip, port));
    } catch (IOException ex) {
      Logging.getLogger().error(String.format("An error occurred while closing connection on %s:%d", ip, port), ex);
    } finally {
      updateConnectedProperty();
    }

    return closed;
  }

  @Override
  public boolean stopExecutions() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  private PrintWriter getOutputStream(Socket clientSocket) {
    PrintWriter printWriter;

    try {
      printWriter = new PrintWriter(clientSocket.getOutputStream(), false);
    } catch (IOException ex) {
      throw new ClientSendMessageException("An error occured on the OutputStream", ex);
    } finally {
      updateConnectedProperty();
    }

    return printWriter;
  }

  private BufferedReader getInputStream(Socket clientSocket) {
    BufferedReader bufferedReader;

    try {
      bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    } catch (IOException ex) {
      throw new ClientReadMessageException("An error occured on the InputStream", ex);
    } finally {
      updateConnectedProperty();
    }

    return bufferedReader;
  }

  private void updateConnectedProperty() {
    connectedProperty.set(clientSocket != null && !clientSocket.isClosed() && clientSocket.isConnected());
  }
}
