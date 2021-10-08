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
package com.ioleak.jnetcat.server.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

import com.ioleak.jnetcat.common.Logging;
import com.ioleak.jnetcat.common.properties.Observable;
import com.ioleak.jnetcat.options.startup.ServerParametersTCP;
import com.ioleak.jnetcat.server.generic.Listener;
import com.ioleak.jnetcat.server.generic.ServerState;
import com.ioleak.jnetcat.server.tcp.exception.TCPServerUnitializatedStartException;

public class TCPServer
        extends Listener<TCPServerType, Socket> {

  private ServerSocket serverSocket;
  private ServerState serverState = ServerState.NOT_STARTED;

  private Observable keyListener;

  public TCPServer(ServerParametersTCP serverParametersTCP) {
    this(serverParametersTCP.getServerType(), serverParametersTCP.getPort());
  }

  private TCPServer(TCPServerType serverType, int port) {
    super(serverType, port);
  }

  @Override
  public void setKeyListener(Observable keyListener) {
    this.keyListener = keyListener;

    // TODO
    //if (this.keyListener != null) {
    //  keyListener.addListener((PropertyChangeEvent evt) -> {
    //    System.out.println("key hit!! " + evt.getNewValue());
    //  });
    //}
  }

  @Override
  public void start() {
    serverState = ServerState.STARTING;
    Logging.getLogger().info(String.format("Server act as a server (TCP): %s", getServerType().toString()));

    try {
      serverSocket = new ServerSocket(getPort());
      Logging.getLogger().info(String.format("Listening on port %d", getLocalPort()));

      while (!(serverSocket.isClosed() || Thread.currentThread().isInterrupted())) {

        serverState = ServerState.WAITING_FOR_CONNECTION;
        try (Socket socket = serverSocket.accept()) {
          serverState = ServerState.CLIENT_CONNECTED;
          getConnectionClients().add(socket);
          Logging.getLogger().info(String.format("Connection received from %s", socket.getRemoteSocketAddress()));

          try {
            getServerType().getClient().startClient(socket);
          } catch (SocketException ex) {
            Logging.getLogger().info(String.format("Socket failure: %s", ex.getMessage()));
          }
          Logging.getLogger().info(String.format("Connection closed on client %s", socket.getRemoteSocketAddress()));
        } catch (IOException ex) {
          if (!serverSocket.isClosed()) {
            Logging.getLogger().info("Client connection error", ex);
          }
        }
      }
    } catch (IOException ex) {
      if (!serverSocket.isClosed()) {
        Logging.getLogger().info(String.format("Unable to start TCP listener"));
      }
    }

    serverState = ServerState.CLOSED;
    Logging.getLogger().warn(String.format("Server closed on defined port %d", getPort()));
  }

  @Override
  public boolean isStateSuccessful() {
    return (serverState == ServerState.CLOSED);
  }

  @Override
  public boolean stopActiveExecution() {
    boolean clentClosed = false;

    List<Socket> copyClients = List.copyOf(getConnectionClients());
    for (Socket socket : copyClients) {
      if (socket.isConnected() && !socket.isClosed()) {
        Logging.getLogger().warn(String.format("Received a key to stop client connection (%s)", socket.getRemoteSocketAddress()));

        try {
          socket.close();
          getConnectionClients().remove(socket);
          clentClosed = true;

          Logging.getLogger().error("Client connection closed successfully");
        } catch (IOException ex) {
          Logging.getLogger().error("Unable to stop client connection");
        }
      }
    }

    if (!clentClosed) {
      Logging.getLogger().warn("No client is currently connected. Please wait for a connection to be established");
    }

    return clentClosed;
  }

  @Override
  public boolean stopExecutions() {
    boolean closed = false;

    try {
      Logging.getLogger().info("Close server request received...");

      List<Socket> copyClients = List.copyOf(getConnectionClients());
      for (Socket client : copyClients) {
        if (client.isConnected() && !client.isClosed()) {
          client.close();
        }

        getConnectionClients().remove(client);
      }

      if (serverSocket != null && !serverSocket.isClosed()) {
        serverSocket.close();
        closed = true;
      }
    } catch (IOException ex) {
      Logging.getLogger().error("Unable to close", ex);
    }

    return closed;
  }

  public ServerState getServerState() {
    return serverState;
  }

  public int getLocalPort() {
    if (serverSocket == null || serverSocket.isClosed()) {
      throw new TCPServerUnitializatedStartException("Server is not started");
    }

    return serverSocket.getLocalPort();
  }

  public int getConnectedClientsNumber() {
    return getConnectionClients().size();
  }
}
