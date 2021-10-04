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
package com.ioleak.jnetcat.server.console;

import java.beans.PropertyChangeEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(Lifecycle.PER_CLASS)
public class KeyCharReaderTest {

  private PropertyChangeEvent event;
  private KeyCharReader keyCharReader;

  private boolean stopped = false;
  private StringBuilder stringEventReceived;

  private boolean hitKeyS = false;
  private boolean hitKeyQ = false;

  
  @BeforeEach
  public void setUp() {
    keyCharReader = new KeyCharReader(this::hitKeyS, this::hitKeyQ);
    stringEventReceived = new StringBuilder();

    keyCharReader.addListener((PropertyChangeEvent propertyChangeEvent) -> {
      this.event = propertyChangeEvent;
      stringEventReceived.append(propertyChangeEvent.getNewValue());
    });

    event = null;
  }

  @Test
  public void readChar_HitKey_FirePropertyChange() throws IOException, InterruptedException {
    System.setIn(new ByteArrayInputStream("abcd TesT #1 *?".getBytes()));
    keyCharReader.readChar();

    assertTrue(event != null);
    assertEquals("abcd TesT #1 *?", stringEventReceived.toString());
  }

  @Test
  public void readChar_HitKeyQ_FunctionExecuted() throws IOException, InterruptedException {
    System.setIn(new ByteArrayInputStream("q".getBytes()));
    keyCharReader.readChar();

    assertTrue(event != null);
    assertEquals("q", stringEventReceived.toString());
    assertTrue(hitKeyQ);
  }

  @Test
  public void readChar_HitKeyS_FunctionExecuted() throws IOException, InterruptedException {
    System.setIn(new ByteArrayInputStream("s".getBytes()));
    keyCharReader.readChar();

    assertTrue(event != null);
    assertEquals("s", stringEventReceived.toString());
    assertTrue(hitKeyS);
  }

  @Test
  public void readChar_HitKeyK_ThrowsException() throws IOException, InterruptedException {
    System.setIn(new ByteArrayInputStream("k".getBytes()));
    assertThrows(HitKeyCloseCharReaderException.class, () -> keyCharReader.readChar());
  }

  private boolean hitKeyS() {
    hitKeyS = true;
    return hitKeyS;
  }

  private boolean hitKeyQ() {
    hitKeyQ = true;
    return hitKeyQ;
  }
}
