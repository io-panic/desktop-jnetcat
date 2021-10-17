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
package com.ioleak.jnetcat.formatter;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import com.ioleak.jnetcat.common.Logging;
import com.ioleak.jnetcat.common.utils.StringUtils;

public class PrettyHexStringOutput
        extends StreamRawReaderNoOutput {
  
  private final int lineWidth;
  
  private final List<Byte> bufferData = new ArrayList<>();

  private int data = 0;
  private int shift = 1;

  public PrettyHexStringOutput(int lineWidth) {
    this(null, lineWidth);
  }
  
  public PrettyHexStringOutput(StreamFormatOutput prettyFormatOutput, int lineWidth) {
    super(prettyFormatOutput);
    
    this.lineWidth = lineWidth;
  }
  
  @Override
  public void formatDataOutput(PropertyChangeEvent evt) {
    super.formatDataOutput(evt);
    
    if (evt.getNewValue() != null) {
      byte value = (byte)evt.getNewValue();
      bufferData.add(value);
      
      if (shift == 1) {
        data += value << 8 & 0xFFFF;
      } else {
        data += value & 0x00FF;
      }
      
      shift--;
      if (shift < 0) {
        reset2BytesData();
      }
      
      if (bufferData.size() >= lineWidth) {
        flushBufferToOutput();
      }
    }
  }
  
  @Override
  public String getEndOfStreamData() {
    flushBufferToOutput();
    
    if (bufferData.size() > 0) {
      Logging.getLogger().warn("Pending data are not displayed (buffer not empty)");
    }

    return super.getEndOfStreamData();
  }
  
  private String flushBufferToOutput() {
    String line = "";
    
    if (data != 0) {
      reset2BytesData();
    }
    
    if (bufferData.size() > 0) {
      line = buildStringFromBytes(bufferData);
      System.out.println("\t" + replaceReturnEndOfLine(line));

      bufferData.clear();
    }
    
    return line;
  }
  
  private void reset2BytesData() {
    System.out.print(StringUtils.toHex(data) + " ");
    System.out.flush();
    
    shift = 1;
    data = 0;
  }

  private String replaceReturnEndOfLine(String data) {
    return data.replace("\r", "\\r")
            .replace("\n", "\\n");
  }
}
