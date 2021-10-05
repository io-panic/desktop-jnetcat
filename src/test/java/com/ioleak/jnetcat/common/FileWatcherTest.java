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
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.ioleak.jnetcat.common.utils.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileWatcherTest {

  private static final File TEMP_FILE = new File(String.format("%s.txt", StringUtils.generateRandomString(8)));
  private static final int ADD_DELAY_TO_MODIFY_FILE_DATE = 1250;

  FileWatcher fileWatcher;
  private boolean fileWasUpdated;

  @BeforeEach
  public void setUp() {
    fileWasUpdated = false;
    fileWatcher = new FileWatcher(TEMP_FILE, (file) -> fileWasUpdated = true);
  }

  @AfterEach
  public void tearDown() {
    if (TEMP_FILE.exists() && !TEMP_FILE.delete()) {
      Logging.getLogger().warn("An error occured while trying to delete file: %s", TEMP_FILE.getAbsolutePath());
    }
  }

  @Test
  public void fileWatcher_InitWhenFileExists_ShouldExecute() {
    createFile(TEMP_FILE);
    fileWatcher = new FileWatcher(TEMP_FILE, (file) -> fileWasUpdated = true);

    fileWatcher.run();
    assertTrue(fileWasUpdated, "Execution on first run should occurs (lastModified date is different");
  }

  @Test
  public void fileWatcher_InitWhenFileDontExists_ShouldExecute() {
    createFile(TEMP_FILE);

    fileWatcher.run();
    assertTrue(fileWasUpdated, "Execution on first run should occurs (lastModified date is different");
  }

  @Test
  public void fileWatcher_FileModified_ChangeDetected() {
    createFile(TEMP_FILE);

    fileWatcher.run();
    assertTrue(fileWasUpdated, "Execution on first run should occurs (lastModified date was -1)");

    try {
      Files.write(TEMP_FILE.toPath(), "one line to modify".getBytes(Charset.forName("UTF-8")), StandardOpenOption.APPEND);
    } catch (IOException ex) {
      Logging.getLogger().error(String.format("An error occured while trying to write in file %s", TEMP_FILE.getAbsolutePath()));
    }

    fileWatcher.run();
    assertTrue(fileWasUpdated, "File modification should have occured but no execution of the method occurred");
  }

  @Test
  public void fileWatcher_FileDeletedThenCreated_MethodExecuted() {
    createFile(TEMP_FILE);

    fileWatcher.run();
    assertTrue(fileWasUpdated, "Execution on first run should occurs");

    fileWasUpdated = false;
    assertTrue(TEMP_FILE.delete());
    fileWatcher.run();
    assertFalse(fileWasUpdated);

    createFile(TEMP_FILE);
    TEMP_FILE.setLastModified(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli());

    fileWatcher.run();
    assertTrue(fileWasUpdated);
  }

  private boolean createFile(File file) {
    boolean fileCreated = false;

    try {
      if (!file.exists()) {
        fileCreated = file.createNewFile();
      }
    } catch (IOException ex) {
      Logging.getLogger().error(String.format("Unable to create file: %s", file), ex);
    }

    assertEquals(true, fileCreated, "The file must be created and exists to detect change");

    return fileCreated;
  }
}
