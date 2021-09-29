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
package com.ioleak.jnetcat;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;

import com.ioleak.jnetcat.common.FileWatcher;
import com.ioleak.jnetcat.common.Logging;
import com.ioleak.jnetcat.common.arguments.CommandLineArguments;
import com.ioleak.jnetcat.common.utils.JsonUtils;
import com.ioleak.jnetcat.server.console.KeyCharReader;

public class JNetcat {

  private static Thread jnetcatThread;
  private static JNetcatProcess jnetcatRun;

  public static void main(String... args) {
    String jsonParameters = "external/conf/AllOptionsInOne.json";
    CommandLineArguments cliArgs = new CommandLineArguments(args);
    int returnCode = 0;

    if (cliArgs.switchPresent("-f")) {
      jsonParameters = cliArgs.switchValue("-f");
    }

    URL url = JsonUtils.getRelativePath(jsonParameters, JNetcat.class);

    try {
      File jsonFile = new File(url.toURI());

      FileWatcher fileWatcher = new FileWatcher(jsonFile, JNetcat::paramsHotReload);
      Timer timer = new Timer();
      timer.schedule(fileWatcher, new Date(), 1000);

      jnetcatRun = new JNetcatProcess(jsonFile);
      jnetcatThread = new Thread(jnetcatRun);
      jnetcatThread.start();

      KeyCharReader keyCharReader = new KeyCharReader(jnetcatRun::stopActiveExecution, jnetcatRun::stopExecutions);
      keyCharReader.run();

      final CountDownLatch latch = new CountDownLatch(1);
      try {
        latch.await();
      } catch (InterruptedException ex) {
        Logging.getLogger().error("Execution interrupted. Program will exit.", ex);
      }
    } catch (URISyntaxException ex) {
      Logging.getLogger().error("Cannot start execution, invalid URI", ex);
    }

    System.exit(returnCode);
  }

  private static boolean paramsHotReload(File jsonParamsFile) {
    Logging.getLogger().info(String.format("File (%s) modified: trying to hot reload...", jsonParamsFile.getAbsolutePath()));

    if (jnetcatThread != null) {
      jnetcatRun.stopExecutions();
      jnetcatThread.interrupt();

      try {
        jnetcatThread.join();
      } catch (InterruptedException ex) {
        Logging.getLogger().warn("A running thread was interrupted (and it was expected to stop: this message is informative only)");
      }

      Logging.getLogger().warn(String.format("Existing thread stopped with return code: %d", jnetcatRun.getResultExecution()));
    }

    jnetcatThread = new Thread(jnetcatRun);
    jnetcatThread.start();

    return true;
  }
}
