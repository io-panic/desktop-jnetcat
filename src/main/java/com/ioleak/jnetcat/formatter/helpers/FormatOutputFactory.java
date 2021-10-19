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
package com.ioleak.jnetcat.formatter.helpers;

import com.ioleak.jnetcat.formatter.PrettyHexStringOutput;
import com.ioleak.jnetcat.formatter.SilentModeFormatOutput;
import com.ioleak.jnetcat.formatter.SimpleLoggerStringOutput;

public class FormatOutputFactory {

  public static StreamFormatOutput createFormatOutput(FormatOutputType formatOutputType, int lineWidth) {
    StreamFormatOutput streamFormatOutput = null;

    switch (FormatOutputType.valueOf(formatOutputType.name())) {
      case NO_OUTPUT:
        streamFormatOutput = new SilentModeFormatOutput();
        break;
      case SIMPLE:
        streamFormatOutput = new SimpleLoggerStringOutput(lineWidth);
        break;
      case PRETTY_HEX:
        streamFormatOutput = new PrettyHexStringOutput(null, lineWidth);
        break;
    }

    return streamFormatOutput;
  }
}
