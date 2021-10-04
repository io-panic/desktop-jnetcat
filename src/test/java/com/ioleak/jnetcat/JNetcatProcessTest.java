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

import com.ioleak.jnetcat.client.ClientNotConnectedException;
import com.ioleak.jnetcat.client.TCPClient;
import com.ioleak.jnetcat.common.Logging;
import com.ioleak.jnetcat.common.utils.ThreadUtils;
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

  private static final int THREAD_MAX_WAIT_MS = 10000;
  private static final int THREAD_MIN_WAIT_MS = 100;
  
  private static final int LISTEN_PORT = 41959;

  private JNetcatProcess jNetcatProcess;
  private Thread jNetcatThread;

  @BeforeAll
  public void setUpAll() {
    jNetcatProcess = JNetcatProcess.JNETCATPROCESS;
  }

  @Test
  public void run_OneInstanceOnly_SameMemoryAddress() {
    JNetcatProcess jNetcatProcessLocal = JNetcatProcess.JNETCATPROCESS;
    assertEquals(jNetcatProcess, jNetcatProcessLocal);
  }

  @Test
  public void run_RunWithoutFileParams_ExceptionThrown() {
    jNetcatProcess.setJsonParamsFile(null);
    assertThrows(JNetcatProcessFileNotSetException.class, () -> jNetcatProcess.run());
  }

  @Test
  public void run_RunWithInvalidFile_ErrorCode() {
    jNetcatProcess.setJsonParamsFile(new File("test/FileDontExists.json"));
    jNetcatProcess.run();

    assertEquals(JNetcatProcessResult.ERROR, jNetcatProcess.getResultExecution());
  }

  @Test
  @Order(1)
  public void run_ThreadOrder1_ExecutionInProgressCode() {
    JNetcatParameters jNetcatParameters = new JNetcatParameters.ParametersBuilder(true, true)
            .withServerParametersTCP(new ServerParametersTCP.ParametersBuilder(LISTEN_PORT).withServerType(TCPServerType.ECHO).build()).build();

    jNetcatThread = new Thread() {
      @Override
      public void run() {
        jNetcatProcess.run(jNetcatParameters);
      }
    };

    jNetcatThread.start();
    ThreadUtils.waitForThread(() ->  jNetcatProcess.getResultExecution() != JNetcatProcessResult.IN_PROGRESS);

    assertEquals(JNetcatProcessResult.IN_PROGRESS, jNetcatProcess.getResultExecution());
  }

  @Test
  @Order(2)
  public void run_ThreadOrder2_AlreadyRunningCode() {
    JNetcatParameters jNetcatParameters = new JNetcatParameters.ParametersBuilder(true, true)
            .withServerParametersTCP(new ServerParametersTCP.ParametersBuilder(8080).build()).build();

    assertThrows(JNetcatProcessRunningException.class, () -> jNetcatProcess.run(jNetcatParameters));
  }

  @Test
  @Order(3)
  public void stopActiveExecution_ThreadOrder3_KeepInProgressStatus() {
    final ClientParametersTCP clientParametersTCP = new ClientParametersTCP.ParametersBuilder("127.0.0.1", LISTEN_PORT).build();
    final TCPClient tcpClient = new TCPClient(clientParametersTCP);
    
    tcpClient.open();
    assertTrue(tcpClient.connectedProperty().get());
    
    jNetcatProcess.stopActiveExecution();
    
    assertThrows(ClientNotConnectedException.class, () -> tcpClient.sendMessage("Test message"));
    assertFalse(tcpClient.connectedProperty().get());
    assertEquals(JNetcatProcessResult.IN_PROGRESS, jNetcatProcess.getResultExecution());
  }

  @Test
  @Order(4)
  public void stopExecutions_ThreadOrder4_SuccessCode() {
    Logging.getLogger().warn(String.format("Executing: %s", new Throwable().getStackTrace()[0].getMethodName()));
    
    jNetcatProcess.stopExecutions();

    try {
      jNetcatThread.join();
    } catch (InterruptedException ex) {
      Logging.getLogger().error("Interrupted Exception", ex);
    }

    assertEquals(JNetcatProcessResult.SUCCESS, jNetcatProcess.getResultExecution());
  }
}
