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

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import com.ioleak.jnetcat.common.Logging;
import com.ioleak.jnetcat.common.exception.FileNotFoundException;
import com.ioleak.jnetcat.common.utils.JsonUtils;
import com.ioleak.jnetcat.common.utils.StringUtils;
import com.ioleak.jnetcat.server.udp.UDPClientConnection;

public class Quote
        extends UDPClientConnection {

  public static final String DEFAULT_QUOTES_TXT = "conf/quotes.txt";
  private static final int MAX_PACKET_LENGTH = 1;
  
  private final List<String> quotes = new ArrayList<>();
  private final Queue<String> commands = new LinkedList<>();

  public Quote() {
    loadQuote(DEFAULT_QUOTES_TXT);
  }

  private void loadQuote(String relativePath) {
    URL url = JsonUtils.getAbsolutePathTo(relativePath);

    try {
      File jsonFile = new File(url.toURI());
      if (!jsonFile.exists()) {
        throw new FileNotFoundException(String.format("File don't exists: %s", url.toString()));
      }

      String rawQuotes = Files.readString(jsonFile.toPath());
      quotes.addAll(Arrays.asList(rawQuotes.split("\n")));

    } catch (URISyntaxException ex) {
      Logging.getLogger().error("Unable to load QUOTE file: syntax error", ex);
    } catch (IOException ex) {
      Logging.getLogger().error("Unable to load QUOTE file: exception on read", ex);
    }
  }

  @Override
  public void dataRead(String readData) {
    commands.add(StringUtils.removeLastCharIfCRLF(readData));
  }

  @Override
  public void dataSend(DatagramSocket datagramSocket, DatagramPacket request) throws IOException {
    String response = "Unknown command sent. Please retry";
    String lastCommand = commands.poll();

    if (lastCommand != null && lastCommand.equals("1")) {
      int selectedQuoteIndex = new Random().nextInt(quotes.size());
      response = quotes.get(selectedQuoteIndex);
    }

    InetAddress clientAddress = request.getAddress();
    int clientPort = request.getPort();
    byte[] data = StringUtils.getBytesFromString(response);

    // Logging.getLogger().info(String.format("Response sent to %s:%d", clientAddress.getHostAddress(), clientPort));
    datagramSocket.send(new DatagramPacket(data, data.length, clientAddress, clientPort));
  }

  @Override
  public int getMaxPacketLength() {
    return MAX_PACKET_LENGTH;
  }
}
