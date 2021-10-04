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
package com.ioleak.jnetcat.client.mock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.ioleak.jnetcat.common.Logging;

public class TCPServerMock
        implements Runnable {

  private ServerSocket server;
  private Socket clientSocket;

  public TCPServerMock() {
    try {
      server = new ServerSocket(0);
    } catch (IOException ex) {
      Logging.getLogger().error("Unable to start TCP server", ex);
    }
  }

  public void run() {
    try {
      while (!Thread.currentThread().isInterrupted()) {
        clientSocket = server.accept();
      }
    } catch (IOException ex) {
      Logging.getLogger().warn(String.format("Exception occured in run(): %s", ex.getMessage()));
    }
  }

  public String read() {
    try {
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      return bufferedReader.readLine();
    } catch (IOException ex) {
      Logging.getLogger().error(String.format("Unable to read from client: %s", ex.getMessage()));
    }

    return "";
  }

  public void write(String stringToWrite) {
    try {
      PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream());
      printWriter.write(stringToWrite);
      printWriter.flush();
    } catch (IOException ex) {
      Logging.getLogger().error(String.format("Unable to write to client: %s", ex.getMessage()));
    }
  }

  public int getPort() {
    return server.getLocalPort();
  }

  public void closeClient() {
    try {
      if (clientSocket != null && !clientSocket.isClosed()) {
        clientSocket.close();
      }
    } catch (IOException ex) {
      Logging.getLogger().error(String.format("Unable to close client", ex.getMessage()));
    }
  }

  public void closeServer() {
    try {
      closeClient();
      server.close();
    } catch (IOException ex) {
      Logging.getLogger().error(String.format("Unable to close server", ex.getMessage()));
    }
  }
}
