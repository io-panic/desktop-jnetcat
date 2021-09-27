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

import com.ioleak.jnetcat.client.TCPClient;
import com.ioleak.jnetcat.client.UDPClient;
import com.ioleak.jnetcat.common.arguments.CommandLineArguments;
import com.ioleak.jnetcat.common.utils.JsonUtils;
import com.ioleak.jnetcat.options.JNetcatParameters;
import com.ioleak.jnetcat.server.tcp.TCPServer;
import com.ioleak.jnetcat.server.udp.UDPServer;

public class JNetcat {

  public static void main(String... args) {
    String jsonParameters = "external/conf/AllOptionsInOne.json";
    CommandLineArguments cliArgs = new CommandLineArguments(args);
    int returnCode = 0;

    if (cliArgs.switchPresent("-f")) {
      jsonParameters = cliArgs.switchValue("-f");
    }

    String jsonData = JsonUtils.loadJsonFileToString(jsonParameters);
    JNetcatParameters params = JsonUtils.jsonToObject(jsonData, JNetcatParameters.class);
    if (params != null) {
      if (params.isStartAsServer()) {
        if (params.isUseProtocolTCP()) {
          TCPServer tcpListener = new TCPServer(params.getServerParametersTCP());
          tcpListener.startServer();
        } else {
          UDPServer tcpListener = new UDPServer(params.getServerParametersUDP());
          tcpListener.startServer();
        }
      } else {
        if (params.isUseProtocolTCP()) {
          TCPClient tcpClient = new TCPClient(params.getClientParametersTCP());
          tcpClient.open();

          if (!tcpClient.isConnected()) {
            returnCode = 1;
          }
        } else {
          UDPClient udpConnect = new UDPClient(params.getClientParametersUDP());
          udpConnect.open();
        }
      }
    } else {
      returnCode = 1;
    }
    
    /*
        ClientParametersTCP clientParametersTCP = new ClientParametersTCP.ParametersBuilder("192.168.135.55", 1).withNbClientMax(1).withNbExecution(1).build();
        ServerParametersTCP serverParametersTCP = new ServerParametersTCP.ParametersBuilder(8080).withIp("0.0.0.0").withMultiThread(false).withServerType(TCPServerType.ECHO).build();
        ClientParametersUDP clientParametersUDP = new ClientParametersUDP.ParametersBuilder("192.168.135.55", 1).withNbClientMax(1).withNbExecution(1).build();
        ServerParametersUDP serverParametersUDP = new ServerParametersUDP.ParametersBuilder(8080).withIp("0.0.0.0").withMultiThread(false).withServerType(UDPServerType.QUOTE).build();
        
        JNetcatParameters jnetcatParameters = new JNetcatParameters.ParametersBuilder(false, true).withClientParametersTCP(clientParametersTCP).withServerParametersTCP(serverParametersTCP).withClientParametersUDP(clientParametersUDP).withServerParametersUDP(serverParametersUDP).build();
        jnetcatParameters.saveToJsonFile(jsonParameters);
     */
 /*
        if (args.serverArgs != null && args.serverArgs.server) {
            TCPServer tcpListener = new TCPServer(args.serverArgs.serverType, port);
            tcpListener.startServer();
        } else if (args.clientArgs != null && args.clientArgs.client) {
            TCPConnect tcpClient = new TCPConnect(ip, Integer.valueOf(port)).open();
            Logging.getLogger().info(String.format("Connection established on %s:%s -> %s", ip, port, tcpClient.isConnected()));
            tcpClient.close();
        }*/
    System.exit(returnCode);
  }
}
