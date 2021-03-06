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
package com.ioleak.jnetcat.server;

import com.ioleak.jnetcat.server.tcp.TCPClientConnection;
import com.ioleak.jnetcat.server.tcp.TCPServerType;
import com.ioleak.jnetcat.server.tcp.implement.Echo;
import com.ioleak.jnetcat.server.tcp.implement.Proxy;
import com.ioleak.jnetcat.server.udp.UDPClientConnection;
import com.ioleak.jnetcat.server.udp.UDPServerType;
import com.ioleak.jnetcat.server.udp.implement.Quote;
import com.ioleak.jnetcat.server.udp.implement.Shell;

public class ClientConnectionFactory {

  public static TCPClientConnection createClientConnectionTCP(TCPServerType tcpServerType) {
    TCPClientConnection server = null;

    switch (TCPServerType.valueOf(tcpServerType.name())) {
      case BASIC:
        server = new com.ioleak.jnetcat.server.tcp.implement.Basic();
        break;
      case ECHO:
        server = new Echo();
        break;
      case PROXY:
        server = new Proxy();
        break;
    }

    return server;
  }

  public static UDPClientConnection createClientConnectionUDP(UDPServerType udpServerType) {
    UDPClientConnection server = null;

    switch (UDPServerType.valueOf(udpServerType.name())) {
      case BASIC:
        server = new com.ioleak.jnetcat.server.udp.implement.Basic();
        break;
      case QUOTE:
        server = new Quote();
        break;
      case SHELL:
        server = new Shell();
        break;
    }

    return server;
  }
}
