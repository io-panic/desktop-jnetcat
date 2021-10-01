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

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Source: https://github.com/jjenkov/cli-args
 * License: Apache License 2
 * Author: Jakob Jenkov
 */
public class CommandLineArgumentsTest {

  @Test
  public void newCommandLineArguments_NullValue_EmptyObject() {
    CommandLineArguments cliArgs = new CommandLineArguments(null);
    assertEquals(Arrays.asList(""), Arrays.asList(cliArgs.args()), "A null value should generate  an empty array");
  }
  
  @Test
  public void testValueMethods() {
    String[] theArgs = {"firstTarget", "-ha", "midTarget", "-conf", "file1.txt", "file2.txt", "-port", "122", "lastTarget1", "lastTarget2", "-bla"};

    CommandLineArguments cliArgs = new CommandLineArguments(theArgs);

    boolean ha = cliArgs.switchPresent("-ha");
    assertTrue(ha);

    String[] conf = cliArgs.switchValues("-conf");
    assertEquals(2, conf.length);
    assertEquals("file1.txt", conf[0]);
    assertEquals("file2.txt", conf[1]);

    boolean port = cliArgs.switchPresent("-port");
    assertTrue(port);

    String portNo = cliArgs.switchValue("-port");
    assertEquals("122", portNo);

    String defaultValue = "1234";
    String portNo2 = cliArgs.switchValue("-port2", defaultValue);
    assertEquals(defaultValue, portNo2);

    boolean bla = cliArgs.switchPresent("-bla");
    assertTrue(bla);

    String[] targets = cliArgs.targets();
    assertEquals(4, targets.length);
    assertEquals("firstTarget", targets[0]);
    assertEquals("midTarget", targets[1]);
    assertEquals("lastTarget1", targets[2]);
    assertEquals("lastTarget2", targets[3]);
  }
}
