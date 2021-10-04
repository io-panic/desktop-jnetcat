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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArgumentsParserTest {

  ArgumentsParser argumentsParser;

  @BeforeEach
  public void setUp() {
    Map<String, String> parameters = new HashMap<>();
    parameters.put("-f", "Set the configuration file");
    parameters.put("-d", "Enter a numerical value");
    parameters.put("-v", "Verbose parameter (first level)");

    argumentsParser = new ArgumentsParser("1.0.0", "Network debug tool for client/server", parameters);
  }

  @Test
  public void setArguments_UseValueBeforeArgumentSet_ExceptionThrown() {
    assertThrows(ParserNotInitializatedException.class, () -> argumentsParser.switchValue("-f"));
  }

  @Test
  public void switchPresent_EmptyOrNullValue_SwitchNotPresent() {
    argumentsParser.setArguments(null);
    assertFalse(argumentsParser.switchPresent("-f"));

    argumentsParser.setArguments(new String[]{});
    assertFalse(argumentsParser.switchPresent("-f"));

    assertEquals("file1.txt", argumentsParser.switchValue("-f", "file1.txt"),
                 "default value should be returned even if switch not present");
  }

  @Test
  public void switchValue_EmptyValue_SwitchPresentWithDefault() {
    argumentsParser.setArguments(new String[]{"-f"});
    boolean fileSwitch = argumentsParser.switchPresent("-f");

    assertTrue(fileSwitch, "Switch should be present");
    assertTrue(argumentsParser.switchValue("-f").isBlank(), "Switch should not contain any value");
    assertEquals("test.txt", argumentsParser.switchValue("-f", "test.txt"));
  }

  @Test
  public void switchValue_EmptyValue_SwitchPresentWithValue() {
    argumentsParser.setArguments(new String[]{"-f", "file1.txt"});
    boolean fileSwitch = argumentsParser.switchPresent("-f");

    assertTrue(fileSwitch, "Switch should be present");
    assertEquals("file1.txt", argumentsParser.switchValue("-f", "file2.txt"));
    assertEquals("file1.txt", argumentsParser.switchValue("-f"));
  }

  @Test
  public void switchLongValue_EmptyValue_SwitchPresentWithEmpty() {
    argumentsParser.setArguments(new String[]{"-d"});
    boolean numSwitch = argumentsParser.switchPresent("-d");

    assertTrue(numSwitch, "Switch should be present");
    assertEquals(0, argumentsParser.switchLongValue("-d"), "Switch should not contain any value");
    assertEquals(256, argumentsParser.switchLongValue("-f", 256));
  }

  @Test
  public void switchLongValue_ProvideValue_SwitchPresentWithValue() {
    argumentsParser.setArguments(new String[]{"-d", "512"});
    boolean fileSwitch = argumentsParser.switchPresent("-d");

    assertTrue(fileSwitch, "Switch should be present");
    assertEquals(512, argumentsParser.switchLongValue("-d", 256));
    assertEquals(512, argumentsParser.switchLongValue("-d"));
  }

  @Test
  public void switchDoubleValue_EmptyValue_SwitchPresentWithEmpty() {
    argumentsParser.setArguments(new String[]{"-d"});
    boolean numSwitch = argumentsParser.switchPresent("-d");

    assertTrue(numSwitch, "Switch should be present");
    assertEquals(0, argumentsParser.switchDoubleValue("-d"), "Switch should not contain any value");
    assertEquals(756.29, argumentsParser.switchDoubleValue("-f", 756.29));
  }

  @Test
  public void switchDoubleValue_ProvideValue_SwitchPresentWithValue() {
    argumentsParser.setArguments(new String[]{"-d", "756.29"});
    boolean fileSwitch = argumentsParser.switchPresent("-d");

    assertTrue(fileSwitch, "Switch should be present");
    assertEquals(756.29, argumentsParser.switchDoubleValue("-d", 256.12));
    assertEquals(756.29, argumentsParser.switchDoubleValue("-d"));
  }

  @Test
  public void switchValues_DoubleString_ArraySize2() {
    argumentsParser.setArguments(new String[]{"-f", "file1.txt", "file2.txt"});
    boolean fileSwitch = argumentsParser.switchPresent("-f");

    assertTrue(fileSwitch, "Switch should be present");
    assertEquals(2, argumentsParser.switchValues("-f").size());
  }

  @Test
  public void switchValues_EmptyString_ArraySizeEmpty() {
    argumentsParser.setArguments(new String[]{"-f"});
    boolean fileSwitch = argumentsParser.switchPresent("-f");

    assertTrue(fileSwitch, "Switch should be present");
    assertTrue(argumentsParser.switchValues("-f").isEmpty());
    assertTrue(argumentsParser.switchValue("-f").isEmpty());
  }

  @Test
  public void setArguments_UnknownParameter_ExceptionThrown() {
    assertThrows(UnknownArgumentParserException.class, () -> argumentsParser.setArguments(new String[]{"-i"}));
  }

  @Test
  public void setArguments_OptionNoMatch_ExceptionThrown() {
    assertThrows(IllegalArgumentParserException.class, () -> argumentsParser.setArguments(new String[]{"wrongArg"}));
    assertThrows(IllegalArgumentParserException.class, () -> argumentsParser.setArguments(new String[]{"wrongArg", "-f", "test.txt"}));
  }
}
