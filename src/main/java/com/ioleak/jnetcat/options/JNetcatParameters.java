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
package com.ioleak.jnetcat.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ioleak.jnetcat.common.BaseObject;
import com.ioleak.jnetcat.common.parsers.ArgumentsParser;
import com.ioleak.jnetcat.options.exception.ClientIncompatibleArgumentException;
import com.ioleak.jnetcat.options.exception.ServerIncompatibleArgumentException;
import com.ioleak.jnetcat.options.startup.ClientParametersTCP;
import com.ioleak.jnetcat.options.startup.ClientParametersUDP;
import com.ioleak.jnetcat.options.startup.ServerParametersTCP;
import com.ioleak.jnetcat.options.startup.ServerParametersUDP;

@JsonDeserialize(builder = JNetcatParameters.ParametersBuilder.class)
public class JNetcatParameters
        extends BaseObject {

  private final boolean startAsServer;
  private final boolean useProtocolTCP;

  private final OutputFormatConfig outputFormatConfig;
  
  private final ClientParametersTCP clientParametersTCP;
  private final ClientParametersUDP clientParametersUDP;

  private final ServerParametersTCP serverParametersTCP;
  private final ServerParametersUDP serverParametersUDP;

  public static class ParametersBuilder {

    private boolean startAsServer = false;
    private boolean useProtocolTCP = false;
    private OutputFormatConfig outputFormatConfig = new OutputFormatConfig();
    
    private ClientParametersTCP clientParametersTCP;
    private ClientParametersUDP clientParametersUDP;

    private ServerParametersTCP serverParametersTCP;
    private ServerParametersUDP serverParametersUDP;

    public ParametersBuilder(@JsonProperty("startAsServer") boolean startAsServer,
                             @JsonProperty("useProtocolTCP") boolean useProtocolTCP,
                             @JsonProperty("outputFormatConfig") OutputFormatConfig outputFormatConfig) {
      this.startAsServer = startAsServer;
      this.useProtocolTCP = useProtocolTCP;
      this.outputFormatConfig = outputFormatConfig;
    }

    public ParametersBuilder(JNetcatParameters params) {
      this.startAsServer = params.startAsServer;
      this.useProtocolTCP = params.useProtocolTCP;
      this.outputFormatConfig = params.outputFormatConfig;
      
      withClientParametersTCP(params.getClientParametersTCP());
      withClientParametersUDP(params.getClientParametersUDP());
      withServerParametersTCP(params.getServerParametersTCP());
      withServerParametersUDP(params.getServerParametersUDP());
    }

    public final ParametersBuilder withStartAsServer(boolean startAsServer) {
      this.startAsServer = startAsServer;
      return this;
    }

    public final ParametersBuilder withUseProtocolTCP(boolean useProtocolTCP) {
      this.useProtocolTCP = useProtocolTCP;
      return this;
    }

    public final ParametersBuilder withClientParametersTCP(ClientParametersTCP clientParametersTCP) {
      this.clientParametersTCP = clientParametersTCP;
      return this;
    }

    public final ParametersBuilder withServerParametersTCP(ServerParametersTCP serverParametersTCP) {
      this.serverParametersTCP = serverParametersTCP;
      return this;
    }

    public final ParametersBuilder withClientParametersUDP(ClientParametersUDP clientParametersUDP) {
      this.clientParametersUDP = clientParametersUDP;
      return this;
    }

    public final ParametersBuilder withServerParametersUDP(ServerParametersUDP serverParametersUDP) {
      this.serverParametersUDP = serverParametersUDP;
      return this;
    }

    public final JNetcatParameters build() {
      return new JNetcatParameters(this);
    }
  }

  public JNetcatParameters(ParametersBuilder builder) {
    this.startAsServer = builder.startAsServer;
    this.useProtocolTCP = builder.useProtocolTCP;
    this.outputFormatConfig = builder.outputFormatConfig;
    
    this.clientParametersTCP = builder.clientParametersTCP;
    this.serverParametersTCP = builder.serverParametersTCP;
    this.clientParametersUDP = builder.clientParametersUDP;
    this.serverParametersUDP = builder.serverParametersUDP;

    validateParameters();
  }

  public boolean isStartAsServer() {
    return startAsServer;
  }

  public boolean isUseProtocolTCP() {
    return useProtocolTCP;
  }
  
  public OutputFormatConfig getOutputFormatConfig() {
    return outputFormatConfig;
  }
  
  public ClientParametersTCP getClientParametersTCP() {
    return clientParametersTCP;
  }

  public ServerParametersTCP getServerParametersTCP() {
    return serverParametersTCP;
  }

  public ClientParametersUDP getClientParametersUDP() {
    return clientParametersUDP;
  }

  public ServerParametersUDP getServerParametersUDP() {
    return serverParametersUDP;
  }

  private void validateParameters() {
    if (startAsServer && useProtocolTCP && serverParametersTCP == null) {
      throw new ServerIncompatibleArgumentException("Missing server TCP parameters");
    }

    if (startAsServer && !useProtocolTCP && serverParametersUDP == null) {
      throw new ServerIncompatibleArgumentException("Missing server UDP parameters");
    }

    if (!startAsServer && useProtocolTCP && clientParametersTCP == null) {
      throw new ClientIncompatibleArgumentException("Missing client TCP parameters");
    }

    if (!startAsServer && !useProtocolTCP && clientParametersUDP == null) {
      throw new ClientIncompatibleArgumentException("Missing client UDP parameters");
    }
  }

  public JNetcatParameters getOverridenParameters(ArgumentsParser argumentsParser) {
    if (argumentsParser == null) {
      return this;
    }

    JNetcatParameters.ParametersBuilder jNetcatParametersBuilder = new JNetcatParameters.ParametersBuilder(this);
    ClientParametersTCP.ParametersBuilder clientParametersTCPBuilder = new ClientParametersTCP.ParametersBuilder(getClientParametersTCP());
    ClientParametersUDP.ParametersBuilder clientParametersUDPBuilder = new ClientParametersUDP.ParametersBuilder(getClientParametersUDP());
    ServerParametersTCP.ParametersBuilder serverParametersTCPBuilder = new ServerParametersTCP.ParametersBuilder(getServerParametersTCP());
    ServerParametersUDP.ParametersBuilder serverParametersUDPBuilder = new ServerParametersUDP.ParametersBuilder(getServerParametersUDP());

    if (argumentsParser.switchPresent("-t")) {
      boolean useAsServer = argumentsParser.switchValue("-t").equals("s");
      jNetcatParametersBuilder.withStartAsServer(useAsServer);
    }

    if (argumentsParser.switchPresent("-c")) {
      boolean useUDP = argumentsParser.switchValue("-c").equals("u");
      jNetcatParametersBuilder.withUseProtocolTCP(!useUDP);
    }

    if (argumentsParser.switchPresent("-i")) {
      String ip = argumentsParser.switchValue("-i");

      clientParametersTCPBuilder.withIp(ip);
      clientParametersUDPBuilder.withIp(ip);
      serverParametersTCPBuilder.withIp(ip);
      serverParametersUDPBuilder.withIp(ip);
    }

    if (argumentsParser.switchPresent("-p")) {
      int port = argumentsParser.switchIntValue("-p");

      clientParametersTCPBuilder.withPort(port);
      clientParametersUDPBuilder.withPort(port);
      serverParametersTCPBuilder.withPort(port);
      serverParametersUDPBuilder.withPort(port);
    }

    if (argumentsParser.switchPresent("-ci")) {
      clientParametersTCPBuilder.withInteractive(true);
      clientParametersUDPBuilder.withInteractive(true);
    }

    jNetcatParametersBuilder.withClientParametersTCP(clientParametersTCPBuilder.build());
    jNetcatParametersBuilder.withClientParametersUDP(clientParametersUDPBuilder.build());
    jNetcatParametersBuilder.withServerParametersTCP(serverParametersTCPBuilder.build());
    jNetcatParametersBuilder.withServerParametersUDP(serverParametersUDPBuilder.build());

    return jNetcatParametersBuilder.build();
  }
}
