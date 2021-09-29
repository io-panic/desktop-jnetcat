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
import com.ioleak.jnetcat.options.startup.ServerParametersTCP;
import com.ioleak.jnetcat.server.generic.Listener;

public class TCPServer
        extends Listener<TCPServerType, Socket> {

  private ServerSocket serverSocket;
  private boolean stopServerHitKey = false;

  public TCPServer(ServerParametersTCP serverParametersTCP) {
    this(serverParametersTCP.getServerType(), serverParametersTCP.getPort());
  }

  private TCPServer(TCPServerType serverType, int port) {
    super(serverType, port);
  }

  @Override
  public void startServer() {
    Logging.getLogger().info(String.format("Listening on port %d", getPort()));
    Logging.getLogger().info(String.format("Server act as a server: %s", getServerType().toString()));

    try {
      serverSocket = new ServerSocket(getPort());

      while (!stopServerHitKey && !Thread.currentThread().isInterrupted()) {

        try (Socket socket = serverSocket.accept()) {
          getConnectionClients().add(socket);
          Logging.getLogger().info(String.format("Connection received from %s", socket.getRemoteSocketAddress()));

          try {
            getServerType().getClient().startClient(socket);
          } catch (SocketException ex) {
            Logging.getLogger().info(String.format("Socket failure: %s", ex.getMessage()));
          }

          Logging.getLogger().info(String.format("Connection closed on client %s", socket.getRemoteSocketAddress()));
        } catch (IOException ex) {
          if (!stopServerHitKey) {
            Logging.getLogger().info("Client connection error", ex);
          }
        }
      }
    } catch (IOException ex) {
      if (!stopServerHitKey) {
        Logging.getLogger().info(String.format("Unable to start TCP listener"));
      }
    }

    Logging.getLogger().warn(String.format("Server closed on port %d", getPort()));
  }

  @Override
  public boolean stopActiveExecution() {
    boolean serverClosed = false;

    List<Socket> sockets = getConnectionClients();
    for (Socket socket : sockets) {
      if (socket.isConnected() && !socket.isClosed()) {
        Logging.getLogger().warn(String.format("Received a key to stop client connection (%s)", socket.getRemoteSocketAddress()));

        try {
          socket.close();
          serverClosed = true;

          Logging.getLogger().error("Client connection closed successfully");
        } catch (IOException ex) {
          Logging.getLogger().error("Unable to stop client connection");
        }
      }
    }

    if (!serverClosed) {
      Logging.getLogger().warn("No client is currently connected. Please wait for a connection to be established");
    }

    return serverClosed;
  }

  @Override
  public boolean stopExecutions() {
    boolean closed = false;
    stopServerHitKey = true;

    try {
      Logging.getLogger().info("Close server request received...");

      if (serverSocket != null && !serverSocket.isClosed()) {
        serverSocket.close();
        closed = true;
      }
    } catch (IOException ex) {
      Logging.getLogger().error("Unable to close", ex);
    }

    return closed;
  }
}
