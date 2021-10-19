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
package com.ioleak.jnetcat.options.startup;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ioleak.jnetcat.server.udp.UDPServerType;

@JsonDeserialize(builder = ServerParametersUDP.ParametersBuilder.class)
public class ServerParametersUDP
        extends ServerParameters {

  private final UDPServerType udpServerType;

  public static class ParametersBuilder
          extends ServerParameters.ParametersBuilder<ParametersBuilder> {

    private UDPServerType udpServerType;

    public ParametersBuilder(@JsonProperty("port") int port) {
      super(port);
    }

    public ParametersBuilder(ServerParametersUDP serverParametersUDP) {
      super(serverParametersUDP);
      withServerType(serverParametersUDP.getServerType());
    }

    public final ParametersBuilder withServerType(UDPServerType udpServerType) {
      this.udpServerType = udpServerType;
      return this;
    }

    @Override
    public ServerParametersUDP build() {
      return new ServerParametersUDP(this);
    }

    @Override
    protected ParametersBuilder self() {
      return this;
    }
  }

  private ServerParametersUDP(ParametersBuilder builder) {
    super(builder);

    this.udpServerType = builder.udpServerType;
  }

  public UDPServerType getServerType() {
    return udpServerType;
  }
}
