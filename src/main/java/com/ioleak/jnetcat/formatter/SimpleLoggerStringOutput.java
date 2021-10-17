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
import java.nio.charset.Charset;

import com.ioleak.jnetcat.common.Logging;
import com.ioleak.jnetcat.common.utils.StringUtils;

public class SimpleLoggerStringOutput
        extends StreamRawReaderNoOutput {

  private final StringBuilder currentData = new StringBuilder();

  @Override
  public void formatDataOutput(PropertyChangeEvent evt) {
    super.formatDataOutput(evt);

    if (evt.getNewValue() != null) {
      currentData.append(new String(new byte[] {(byte) evt.getNewValue()}, Charset.forName("UTF-8")));
    }
  }

  @Override
  public String getEndOfStreamData() {
    String allReadData = currentData.toString();
    if (allReadData.length() > 0) {
      String removeLastCRLF = StringUtils.removeLastCharIfCRLF(allReadData);
      Logging.getLogger().info(String.format("Server data:\n%s", removeLastCRLF));
    }

    currentData.setLength(0);
    super.getEndOfStreamData();

    return allReadData;
  }
}
