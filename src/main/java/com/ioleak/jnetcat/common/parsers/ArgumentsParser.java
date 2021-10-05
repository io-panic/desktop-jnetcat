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
package com.ioleak.jnetcat.common.parsers;

import com.ioleak.jnetcat.common.parsers.exception.UnknownArgumentParserException;
import com.ioleak.jnetcat.common.parsers.exception.ParserNotInitializatedException;
import com.ioleak.jnetcat.common.parsers.exception.IllegalArgumentParserException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Source: https://github.com/jjenkov/cli-args
 * License: Apache License 2
 * Author: Jakob Jenkov
 */
public class ArgumentsParser {

  public static final String SWITCH_HELP = "-h";
  public static final String SWITCH_HELP_DESCRIPTION = "Display complete informations about the parameters";

  private final Map<String, ArrayList<String>> argumentsMap = new HashMap<>();

  private final String version;
  private final String description;
  private final Map<String, String> parameters = new HashMap<>();

  private List<String> arguments = null;

  public ArgumentsParser(String version, String description, Map<String, String> parameters) {
    this.version = version;
    this.description = description;

    this.parameters.put(SWITCH_HELP, SWITCH_HELP_DESCRIPTION);
    this.parameters.putAll(parameters);
  }

  public String getHelpMessage() {
    String message = String.format("%s\n\tVersion: %s\n",
                                   description, version == null ? "N/A" : version);

    message = parameters.keySet().stream().map(key
            -> String.format("\n%5s -> %s", key, parameters.get(key))).reduce(message, String::concat);

    return message;
  }

  public void setArguments(String[] arguments) {
    this.arguments = Arrays.asList(arguments == null ? new String[] {} : arguments);
    parseArguments();
  }

  private void parseArguments() {
    argumentsMap.clear();

    String lastArgument = null;
    for (String argument : this.arguments) {
      if (argument.startsWith("-")) {
        if (parameters.get(argument) == null) {
          throw new UnknownArgumentParserException(String.format("Argument [%s] cannot be found", argument));
        }

        argumentsMap.put(argument, new ArrayList<>());
        lastArgument = argument;
      } else {
        if (lastArgument == null) {
          throw new IllegalArgumentParserException(String.format("Arguments must match <-key value> pattern. Option '%s' don't match", argument));
        }

        argumentsMap.get(lastArgument).add(argument);
      }
    }
  }

  public boolean switchPresent(String switchName) {
    return argumentsMap.containsKey(switchName);
  }

  public String switchValue(String switchName) {
    return getStringValue(switchName);
  }

  public String switchValue(String switchName, String defaultValue) {
    String value = getStringValue(switchName);
    return value.isBlank() ? defaultValue : value;
  }

  public long switchLongValue(String switchName) {
    String longValue = getStringValue(switchName);
    return Long.parseLong(longValue.isEmpty() ? "0" : longValue);
  }

  public long switchLongValue(String switchName, long defaultValue) {
    String longValue = switchValue(switchName, String.valueOf(defaultValue));
    return Long.parseLong(longValue.isEmpty() ? "0" : longValue);
  }

  public double switchDoubleValue(String switchName) {
    String longValue = getStringValue(switchName);
    return Double.parseDouble(longValue.isEmpty() ? "0" : longValue);
  }

  public double switchDoubleValue(String switchName, double defaultValue) {
    String longValue = switchValue(switchName, String.valueOf(defaultValue));
    return Double.parseDouble(longValue.isEmpty() ? "0" : longValue);
  }

  public List<String> switchValues(String switchName) {
    if (arguments == null) {
      throw new ParserNotInitializatedException("No argument is associated to the parser");
    }

    List<String> values = argumentsMap.get(switchName);

    if (values == null) {
      values = new ArrayList<>();
    }

    return values;
  }

  private String getStringValue(String switchName) {
    String value = "";

    if (switchValues(switchName).size() > 0) {
      value = switchValues(switchName).get(0);
    }

    return value.trim();
  }
}
