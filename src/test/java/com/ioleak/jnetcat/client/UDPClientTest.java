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
import com.ioleak.jnetcat.client.mock.UDPServerMock;
import com.ioleak.jnetcat.common.Logging;
import com.ioleak.jnetcat.formatter.PrettyHexStringOutput;
import com.ioleak.jnetcat.options.startup.ClientParametersUDP;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UDPClientTest {

  UDPServerMock udpServerMock;
  Thread udpServerThread;

  @BeforeEach
  private void setUp() {
    udpServerMock = new UDPServerMock();
    udpServerThread = new Thread(udpServerMock::run);
    udpServerThread.start();
  }

  @AfterEach
  private void tearDown() {
    udpServerMock.closeServer();
    udpServerThread.interrupt();

    try {
      udpServerThread.join();
    } catch (InterruptedException ex) {
      Logging.getLogger().error("InterruptedException", ex);
    }
  }

  @Test
  public void sendMessage_ClientNotStarted_ExceptionThrown() {
    UDPClient udpClient = getUDPClient(null);
    assertThrows(ClientSendMessageException.class, () -> udpClient.sendMessage("Hello wait"));
  }

  @Test
  public void readMessage_ClientNotStarted_ExceptionThrown() {
    UDPClient udpClient = getUDPClient(null);
    assertThrows(ClientReadMessageException.class, () -> udpClient.readMessage());
  }

  @Test
  public void readMessage_ParameterTimeout_ExceptionThrownWithRightTimeout() {
    ClientParametersUDP clientParametersUDP = new ClientParametersUDP.ParametersBuilder("127.0.0.1", udpServerMock.getPort()).withSoTimeout(1000).build();
    UDPClient udpClient = getUDPClient(clientParametersUDP);

    long startTime = System.currentTimeMillis();
    udpClient.start();

    assertThrows(ClientReadMessageException.class, () -> udpClient.readMessage());

    long timeDiff = System.currentTimeMillis() - startTime;
    assertTrue(timeDiff > 1000 && timeDiff < 2000);
  }

  @Test
  public void sendMessage_ClientStarted_ExceptionThrown() {
    UDPClient udpClient = getUDPClient(null);
    udpClient.start();
    udpClient.sendMessage("1");

    assertEquals(UDPServerMock.RESPONSE_MOCK, udpClient.readMessage());
  }

  @Test
  @Disabled("Test used with a real connnection on a QUOTE server")
  public void test() {
    //ClientParametersUDP clientParametersUDP = new ClientParametersUDP.ParametersBuilder("127.0.0.1", 1234).withSoTimeout(0).build();
    ClientParametersUDP clientParametersUDP = new ClientParametersUDP.ParametersBuilder("23.28.179.206", 17).withSoTimeout(0).build();
    //UDPClient udpConnect = new UDPClient("djxmmx.net", 17);

    UDPClient udpConnect = getUDPClient(clientParametersUDP);
    udpConnect.start();
    udpConnect.sendMessage("1");
    System.out.println(udpConnect.readMessage());
    udpConnect.sendMessage("2");
    System.out.println(udpConnect.readMessage());
  }

  public UDPClient getUDPClient(ClientParametersUDP clientParametersUDP) {
    if (clientParametersUDP == null) {
      clientParametersUDP = new ClientParametersUDP.ParametersBuilder("127.0.0.1", udpServerMock.getPort()).build();
    }
    
    UDPClient udpClient = new UDPClient(clientParametersUDP);
    udpClient.setFormatOutput(new PrettyHexStringOutput(null, 20));  // TODO replace with a more BASIC formatter

    return udpClient;
  }
}
