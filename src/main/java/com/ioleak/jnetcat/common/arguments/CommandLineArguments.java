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
package com.ioleak.jnetcat.common.arguments;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Source: https://github.com/jjenkov/cli-args
 * License: Apache License 2
 * Author: Jakob Jenkov
 */
public class CommandLineArguments {

  private String[] args = null;

  private final Map<String, Integer> switchIndexes = new HashMap<String, Integer>();
  private final Set<Integer> takenIndexes = new TreeSet<Integer>();

  public CommandLineArguments(String[] args) {
    parse(args);
  }

  public final void parse(String[] arguments) {
    this.args = arguments;
    //locate switches.
    switchIndexes.clear();
    takenIndexes.clear();
    for (int i = 0; i < args.length; i++) {
      if (args[i].startsWith("-")) {
        switchIndexes.put(args[i], i);
        takenIndexes.add(i);
      }
    }
  }

  public String[] args() {
    return args;
  }

  public String arg(int index) {
    return args[index];
  }

  public boolean switchPresent(String switchName) {
    return switchIndexes.containsKey(switchName);
  }

  public String switchValue(String switchName) {
    return switchValue(switchName, null);
  }

  public String switchValue(String switchName, String defaultValue) {
    if (!switchIndexes.containsKey(switchName)) {
      return defaultValue;
    }

    int switchIndex = switchIndexes.get(switchName);
    if (switchIndex + 1 < args.length) {
      takenIndexes.add(switchIndex + 1);
      return args[switchIndex + 1];
    }
    return defaultValue;
  }

  public Long switchLongValue(String switchName) {
    return switchLongValue(switchName, null);
  }

  public Long switchLongValue(String switchName, Long defaultValue) {
    String switchValue = switchValue(switchName, null);

    if (switchValue == null) {
      return defaultValue;
    }
    return Long.parseLong(switchValue);
  }

  public Double switchDoubleValue(String switchName) {
    return switchDoubleValue(switchName, null);
  }

  public Double switchDoubleValue(String switchName, Double defaultValue) {
    String switchValue = switchValue(switchName, null);

    if (switchValue == null) {
      return defaultValue;
    }
    return Double.parseDouble(switchValue);
  }

  public String[] switchValues(String switchName) {
    if (!switchIndexes.containsKey(switchName)) {
      return new String[0];
    }

    int switchIndex = switchIndexes.get(switchName);

    int nextArgIndex = switchIndex + 1;
    while (nextArgIndex < args.length && !args[nextArgIndex].startsWith("-")) {
      takenIndexes.add(nextArgIndex);
      nextArgIndex++;
    }

    String[] values = new String[nextArgIndex - switchIndex - 1];
    for (int j = 0; j < values.length; j++) {
      values[j] = args[switchIndex + j + 1];
    }
    return values;
  }

  public String[] targets() {
    String[] targetArray = new String[args.length - takenIndexes.size()];
    int targetIndex = 0;
    for (int i = 0; i < args.length; i++) {
      if (!takenIndexes.contains(i)) {
        targetArray[targetIndex++] = args[i];
      }
    }

    return targetArray;
  }

}
