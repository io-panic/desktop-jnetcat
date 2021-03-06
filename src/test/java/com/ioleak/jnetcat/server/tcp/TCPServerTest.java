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
import java.net.Socket;

import com.ioleak.jnetcat.common.Logging;
import com.ioleak.jnetcat.common.utils.ThreadUtils;
import com.ioleak.jnetcat.formatter.PrettyHexStringOutput;
import com.ioleak.jnetcat.options.startup.ServerParametersTCP;
import com.ioleak.jnetcat.server.generic.ServerState;
import com.ioleak.jnetcat.server.tcp.exception.TCPServerUnitializatedStartException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TCPServerTest {

  private Thread tcpServerThread;
  private TCPServer tcpServer;
  private static final int LISTEN_PORT = 0;

  @BeforeEach
  public void setUp() {
    tcpServer = getTcpServer();
    tcpServer.setFormatOutput(new PrettyHexStringOutput(null, 20)); // TODO change for a more basic output
    
    tcpServerThread = new Thread(tcpServer::start);
    tcpServerThread.start();
  }

  @AfterEach
  public void tearDown() {
    tcpServerThread.interrupt();
    tcpServer.stopExecutions();

    try {
      tcpServerThread.join();
    } catch (InterruptedException ex) {
      Logging.getLogger().error("InterruptedException", ex);
    }
  }

  @Test
  public void getLocalPort_ServerNotStarted_ExceptionThrown() {
    TCPServer tcpTmpServer = getTcpServer();

    assertEquals(ServerState.NOT_STARTED, tcpTmpServer.getServerState());
    assertThrows(TCPServerUnitializatedStartException.class, () -> tcpTmpServer.getLocalPort());
  }

  @Test
  public void startServer_ClientConnection_IsConnected() {
    boolean isConnected = false;

    try {
      ThreadUtils.waitForThread(() -> tcpServer.getServerState() != ServerState.WAITING_FOR_CONNECTION);
      assertEquals(ServerState.WAITING_FOR_CONNECTION, tcpServer.getServerState());

      Socket client = new Socket("127.0.0.1", tcpServer.getLocalPort());

      ThreadUtils.waitForThread(() -> tcpServer.getServerState() != ServerState.WAITING_FOR_CONNECTION);
      assertTrue(tcpServer.getServerState().equals(ServerState.CLIENT_CONNECTED)
                 || tcpServer.getServerState().equals(ServerState.WAITING_FOR_CONNECTION));

      isConnected = !client.isClosed() && client.isConnected();
      client.close();
    } catch (IOException ex) {
      Logging.getLogger().error("An error occured", ex);
    }

    assertTrue(isConnected);
    assertEquals(1, tcpServer.getConnectedClientsNumber());
    assertEquals(ServerState.CLIENT_CONNECTED, tcpServer.getServerState());
  }

  @Test
  public void stopActiveExecution_ServerState_WaitingNoConnectionLeft() {
    boolean isConnected = true;

    Logging.getLogger().info(new Throwable().getStackTrace()[0].getMethodName());

    ThreadUtils.waitForThread(() -> tcpServer.getServerState() != ServerState.WAITING_FOR_CONNECTION);
    assertEquals(ServerState.WAITING_FOR_CONNECTION, tcpServer.getServerState());

    try {

      Socket client = new Socket("127.0.0.1", tcpServer.getLocalPort());
      isConnected = !client.isClosed() && client.isConnected();
      assertTrue(isConnected);

      ThreadUtils.waitForThread(() -> tcpServer.getServerState() != ServerState.WAITING_FOR_CONNECTION);
      tcpServer.stopActiveExecution();
      ThreadUtils.waitForThread(() -> tcpServer.getServerState() != ServerState.CLOSED);
      
      client.sendUrgentData(1);
      isConnected = client.getInputStream().read() != -1;
      client.close();

    } catch (IOException ex) {
      Logging.getLogger().error("An error occured", ex);
      isConnected = false;
    }

    assertFalse(isConnected);
    assertEquals(0, tcpServer.getConnectedClientsNumber());
    assertEquals(ServerState.WAITING_FOR_CONNECTION, tcpServer.getServerState());
  }

  @Test
  public void stopExecutions_ServerState_ClosedNoConnectionLeft() {
    Socket client;

    ThreadUtils.waitForThread(() -> tcpServer.getServerState() != ServerState.WAITING_FOR_CONNECTION);

    try {
      client = new Socket("127.0.0.1", tcpServer.getLocalPort());
      //client.close();
    } catch (IOException ex) {
      Logging.getLogger().error("An error occured while opening/closing a client", ex);
    }

    ThreadUtils.waitForThread(() -> tcpServer.getServerState() != ServerState.WAITING_FOR_CONNECTION);
    assertEquals(1, tcpServer.getConnectedClientsNumber());

    tcpServer.stopExecutions();
    ThreadUtils.waitForThread(() -> tcpServer.getServerState() != ServerState.CLOSED);

    assertEquals(0, tcpServer.getConnectedClientsNumber());
    assertEquals(ServerState.CLOSED, tcpServer.getServerState());
  }
  
  private TCPServer getTcpServer() {
    ServerParametersTCP serverParametersTCP = new ServerParametersTCP.ParametersBuilder(LISTEN_PORT).withServerType(TCPServerType.ECHO).build();
    return new TCPServer(serverParametersTCP);
  }
}
