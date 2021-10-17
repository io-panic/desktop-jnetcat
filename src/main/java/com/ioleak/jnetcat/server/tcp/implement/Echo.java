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
package com.ioleak.jnetcat.server.tcp.implement;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;

import com.ioleak.jnetcat.common.utils.StringUtils;
import com.ioleak.jnetcat.server.tcp.TCPClientConnection;

public class Echo
        extends TCPClientConnection {

  private Queue<String> commands = new LinkedList<>();

  @Override
  public void initClient(OutputStream out)
          throws IOException {
    out.write(StringUtils.getBytesFromString("Welcome on super echo server v0.0.0-alpha0\n"));
    out.write(StringUtils.getBytesFromString("This server accept only one connection at a time\n"));
    out.write(StringUtils.getBytesFromString("You can type anything you want. Type 'exit' to quit\n\n"));
  }

  @Override
  public void dataRead(String readData) {
    commands.add(StringUtils.removeLastCharIfCRLF(readData));
  }

  @Override
  public void dataSend(OutputStream out)
          throws IOException {
    String currentCommand = commands.poll();
    if (currentCommand != null) {
      if (currentCommand.toLowerCase().equals("exit")) {
        try (out) {
          out.write(StringUtils.getBytesFromString("\nThanks for trying, astalavista!\n"));
          out.flush();
        }
      } else {
        out.write(StringUtils.getBytesFromString(currentCommand));
        out.flush();
      }
    }
  }
}
