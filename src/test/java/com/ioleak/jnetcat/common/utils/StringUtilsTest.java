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
package com.ioleak.jnetcat.common.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StringUtilsTest {

  @Test
  public void toHexWithSpaceSeparator_NullInput_Empty() {
    assertTrue(StringUtils.toHexWithSpaceSeparator(null).isEmpty());
  }

  @Test
  public void toHexWithSpaceSeparator_EmptyInput_Empty() {
    assertTrue(StringUtils.toHexWithSpaceSeparator("").isEmpty());
  }

  @Test
  public void toHexWithSpaceSeparator_ValidInput_AsExpected() {
    assertEquals("48 65 6C 6C 6F 20 74 6F 20 79 6F 75 20 21 0A", StringUtils.toHexWithSpaceSeparator("Hello to you !\n"));
    assertEquals("0A", StringUtils.toHexWithSpaceSeparator("\n"));
    assertEquals("0A 20 31 31", StringUtils.toHexWithSpaceSeparator("\n 11"));
  }

  @Test
  public void toStringWithLineSeparator_NullValue_Empty() {
    assertTrue(StringUtils.toStringWithLineSeparator(null).isEmpty());
  }

  @Test
  public void toStringWithLineSeparator_EmptyValue_Empty() {
    assertTrue(StringUtils.toStringWithLineSeparator("").isEmpty());
  }

  @Test
  public void toStringWithLineSeparator_ValidValue_AsExpected() {
    String stringToSplit = "XX012345012345012345012345012345012345012345012345XX234501234501234501234501234501234501234501234501XX2345012345012345012345 0123450123450123450123450XX12345";

    assertEquals("XX012345012345012345012345012345012345012345012345\n"
                 + "XX234501234501234501234501234501234501234501234501\n"
                 + "XX2345012345012345012345 0123450123450123450123450\n"
                 + "XX12345", StringUtils.toStringWithLineSeparator(stringToSplit));
  }

  @Test
  public void isNullOrEmpty_Values_AsExpected() {
    assertTrue(StringUtils.isNullOrEmpty(""));
    assertTrue(StringUtils.isNullOrEmpty(null));
    assertTrue(StringUtils.isNullOrEmpty(" "));
    assertFalse(StringUtils.isNullOrEmpty("ABCDEF"));
    assertFalse(StringUtils.isNullOrEmpty("0"));
  }

  @Test
  public void isStringContainsIPv4_Values_AsExpected() {
    assertTrue(StringUtils.isStringContainsIPv4("236.65.43.33"));
    assertTrue(StringUtils.isStringContainsIPv4("0.0.0.0"));
    assertTrue(StringUtils.isStringContainsIPv4("255.255.255.255"));
    assertFalse(StringUtils.isStringContainsIPv4("256.255.255.255"));
    assertFalse(StringUtils.isStringContainsIPv4("-25.25.25.25"));
    assertFalse(StringUtils.isStringContainsIPv4("255:255:255:255"));
  }

  @Test
  public void generateRandomString_LengthOutOfBound_AsExpected() {
    assertTrue(StringUtils.generateRandomString(-64).length() == 0);   
    assertTrue(StringUtils.generateRandomString(-10).length() == 0);    
    assertTrue(StringUtils.generateRandomString(0).length() == 0);
  }
  
  @Test
  public void generateRandomString_LengthVaria_AsExpected() {
    assertTrue(StringUtils.generateRandomString(10).length() == 10);
    assertTrue(StringUtils.generateRandomString(1).length() == 1);
    assertTrue(StringUtils.generateRandomString(15).length() == 15);
    assertTrue(StringUtils.generateRandomString(128).length() == 128);
  }
}
