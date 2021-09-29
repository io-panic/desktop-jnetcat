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
package com.ioleak.jnetcat.common;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.function.Function;

public class FileWatcher
        extends TimerTask {

  private final File watchFile;
  private final Function<File, Boolean> executeOnChange;

  private long timestampLastModified;

  public FileWatcher(File file, Function<File, Boolean> method) {
    this.watchFile = file;
    this.timestampLastModified = file.lastModified();
    this.executeOnChange = method;

    LocalDateTime triggerTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampLastModified),
                                                        TimeZone.getDefault().toZoneId());

    if (!watchFile.exists()) {
      Logging.getLogger().warn(String.format("Cannot watch for file (%s) modification: file doesn't exists", watchFile.getAbsolutePath()));
    } else {
      Logging.getLogger().info(String.format("Watching file (%s) for modification (%s)", file.getAbsolutePath(), triggerTime));
    }
  }

  @Override
  public final void run() {
    long timestampCurrent = watchFile.lastModified();

    if (timestampLastModified != timestampCurrent) {
      timestampLastModified = timestampCurrent;
      executeOnChange.apply(watchFile);
    }
  }
}
