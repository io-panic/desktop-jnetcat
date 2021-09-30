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
package com.ioleak.jnetcat;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ioleak.jnetcat.client.TCPClient;
import com.ioleak.jnetcat.options.JNetcatParameters;
import com.ioleak.jnetcat.options.startup.ClientParametersTCP;
import com.ioleak.jnetcat.options.startup.ServerParametersTCP;
import com.ioleak.jnetcat.server.tcp.TCPServerType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class JNetcatProcessTest {

  private static final int MAX_THREAD_WAIT_MS = 10000;
  private static final int LISTEN_PORT = 41959;

  private JNetcatProcess jNetcatProcess;
  private Thread jNetcatThread;

  @BeforeAll
  public void initJNetcatProces() {
    jNetcatProcess = JNetcatProcess.JNETCATPROCESS;
  }

  @Test
  public void jNetcatProcess_OneInstanceOnly_SameAddress() {
    JNetcatProcess jNetcatProcessLocal = JNetcatProcess.JNETCATPROCESS;
    assertEquals(jNetcatProcess, jNetcatProcessLocal);
  }

  @Test
  public void jNetcatProcess_RunWithoutFileParams_ExceptionThrown() {
    assertThrows(JNetcatProcessFileNotSetException.class, () -> jNetcatProcess.run());
  }

  @Test
  public void jNetcatProcess_RunWithInvalidFile_ErrorCode() {
    jNetcatProcess.setJsonParamsFile(new File("test/FileDontExists.json"));
    jNetcatProcess.run();

    assertEquals(JNetcatProcessResult.ERROR, jNetcatProcess.getResultExecution());
  }

  @Test
  @Order(1)
  public void jNetcatProcess_RunWithoInvalidFile_ExecutionInProgressCode() {
    JNetcatParameters jNetcatParameters = new JNetcatParameters.ParametersBuilder(true, true)
            .withServerParametersTCP(new ServerParametersTCP.ParametersBuilder(LISTEN_PORT).withServerType(TCPServerType.ECHO).build()).build();

    jNetcatThread = new Thread() {
      @Override
      public void run() {
        jNetcatProcess.run(jNetcatParameters);
      }
    };

    jNetcatThread.start();

    int i = 0;
    while (i <= 10 && jNetcatProcess.getResultExecution() != JNetcatProcessResult.IN_PROGRESS) {     
      try {
        Thread.sleep(250);
      } catch (InterruptedException ex) {
        Logger.getLogger(JNetcatProcessTest.class.getName()).log(Level.SEVERE, null, ex);
      }
      
      i++;
    }

    assertEquals(JNetcatProcessResult.IN_PROGRESS, jNetcatProcess.getResultExecution());
  }

  @Test
  @Order(2)
  public void jNetcatProcess_RunWithoInvalidFile_AlreadyRunningCode() {
    JNetcatParameters jNetcatParameters = new JNetcatParameters.ParametersBuilder(true, true)
            .withServerParametersTCP(new ServerParametersTCP.ParametersBuilder(8080).build()).build();

    assertThrows(JNetcatProcessRunningException.class, () -> jNetcatProcess.run(jNetcatParameters));
  }

  @Test
  @Order(3)
  public void jNetcatProcess_StopActiveExecution_SuccessCode() {
    final ClientParametersTCP clientParametersTCP = new ClientParametersTCP.ParametersBuilder("127.0.0.1", LISTEN_PORT).build();
    final TCPClient tcpClient = new TCPClient(clientParametersTCP);

    Thread tcpConnect = new Thread() {
      public void run() {
        tcpClient.open();
      }
    };

    tcpConnect.start();

    int i = 0;
    while (i <= 10 && !tcpClient.isConnected()) {
      try {
        Thread.sleep(250);
      } catch (InterruptedException ex) {
        Logger.getLogger(JNetcatProcessTest.class.getName()).log(Level.SEVERE, null, ex);
      }
      
      i++;
    }
    assertTrue(tcpClient.isConnected());
    jNetcatProcess.stopActiveExecution();

    tcpClient.sendMessage("Test message");
    try {
      tcpConnect.join();
    } catch (InterruptedException ex) {
      Logger.getLogger(JNetcatProcessTest.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    assertFalse(tcpClient.isConnected());
  }

  @Test
  @Order(4)
  public void jNetcatProcess_StopExecutions_SuccessCode() {
    jNetcatProcess.stopExecutions();

    try {
      jNetcatThread.join(MAX_THREAD_WAIT_MS);
    } catch (InterruptedException ex) {
      Logger.getLogger(JNetcatProcessTest.class.getName()).log(Level.SEVERE, null, ex);
    }

    assertEquals(JNetcatProcessResult.SUCCESS, jNetcatProcess.getResultExecution());
  }
}
