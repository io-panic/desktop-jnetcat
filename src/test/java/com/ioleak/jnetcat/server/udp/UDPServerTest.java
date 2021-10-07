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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.ioleak.jnetcat.common.Logging;
import com.ioleak.jnetcat.common.utils.ThreadUtils;
import com.ioleak.jnetcat.options.startup.ServerParametersUDP;
import com.ioleak.jnetcat.server.generic.ServerState;
import com.ioleak.jnetcat.server.udp.exception.UDPServerUnitializatedStartException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class UDPServerTest {
  private Thread udpServerThread;
  private UDPServer udpServer;
  private static final int LISTEN_PORT = 0;

  @BeforeEach
  public void setUp() {
    udpServer = getUdpServer();
    udpServerThread = new Thread(udpServer::start);
    udpServerThread.start();
  }

  @AfterEach
  public void tearDown() {
    if (udpServer != null) {
      udpServer.stopExecutions();  
    }
    
    udpServerThread.interrupt();

    try {
      udpServerThread.join();
    } catch (InterruptedException ex) {
      Logging.getLogger().error("InterruptedException", ex);
    }
  }
  
  @Test
  public void getLocalPort_ServerNotStarted_ExceptionThrown() {
    UDPServer udpTmpServer = getUdpServer();

    assertEquals(ServerState.NOT_STARTED, udpTmpServer.getServerState());
    assertThrows(UDPServerUnitializatedStartException.class, () -> udpTmpServer.getLocalPort());
  }
  
  @Test
  public void startServer_ClientConnection_IsConnected() {
    boolean isConnected = false;

    try {
      ThreadUtils.waitForThread(() -> udpServer.getServerState() != ServerState.WAITING_FOR_CONNECTION);
      assertEquals(ServerState.WAITING_FOR_CONNECTION, udpServer.getServerState());

      String sendData = "Test UDP DATA";
      DatagramPacket udpPacket = new DatagramPacket(sendData.getBytes(), sendData.getBytes().length, 
              InetAddress.getByName("127.0.0.1"), udpServer.getLocalPort());
            
      DatagramSocket udpClient = new DatagramSocket(0);
      udpClient.send(udpPacket);
      
      ThreadUtils.waitForThread(() -> udpServer.getServerState() != ServerState.WAITING_FOR_CONNECTION);
      assertTrue(udpServer.getServerState().equals(ServerState.WAITING_FOR_CONNECTION));

      isConnected = !udpClient.isClosed();
      udpClient.close();
    } catch (IOException ex) {
      Logging.getLogger().error("An error occured", ex);
    }

    assertTrue(isConnected);
    assertEquals(ServerState.WAITING_FOR_CONNECTION, udpServer.getServerState());
  }

  private UDPServer getUdpServer() {
    ServerParametersUDP serverParametersUDP = new ServerParametersUDP.ParametersBuilder(LISTEN_PORT).withServerType(UDPServerType.BASIC).build();
    return new UDPServer(serverParametersUDP);
  }
}
