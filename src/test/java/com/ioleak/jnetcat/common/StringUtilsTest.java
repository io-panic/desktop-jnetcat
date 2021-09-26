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

import com.ioleak.jnetcat.common.utils.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class StringUtilsTest {
  @Test
  public void toHexSpaceSeparatorWithNullInput() {
    assertTrue(StringUtils.toHexWithSpaceSeparator(null).equals(""));
  }

  @Test
  public void toHexSpaceSeparatorWithEmptyInput() {
    assertTrue(StringUtils.toHexWithSpaceSeparator("").equals(""));
  }
    
  @Test
  public void toHexSpaceSeparatorWithValidInput() { 
    assertTrue(StringUtils.toHexWithSpaceSeparator("Hello to you !\n").equals("48 65 6C 6C 6F 20 74 6F 20 79 6F 75 20 21 0A"));
    assertTrue(StringUtils.toHexWithSpaceSeparator("\n").equals("0A"));
    assertTrue(StringUtils.toHexWithSpaceSeparator("\n 11").equals("0A 20 31 31"));
  }
  
  @Test
  public void toStringWithLineSeparatorNullInput() {
    assertTrue(StringUtils.toStringWithLineSeparator(null).equals(""));
  }

  @Test
  public void toStringWithLineSeparatorEmptyInput() {
    assertTrue(StringUtils.toStringWithLineSeparator("").equals(""));
  }
    
  @Test
  public void toStringWithLineSeparatorValidInput() {
    String stringToSplit = "XX012345012345012345012345012345012345012345012345XX234501234501234501234501234501234501234501234501XX2345012345012345012345 0123450123450123450123450XX12345";

    assertTrue(StringUtils.toStringWithLineSeparator(stringToSplit).equals("XX012345012345012345012345012345012345012345012345\n" +
            "XX234501234501234501234501234501234501234501234501\n" +
            "XX2345012345012345012345 0123450123450123450123450\n" +
            "XX12345"));
  } 
  
  @Test
  public void stringIsNullOrEmpty() {
    assertTrue(StringUtils.isNullOrEmpty(""));
    assertTrue(StringUtils.isNullOrEmpty(null));
    assertTrue(StringUtils.isNullOrEmpty(" "));
    assertFalse(StringUtils.isNullOrEmpty("ABCDEF"));
    assertFalse(StringUtils.isNullOrEmpty("0"));
  }
  
  @Test
  public void verifyStringContainsIpv4() {
    assertTrue(StringUtils.isStringContainsIPv4("236.65.43.33"));
    assertTrue(StringUtils.isStringContainsIPv4("0.0.0.0"));
    assertTrue(StringUtils.isStringContainsIPv4("255.255.255.255"));
    assertFalse(StringUtils.isStringContainsIPv4("256.255.255.255"));  
    assertFalse(StringUtils.isStringContainsIPv4("-25.25.25.25"));  
    assertFalse(StringUtils.isStringContainsIPv4("255:255:255:255")); 
  }
}
