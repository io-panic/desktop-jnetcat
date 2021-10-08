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

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

import com.ioleak.jnetcat.common.Logging;
import com.ioleak.jnetcat.common.properties.Observable;
import com.ioleak.jnetcat.options.startup.ServerParametersUDP;
import com.ioleak.jnetcat.server.generic.Listener;
import com.ioleak.jnetcat.server.generic.ServerState;
import com.ioleak.jnetcat.server.udp.exception.UDPServerUnitializatedStartException;

public class UDPServer
        extends Listener<UDPServerType, DatagramSocket> {

  private Observable keyListener;
  private DatagramSocket serverSocket;
  private ServerState serverState = ServerState.NOT_STARTED;

  public UDPServer(ServerParametersUDP serverParametersUDP) {
    this(serverParametersUDP.getServerType(), serverParametersUDP.getPort());
  }

  private UDPServer(UDPServerType serverType, int port) {
    super(serverType, port);
  }

  @Override
  public void start() {
    serverState = ServerState.STARTING;
    Logging.getLogger().info(String.format("Server act as a server (UDP): %s", getServerType().toString()));

    try {
      serverSocket = new DatagramSocket(getPort());
      Logging.getLogger().info(String.format("Listening on port %d", getLocalPort()));

      while (!(serverSocket.isClosed() || Thread.currentThread().isInterrupted())) {
        serverState = ServerState.WAITING_FOR_CONNECTION;
        try {
          getServerType().getClient().startClient(serverSocket);
          serverState = ServerState.CLIENT_CONNECTED;
        } catch (IOException ex) {
          Logging.getLogger().error(String.format("Unable to start a new UDP client (port: %d)", getLocalPort()), ex);
        }

      }
    } catch (SocketException ex) {
      Logging.getLogger().error(String.format("Unable to start socket on port %d", getLocalPort()), ex);
    }

    serverState = ServerState.CLOSED;
    Logging.getLogger().warn(String.format("Server closed on defined port %d", getLocalPort()));
  }

  @Override
  public boolean isStateSuccessful() {
    return (serverState == ServerState.CLOSED);
  }

  @Override
  public boolean stopActiveExecution() {
    boolean serverClosed = false;

    List<DatagramSocket> serverSockets = getConnectionClients();
    for (DatagramSocket serverSocket : serverSockets) {
      if (serverSocket.isConnected() && !serverSocket.isClosed()) {
        Logging.getLogger().warn(String.format("Received a key to stop client connection (%s)", serverSocket.getRemoteSocketAddress()));

        serverSocket.close();
        serverClosed = true;

        Logging.getLogger().error("Client connection closed successfully");
      }
    }

    if (!serverClosed) {
      Logging.getLogger().warn("No client is currently connected. Please wait for a connection to be established");
    }

    return serverClosed;
  }

  public void setKeyListener(Observable keyListener) {
    this.keyListener = keyListener;

    // TODO: ...
    //if (this.keyListener != null) {
    //  keyListener.addListener((PropertyChangeEvent evt) -> {
    //    System.out.println("key hit!! " + evt.getNewValue());
    //  });
  }

  @Override
  public boolean stopExecutions() {
    boolean closed = false;

    System.out.println("Stop server request");
    if (serverSocket != null) {
      serverSocket.close();
      closed = true;
    }

    return closed;
  }

  public ServerState getServerState() {
    return serverState;
  }

  public int getLocalPort() {
    if (serverSocket == null) {
      throw new UDPServerUnitializatedStartException("Server is not started");
    }

    return serverSocket.isClosed() ? getPort() : serverSocket.getLocalPort();
  }
}
