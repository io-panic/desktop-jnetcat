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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.ioleak.jnetcat.common.Logging;
import com.ioleak.jnetcat.common.interfaces.ProcessAction;
import com.ioleak.jnetcat.common.properties.ObjectProperty;
import com.ioleak.jnetcat.options.startup.ClientParametersTCP;

public class TCPClient
        implements ProcessAction {

  private Socket clientSocket;

  private final String ip;
  private final int port;
  private final ObjectProperty<Boolean> connectedProperty = new ObjectProperty<>(false);

  public TCPClient(ClientParametersTCP clientParametersTCP) {
    this(clientParametersTCP.getIp(), clientParametersTCP.getPort());
  }

  private TCPClient(String ip, int port) {
    this.ip = ip;
    this.port = port;
  }

  public TCPClient open() {
    try {
      if (connectedProperty().get()) {
        Logging.getLogger().warn(String.format("TCP connection already open on %s:%d", ip, port));
      } else {
        Logging.getLogger().info(String.format("Trying to open a TCP connection [%s:%s]", ip, port));
        clientSocket = new Socket(ip, port);
        Logging.getLogger().info(String.format("TCP connection established on %s:%d", ip, port));
      }
    } catch (IOException ex) {
      Logging.getLogger().error(String.format("Unable to open a TCP connection on %s:%d [%s]", ip, port, ex.getMessage()));
    } finally {
      updateConnectedProperty();
    }

    return this;
  }

  public void sendMessage(String msg) {
    if (!connectedProperty().get()) {
      throw new ClientNotConnectedException("Client must be connected");
    }

    try {
      clientSocket.sendUrgentData(1);

      PrintWriter printWriter = getOutputStream(clientSocket);
      printWriter.print(msg);
      printWriter.flush();

      if (printWriter.checkError()) {
        clientSocket.close();
        throw new ClientNotConnectedException("Unable to sendMessage: socket not connected");
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
      throw new ClientNotConnectedException("Client must be connected");
    }

    String readData = "";

    try {
      BufferedReader bufferedReader = getInputStream(clientSocket);
      readData = bufferedReader.readLine();

      if (readData == null) {
        clientSocket.close();
        throw new ClientNotConnectedException("Unable to read message: client not connected");
      }
    } catch (IOException ex) {
      Logging.getLogger().error("Unable to read message from socket", ex);
    } finally {
      updateConnectedProperty();
    }

    return readData;
  }

  public void close() {
    try {
      if (clientSocket != null) {
        clientSocket.close();
      }

      Logging.getLogger().info(String.format("TCP connection closed on %s:%d", ip, port));
    } catch (IOException ex) {
      Logging.getLogger().error(String.format("An error occurred while closing connection on %s:%d", ip, port), ex);
    } finally {
      updateConnectedProperty();
    }
  }

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

  private PrintWriter getOutputStream(Socket clientSocket) {
    PrintWriter printWriter;

    try {
      printWriter = new PrintWriter(clientSocket.getOutputStream(), false);
    } catch (IOException ex) {
      throw new ClientSendMessageException(ex.getMessage());
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
      throw new ClientReadMessageException(ex.getMessage());
    } finally {
      updateConnectedProperty();
    }

    return bufferedReader;
  }

  private void updateConnectedProperty() {
    connectedProperty.set(clientSocket != null && !clientSocket.isClosed() && clientSocket.isConnected());
  }
}
