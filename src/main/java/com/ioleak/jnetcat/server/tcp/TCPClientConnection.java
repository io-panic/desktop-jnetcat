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
package com.ioleak.jnetcat.server.tcp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import com.ioleak.jnetcat.formatter.helpers.StreamFormatOutput;

public abstract class TCPClientConnection {

  private StreamFormatOutput streamFormatOutput;
  
  public abstract void initClient(OutputStream out) throws IOException;
  public abstract void dataRead(String readData);
  public abstract void dataSend(OutputStream out) throws IOException;

  public final void startClient(Socket clientSocket, StreamFormatOutput streamFormatOutput)
          throws IOException, SocketException {

    this.streamFormatOutput = streamFormatOutput;
    initClient(clientSocket.getOutputStream());

    while (!Thread.currentThread().isInterrupted()) {
      streamFormatOutput.startReading(clientSocket.getInputStream());
      String readData = streamFormatOutput.getEndOfStreamData();

      if (!readData.isBlank()) {
        dataRead(readData);
        dataSend(clientSocket.getOutputStream());
      }
    }
  }
}
