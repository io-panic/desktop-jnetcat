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

  private final ClientParametersTCP clientParametersTCP;
  private final ClientParametersUDP clientParametersUDP;

  private final ServerParametersTCP serverParametersTCP;
  private final ServerParametersUDP serverParametersUDP;

  public static class ParametersBuilder {

    private boolean startAsServer = false;
    private boolean useProtocolTCP = false;

    private ClientParametersTCP clientParametersTCP;
    private ClientParametersUDP clientParametersUDP;

    private ServerParametersTCP serverParametersTCP;
    private ServerParametersUDP serverParametersUDP;

    public ParametersBuilder(@JsonProperty("startAsServer") boolean startAsServer,
                             @JsonProperty("useProtocolTCP") boolean useProtocolTCP) {
      this.startAsServer = startAsServer;
      this.useProtocolTCP = useProtocolTCP;
    }

    public ParametersBuilder withClientParametersTCP(ClientParametersTCP clientParametersTCP) {
      this.clientParametersTCP = clientParametersTCP;
      return this;
    }

    public ParametersBuilder withServerParametersTCP(ServerParametersTCP serverParametersTCP) {
      this.serverParametersTCP = serverParametersTCP;
      return this;
    }

    public ParametersBuilder withClientParametersUDP(ClientParametersUDP clientParametersUDP) {
      this.clientParametersUDP = clientParametersUDP;
      return this;
    }

    public ParametersBuilder withServerParametersUDP(ServerParametersUDP serverParametersUDP) {
      this.serverParametersUDP = serverParametersUDP;
      return this;
    }

    public JNetcatParameters build() {
      return new JNetcatParameters(this);
    }
  }

  public JNetcatParameters(ParametersBuilder builder) {
    this.startAsServer = builder.startAsServer;
    this.useProtocolTCP = builder.useProtocolTCP;
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
}
