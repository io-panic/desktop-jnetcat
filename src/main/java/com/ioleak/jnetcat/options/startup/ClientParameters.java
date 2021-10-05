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
package com.ioleak.jnetcat.options.startup;

import com.ioleak.jnetcat.common.BaseObject;
import com.ioleak.jnetcat.common.utils.StringUtils;
import com.ioleak.jnetcat.options.exception.IPv4InvalidArgumentException;
import com.ioleak.jnetcat.options.exception.PortInvalidArgumentException;

public abstract class ClientParameters
        extends BaseObject {

  private final String ip;
  private final Integer port;

  private final int soTimeout;

  private final int nbClientMax;
  private final int nbExecution;
  private final int sleepBetweenExecMs;

  abstract static class ParametersBuilder<T extends ParametersBuilder> {

    private int port = -1;
    private String ip = "0.0.0.0";
    private int soTimeout = 0;

    private int nbClientMax = 1;
    private int nbExecution = 1;
    private int sleepBetweenExecMs = 1000;

    abstract ClientParameters build();

    protected abstract T self();

    public ParametersBuilder(String ip, int port) {
      this.ip = ip;
      this.port = port;
    }

    public T withSoTimeout(int soTimeout) {
      this.soTimeout = soTimeout;
      return self();
    }

    public T withNbClientMax(int nbClientMax) {
      this.nbClientMax = nbClientMax;
      return self();
    }

    public T withNbExecution(int nbClientMax) {
      this.nbClientMax = nbClientMax;
      return self();
    }

    public T withSleepBetweenExecMs(int sleepBetweenExecMs) {
      this.sleepBetweenExecMs = sleepBetweenExecMs;
      return self();
    }
  }

  ClientParameters(ParametersBuilder<?> builder) {
    if (!StringUtils.isNullOrEmpty(builder.ip) && !StringUtils.isStringContainsIPv4(builder.ip)) {
      throw new IPv4InvalidArgumentException("IP must use a standard IPv4 format (0.0.0.0)");
    }

    if (builder.port < 1 || builder.port > 65535) {
      throw new PortInvalidArgumentException("Port range is not valid. You must specify a port between 1 and 65535");
    }

    this.ip = builder.ip;
    this.port = builder.port;
    this.soTimeout = builder.soTimeout;
    this.nbClientMax = builder.nbClientMax;
    this.nbExecution = builder.nbExecution;
    this.sleepBetweenExecMs = builder.sleepBetweenExecMs;
  }

  public String getIp() {
    return ip;
  }

  public Integer getPort() {
    return port;
  }

  public int getSoTimeout() {
    return soTimeout;
  }

  public int getNbClientMax() {
    return nbClientMax;
  }

  public int getNbExecution() {
    return nbExecution;
  }

  public int getSleepBetweenExecMs() {
    return sleepBetweenExecMs;
  }
}
