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
package com.ioleak.jnetcat.common.properties;

import java.beans.PropertyChangeEvent;

import com.ioleak.jnetcat.common.utils.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ObjectPropertyTest {

  private PropertyChangeEvent event;
  private ObjectProperty<String> objectProperty;

  @BeforeEach
  public void setUp() {
    objectProperty = new ObjectProperty<>();
    objectProperty.addListener((PropertyChangeEvent propertyChangeEvent) -> {
      this.event = propertyChangeEvent;
    });

    event = null;
  }

  @Test
  public void setObject_ValueToEmpty_ValueWithEventFired() {
    objectProperty.set("One Value");

    assertTrue(event != null);
    assertTrue(StringUtils.isNullOrEmpty((String) event.getOldValue()));
    assertEquals("One Value", event.getNewValue());
    assertEquals("One Value", objectProperty.get());
  }

  @Test
  public void setObject_ExistingPreviousValue_ValueWithEventFired() {
    objectProperty.set("One Value");
    objectProperty.set("New Value");

    assertTrue(event != null);
    assertEquals("One Value", event.getOldValue());
    assertEquals("New Value", event.getNewValue());
    assertEquals("New Value", objectProperty.get());
  }
}
