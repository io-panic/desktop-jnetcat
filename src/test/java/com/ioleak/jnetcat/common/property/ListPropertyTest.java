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
package com.ioleak.jnetcat.common.property;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;

import com.ioleak.jnetcat.common.utils.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListPropertyTest {

  private PropertyChangeEvent event;
  private ListProperty<String> listProperty;

  @BeforeEach
  public void initListProperty() {
    listProperty = new ListProperty<>(new ArrayList<>(Arrays.asList("A", "B", "C", "D")));
    listProperty.addListener((PropertyChangeEvent propertyChangeEvent) -> {
      this.event = propertyChangeEvent;
    });

    event = null;
  }

  @Test
  public void add_Value_ExpectValue() {
    listProperty.add("E");

    assertTrue(event != null);
    assertTrue(StringUtils.isNullOrEmpty((String) event.getOldValue()));
    assertEquals("E", event.getNewValue());
  }

  @Test
  public void remove_ValueDontExists_NoEventFired() {
    listProperty.remove("E");

    assertTrue(event == null);
  }

  @Test
  public void remove_ExistingValue_ValueRemoved() {
    listProperty.remove("D");

    assertTrue(event != null);
    assertEquals("D", event.getOldValue());
    assertTrue(StringUtils.isNullOrEmpty((String) event.getNewValue()));
  }

  @Test
  public void addAll_ExistingAndNewValues_ValuesAdded() {
    listProperty.addAll(Arrays.asList("D", "E", "F"));

    assertTrue(event != null);
    assertEquals(Arrays.asList("D", "E", "F"), event.getOldValue());
    assertEquals(Arrays.asList("A", "B", "C", "D", "D", "E", "F"), event.getNewValue());
  }

  @Test
  public void addAll_ExistingAndNewValuesAtPosition_ValuesAdded() {
    listProperty.addAll(2, Arrays.asList("D", "E", "F"));

    assertTrue(event != null);
    assertEquals(Arrays.asList("D", "E", "F"), event.getOldValue());
    assertEquals(Arrays.asList("A", "B", "D", "E", "F", "C", "D"), event.getNewValue());
  }

  @Test
  public void removeAll_Values_Removed() {
    listProperty.removeAll(Arrays.asList("D", "E", "F"));

    assertTrue(event != null);
    assertEquals(Arrays.asList("D", "E", "F"), event.getOldValue());
    assertEquals(Arrays.asList("A", "B", "C"), event.getNewValue());
  }

  @Test
  public void removeAll_NoValuesExists_NoEventFired() {
    listProperty.removeAll(Arrays.asList("E", "F"));

    assertTrue(event == null);
  }
}
