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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcessExecutor {

  public ProcessResult execute() {
    Runtime rt = Runtime.getRuntime();
    String[] commands = {"cmd.exe", "/c", "dir c:"};
    Process proc;
    ProcessResult processResult = new ProcessResult();

    try {
      proc = rt.exec(commands);

      BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
      BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

      String outputLine = null;
      while ((outputLine = stdInput.readLine()) != null) {
        processResult.appendStdIn(String.format("%s%s", outputLine, System.lineSeparator()));
      }

      while ((outputLine = stdError.readLine()) != null) {
        processResult.appendStdErr(String.format("%s%s", outputLine, System.lineSeparator()));
      }

      processResult.setExitValue(proc.waitFor());
    } catch (IOException ex) {
      Logging.getLogger().error("Unable to executable command", ex);
    } catch (InterruptedException ex) {
      Logging.getLogger().error("Process was interrupted", ex);
    }

    return processResult;
  }

  public class ProcessResult {

    private final StringBuilder stdErr = new StringBuilder();
    private final StringBuilder stdIn = new StringBuilder();

    private int exitValue = -1;

    public void appendStdErr(String data) {
      stdErr.append(data);
    }

    public String getStdErr() {
      return stdErr.toString();
    }

    public void appendStdIn(String data) {
      stdIn.append(data);
    }

    public String getStdIn() {
      return stdIn.toString();
    }

    public void setExitValue(int exitValue) {
      this.exitValue = exitValue;
    }

    public int getExitValue() {
      return exitValue;
    }

    @Override
    public String toString() {
      return String.format("Return: %d\nstdIn: %s\nstdErr: %s",
                           getExitValue(), getStdIn(), getStdErr());
    }
  }
}
