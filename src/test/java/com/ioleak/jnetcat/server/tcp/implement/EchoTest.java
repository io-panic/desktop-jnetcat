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
package com.ioleak.jnetcat.server.tcp.implement;

import java.io.IOException;
import java.net.Socket;

import com.ioleak.jnetcat.common.Logging;
import com.ioleak.jnetcat.common.utils.StringUtils;
import com.ioleak.jnetcat.common.utils.ThreadUtils;
import com.ioleak.jnetcat.formatter.PrettyHexStringOutput;
import com.ioleak.jnetcat.options.startup.ServerParametersTCP;
import com.ioleak.jnetcat.server.generic.ServerState;
import com.ioleak.jnetcat.server.tcp.TCPServer;
import com.ioleak.jnetcat.server.tcp.TCPServerType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EchoTest {

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
  public void outputExit_SocketClosed_NoClient() throws Exception {
    ThreadUtils.waitForThread(() -> tcpServer.getServerState() != ServerState.WAITING_FOR_CONNECTION);
    assertEquals(ServerState.WAITING_FOR_CONNECTION, tcpServer.getServerState());

    try {
      Socket client = new Socket("127.0.0.1", tcpServer.getLocalPort());
      client.getOutputStream().write(StringUtils.getBytesFromString("exit"));
    } catch (IOException ex) {
      Logging.getLogger().error("An error occured while opening/closing a client", ex);
    }
   
    Thread.sleep(500);
    assertEquals(0, tcpServer.getConnectedClientsNumber());
    assertEquals(ServerState.WAITING_FOR_CONNECTION, tcpServer.getServerState());
  }

  private TCPServer getTcpServer() {
    ServerParametersTCP serverParametersTCP = new ServerParametersTCP.ParametersBuilder(LISTEN_PORT).withServerType(TCPServerType.ECHO).build();
    return new TCPServer(serverParametersTCP);
  }
}
