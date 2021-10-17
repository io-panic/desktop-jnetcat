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

import java.nio.charset.Charset;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

  public static final Charset DEFAULT_ENCODING_NETWORK = Charset.forName("ISO-8859-1"); // 8 bits

  private static final int DEFAULT_STRINGBUILDER_CAPACITY = 250;
  private static final int NB_CHAR_BETWEEN_LINES = 50;

  private static final String IPv4_REGEX_VALIDATION = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";
  private static final Pattern pattern = Pattern.compile(IPv4_REGEX_VALIDATION);

  private StringUtils() {
  }

  public static String toHex(int value) {
    return String.format("%04X", value);
  }

  public static String toHex(String stringToHex) {
    if (stringToHex == null || stringToHex.isEmpty()) {
      return "";
    }

    StringBuilder buf = new StringBuilder(DEFAULT_STRINGBUILDER_CAPACITY);
    byte[] stringBytes = getBytesFromString(stringToHex);
    int data = 0;
    int shift = 1;

    for (byte ch : stringBytes) {
      ch = (byte) (ch & 0xFF);

      if (shift == 1) {
        data += ch << 8 & 0xFFFF;
      } else {
        data += ch & 0x00FF;
      }

      shift--;
      if (shift < 0) {
        buf.append(toHex(data));

        shift = 1;
        data = 0;
      }
    }

    if (data != 0) {
      buf.append(toHex(data));
    }

    return buf.toString();
  }

  public static String getStringFromBytes(List<Byte> data) {
    byte[] bytes = new byte[data.size()];
    for (int i = 0; i < data.size(); i++) {
      bytes[i] = (byte) (data.get(i) & 0xFF);
    }

    return new String(bytes, DEFAULT_ENCODING_NETWORK);
  }

  public static byte[] getBytesFromString(String data) {
    return data.getBytes(DEFAULT_ENCODING_NETWORK);
  }

  public static String removeLastCharIfCRLF(final String stringToClean) {
    boolean lastCharCRLF = false;
    String cleanedString = "";

    if (stringToClean != null && !stringToClean.isEmpty()) {
      cleanedString = stringToClean;
      
      do {
        int length = cleanedString.length() - 1;
        lastCharCRLF = (length >= 0);
        if (lastCharCRLF) {
          byte lastChar = (byte) cleanedString.charAt(length);
          lastCharCRLF = (lastChar == 10 || lastChar == 13);
          if (lastCharCRLF) {
            cleanedString = cleanedString.substring(0, length);
          }
        }
      } while (lastCharCRLF);
    }

    return cleanedString;
  }

  public static String toHexWithSpaceSeparator(String stringToHex) {
    stringToHex = toHex(stringToHex);
    return String.join(" ", stringToHex.split("(?<=\\G.{4})"));
  }

  public static String toStringWithLineSeparator(String stringToLineSeparator) {
    if (stringToLineSeparator == null) {
      return "";
    }

    return String.join("\n", stringToLineSeparator.split(String.format("(?<=\\G.{%d})", NB_CHAR_BETWEEN_LINES)));
  }

  public static boolean isStringContainsIPv4(String ipv4) {
    Matcher matcher = pattern.matcher(ipv4);
    return matcher.matches();
  }

  public static boolean isNullOrEmpty(String string) {
    return string == null || string.trim().isEmpty();
  }

  public static String generateRandomString(int length) {
    int zeroNumeralIntValue = 48; // numeral '0'
    int zLetterIntValue = 122; // letter 'z'

    if (length < 0) {
      length = 0;
    }

    return new Random().ints(zeroNumeralIntValue, zLetterIntValue + 1)
            .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
            .limit(length)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
  }
}
