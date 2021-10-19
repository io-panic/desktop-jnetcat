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
package com.ioleak.jnetcat.service;

import com.ioleak.jnetcat.client.TCPClient;
import com.ioleak.jnetcat.client.UDPClient;
import com.ioleak.jnetcat.common.interfaces.ProcessAction;
import com.ioleak.jnetcat.common.properties.Observable;
import com.ioleak.jnetcat.options.JNetcatParameters;
import com.ioleak.jnetcat.server.tcp.TCPServer;
import com.ioleak.jnetcat.server.udp.UDPServer;

public class JNetcatProcessFactory {

  public static ProcessAction createProcess(JNetcatParameters params, Observable keyListener) {
    ProcessAction processAction;

    if (params.isStartAsServer()) {
      if (params.isUseProtocolTCP()) {
        processAction = new TCPServer(params.getServerParametersTCP());
      } else {
        processAction = new UDPServer(params.getServerParametersUDP());
      }
    } else {
      if (params.isUseProtocolTCP()) {
        processAction = new TCPClient(params.getClientParametersTCP());
      } else {
        processAction = new UDPClient(params.getClientParametersUDP());
      }
    }

    // TODO Use params to change the output format
    //  = new PrettyHexStringOutput(null, 20);  // new SimpleLoggerStringOutput(); // // new PrettyHexStringOutput(20);
    processAction.setFormatOutput(params.getOutputFormatConfig().getFormatOutputType().getFormatOutput(params.getOutputFormatConfig().getLineWidth()));
    processAction.setKeyListener(keyListener);

    return processAction;
  }
}
