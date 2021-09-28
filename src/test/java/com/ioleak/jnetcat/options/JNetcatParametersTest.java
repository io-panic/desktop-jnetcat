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

import com.ioleak.jnetcat.common.utils.JsonUtils;
import com.ioleak.jnetcat.options.exception.ClientIncompatibleArgumentException;
import com.ioleak.jnetcat.options.exception.IPv4InvalidArgumentException;
import com.ioleak.jnetcat.options.exception.PortInvalidArgumentException;
import com.ioleak.jnetcat.options.exception.ServerIncompatibleArgumentException;
import com.ioleak.jnetcat.options.startup.ClientParametersTCP;
import com.ioleak.jnetcat.options.startup.ClientParametersUDP;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JNetcatParametersTest {

  private static final String JSON_PARAM_FILE = "conf/AllInOneParametersTest.json";

  @Test
  public void jsonToObject_FileParameter_ContentValid() {
    String json = JsonUtils.loadJsonFileToString(JSON_PARAM_FILE);
    JNetcatParameters params = JsonUtils.jsonToObject(json, JNetcatParameters.class);

    assertTrue(params.isStartAsServer());
    assertTrue(params.isUseProtocolTCP());
  }

  @Test
  public void ParametersBulder_InvalidServerOptions_ThrowsException() {
    assertThrows(ServerIncompatibleArgumentException.class, () -> new JNetcatParameters.ParametersBuilder(true, true).build());
    assertThrows(ServerIncompatibleArgumentException.class, () -> new JNetcatParameters.ParametersBuilder(true, false).build());
  }

  @Test
  public void ParametersBulder_InvalidClientOptions_ThrowsException() {
    assertThrows(ClientIncompatibleArgumentException.class, () -> new JNetcatParameters.ParametersBuilder(false, true).build());
    assertThrows(ClientIncompatibleArgumentException.class, () -> new JNetcatParameters.ParametersBuilder(false, false).build());
  }

  @Test
  public void ParametersBulder_InvalidIP_ThrowsException() {
    assertThrows(IPv4InvalidArgumentException.class, () -> new ClientParametersTCP.ParametersBuilder("192.168.1.256", 80).build());
    assertThrows(IPv4InvalidArgumentException.class, () -> new ClientParametersUDP.ParametersBuilder("192.168.1.256", 80).build());
  }

  @Test
  public void ParametersBulder_InvalidPort_ThrowsException() {
    assertThrows(PortInvalidArgumentException.class, () -> new ClientParametersTCP.ParametersBuilder("192.168.1.255", -100).build());
    assertThrows(PortInvalidArgumentException.class, () -> new ClientParametersTCP.ParametersBuilder("192.168.1.255", 655356).build());
    assertThrows(PortInvalidArgumentException.class, () -> new ClientParametersUDP.ParametersBuilder("192.168.1.255", -100).build());
    assertThrows(PortInvalidArgumentException.class, () -> new ClientParametersUDP.ParametersBuilder("192.168.1.255", 655356).build());
  }
}
