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
package com.ioleak.jnetcat.service;

import java.io.File;

import com.ioleak.jnetcat.common.Logging;
import com.ioleak.jnetcat.common.interfaces.ProcessAction;
import com.ioleak.jnetcat.common.utils.JsonUtils;
import com.ioleak.jnetcat.options.JNetcatParameters;
import com.ioleak.jnetcat.service.exception.JNetcatProcessFileNotSetException;
import com.ioleak.jnetcat.service.exception.JNetcatProcessRunningException;

public enum JNetcatProcess
        implements Runnable {

  JNETCATPROCESS;

  private File jsonParamsFile;
  private JNetcatProcessResult resultExecution = JNetcatProcessResult.UNKNOWN;

  private ProcessAction processAction;
  private ExitConsumer<JNetcatProcessResult, JNetcatParameters> exitMethod;

  private JNetcatProcess() {
  }

  public void setJsonParamsFile(File jsonParamsFile) {
    this.jsonParamsFile = jsonParamsFile;
  }

  public void setExitMethod(ExitConsumer<JNetcatProcessResult, JNetcatParameters> exitMethod) {
    this.exitMethod = exitMethod;
  }

  @Override
  public void run() {
    run(getParametersFromFile());
  }

  public void run(JNetcatParameters params) {
    if (getResultExecution() == JNetcatProcessResult.IN_PROGRESS) {
      throw new JNetcatProcessRunningException("An execution is already in progress");
    }

    Logging.getLogger().info("Starting background process...");
    resultExecution = JNetcatProcessResult.IN_PROGRESS;

    if (params != null) {
      processAction = JNetcatProcessFactory.createProcess(params);
      processAction.start();

      resultExecution = processAction.isStateSuccessful() ? JNetcatProcessResult.SUCCESS : JNetcatProcessResult.FAILED;
    } else {
      resultExecution = JNetcatProcessResult.ERROR;
    }

    resultExecution = resultExecution == JNetcatProcessResult.IN_PROGRESS ? JNetcatProcessResult.SUCCESS : resultExecution;
    if (exitMethod != null) {
      exitMethod.accept(resultExecution, params);
    }
  }

  private JNetcatParameters getParametersFromFile() {
    if (jsonParamsFile == null) {
      throw new JNetcatProcessFileNotSetException("File parameters is not set on the object");
    }

    String jsonData = JsonUtils.loadJsonFileToString(jsonParamsFile);
    JNetcatParameters params = JsonUtils.jsonToObject(jsonData, JNetcatParameters.class);

    return params;
  }

  public boolean stopExecutions() {
    boolean haltedSuccess = false;
    if (processAction != null) {
      haltedSuccess = processAction.stopExecutions();
    }

    return haltedSuccess;
  }

  public boolean stopActiveExecution() {
    boolean haltedSuccess = false;
    if (processAction != null) {
      haltedSuccess = processAction.stopActiveExecution();
    }

    return haltedSuccess;
  }

  public JNetcatProcessResult getResultExecution() {
    return resultExecution;
  }
}
