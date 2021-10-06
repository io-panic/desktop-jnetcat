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

import com.ioleak.jnetcat.client.exception.ClientReadMessageException;
import com.ioleak.jnetcat.client.exception.ClientSendMessageException;
import com.ioleak.jnetcat.client.mock.TCPServerMock;
import com.ioleak.jnetcat.common.Logging;
import com.ioleak.jnetcat.options.startup.ClientParametersTCP;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TCPClientTest {

  TCPServerMock tcpServerMock;
  Thread tcpServerThread;

  TCPClient tcpClient;
          
  @BeforeEach
  private void setUp() {
    tcpServerMock = new TCPServerMock();
    tcpServerThread = new Thread(tcpServerMock::run);
    tcpServerThread.start();
    
    ClientParametersTCP clientParametersTCP = new ClientParametersTCP.ParametersBuilder("127.0.0.1", tcpServerMock.getPort()).build();
    tcpClient = new TCPClient(clientParametersTCP);
  }

  @AfterEach
  private void tearDown() {
    tcpServerMock.closeServer();
    tcpServerThread.interrupt();

    try {
      tcpServerThread.join();
    } catch (InterruptedException ex) {
      Logging.getLogger().error("InterruptedException", ex);
    }
  }

  @Test
  public void start_PortNotOpen_NotConnected() {
    ClientParametersTCP clientParametersTCP = new ClientParametersTCP.ParametersBuilder("127.0.0.1", 33291).build();
    tcpClient = new TCPClient(clientParametersTCP);
    tcpClient.start();
    
    assertFalse(tcpClient.connectedProperty().get());
  }
  
  @Test
  public void sendMessage_NoConnection_ExceptionThrown() {
    assertThrows(ClientSendMessageException.class, () -> tcpClient.sendMessage("Hello wait"));
  }

  @Test
  public void sendMessage_StringWithCRLF_AsExpected() {
    tcpClient.start();
    assertTrue(tcpClient.connectedProperty().get());

    tcpClient.sendMessage("Hello wait\n");
    assertEquals("Hello wait", tcpServerMock.read());
  }

  @Test
  public void sendMessage_ServerCloseClient_ExceptionThrown() {
    tcpClient.start();
    assertTrue(tcpClient.connectedProperty().get());

    tcpServerMock.closeClient();

    assertThrows(ClientSendMessageException.class, () -> tcpClient.sendMessage("Test message\n"));
    assertFalse(tcpClient.connectedProperty().get());
  }

  @Test
  public void readMessage_NoConnection_ExceptionThrown() {
    assertThrows(ClientReadMessageException.class, () -> tcpClient.readMessage());
  }

  @Test
  public void readMessage_StringWithCRLF_AsExpected() {
    tcpClient.start();
    assertTrue(tcpClient.connectedProperty().get());

    tcpServerMock.write("Hello wait\n");
    assertEquals("Hello wait", tcpClient.readMessage());
  }

  @Test
  public void readMessage_ServerCloseClient_ExceptionThrown() {
    tcpClient.start();
    assertTrue(tcpClient.connectedProperty().get());

    tcpServerMock.closeClient();

    assertThrows(ClientReadMessageException.class, () -> tcpClient.readMessage());
    assertFalse(tcpClient.connectedProperty().get());
  }
}
