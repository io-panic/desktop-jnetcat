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

import com.ioleak.jnetcat.client.TCPClient;
import com.ioleak.jnetcat.client.UDPClient;
import com.ioleak.jnetcat.common.interfaces.ProcessAction;
import com.ioleak.jnetcat.common.utils.JsonUtils;
import com.ioleak.jnetcat.options.JNetcatParameters;
import com.ioleak.jnetcat.server.tcp.TCPServer;
import com.ioleak.jnetcat.server.udp.UDPServer;

public class JNetcatProcess
        implements Runnable {

  private final File jsonParamsFile;
  private int resultExecution = -1;

  private ProcessAction threadStop;
  private boolean executionInProgress = false;

  public JNetcatProcess(File jsonParamsFile) {
    this.jsonParamsFile = jsonParamsFile;
  }

  public void run() {
    if (executionInProgress) {
      throw new JNetcatAlreadyRunningException("An execution is already running");
    }

    executionInProgress = true;
    resultExecution = -1;
    int returnCode = 0;

    String jsonData = JsonUtils.loadJsonFileToString(jsonParamsFile);
    JNetcatParameters params = JsonUtils.jsonToObject(jsonData, JNetcatParameters.class);

    if (params != null) {
      if (params.isStartAsServer()) {
        if (params.isUseProtocolTCP()) {
          TCPServer tcpListener = new TCPServer(params.getServerParametersTCP());
          threadStop = tcpListener;
          tcpListener.startServer();
        } else {
          UDPServer udpListener = new UDPServer(params.getServerParametersUDP());
          threadStop = udpListener;
          udpListener.startServer();
        }
      } else {
        if (params.isUseProtocolTCP()) {
          TCPClient tcpClient = new TCPClient(params.getClientParametersTCP());
          threadStop = tcpClient;
          tcpClient.open();

          if (!tcpClient.isConnected()) {
            returnCode = 1;
          }
        } else {
          UDPClient udpConnect = new UDPClient(params.getClientParametersUDP());
          threadStop = udpConnect;
          udpConnect.open();
        }
      }
    } else {
      returnCode = 1;
    }

    executionInProgress = false;
    resultExecution = returnCode;
  }

  public boolean stopExecutions() {
    boolean haltedSuccess = false;
    if (threadStop != null) {
      haltedSuccess = threadStop.stopExecutions();
    }

    return haltedSuccess;
  }

  public boolean stopActiveExecution() {
    boolean haltedSuccess = false;
    if (threadStop != null) {
      haltedSuccess = threadStop.stopActiveExecution();
    }

    return haltedSuccess;
  }

  public int getResultExecution() {
    return resultExecution;
  }
}
