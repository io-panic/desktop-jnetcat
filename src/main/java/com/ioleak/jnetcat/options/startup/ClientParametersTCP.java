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

@JsonDeserialize(builder = ClientParametersTCP.ParametersBuilder.class)
public class ClientParametersTCP
        extends ClientParameters {

  public static class ParametersBuilder
          extends ClientParameters.ParametersBuilder<ParametersBuilder> {

    public ParametersBuilder(@JsonProperty("ip") String ip,
                             @JsonProperty("port") int port) {
      super(ip, port);
    }

    public ParametersBuilder(ClientParametersTCP clientParametersTCP) {
      super(clientParametersTCP.getIp(), clientParametersTCP.getPort());

      withNbClientMax(clientParametersTCP.getNbClientMax());
      withNbExecution(clientParametersTCP.getNbExecution());
      withSleepBetweenExecMs(clientParametersTCP.getSleepBetweenExecMs());
      withSoTimeout(clientParametersTCP.getSoTimeout());
      withInteractive(clientParametersTCP.isInteractive());
    }

    @Override
    public ClientParametersTCP build() {
      return new ClientParametersTCP(this);
    }

    @Override
    protected ParametersBuilder self() {
      return this;
    }
  }

  private ClientParametersTCP(ParametersBuilder builder) {
    super(builder);
  }
}
