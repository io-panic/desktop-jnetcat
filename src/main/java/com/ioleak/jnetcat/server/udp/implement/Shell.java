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
package com.ioleak.jnetcat.server.udp.implement;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;

import com.ioleak.jnetcat.common.ProcessExecutor;
import com.ioleak.jnetcat.common.utils.StringUtils;
import com.ioleak.jnetcat.server.udp.UDPClientConnection;

public class Shell
        extends UDPClientConnection {

  private static final int MAX_PACKET_LENGTH = 1024;
  private final Queue<String> commands = new LinkedList<>();

  @Override
  public void dataRead(String readData) {
    commands.add(StringUtils.removeLastCharIfCRLF(readData));
  }

  @Override
  public void dataSend(DatagramSocket datagramSocket, DatagramPacket request) throws IOException {
    String lastCommand = commands.poll();

    InetAddress clientAddress = request.getAddress();
    int clientPort = request.getPort();
    byte[] data = StringUtils.getBytesFromString(new ProcessExecutor().execute(lastCommand).toString());

    // Logging.getLogger().info(String.format("Response sent to %s:%d", clientAddress.getHostAddress(), clientPort));
    datagramSocket.send(new DatagramPacket(data, data.length, clientAddress, clientPort));
  }

  @Override
  public int getMaxPacketLength() {
    return MAX_PACKET_LENGTH;
  }
}
