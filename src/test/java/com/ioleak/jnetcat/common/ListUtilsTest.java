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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ioleak.jnetcat.common.utils.ListUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListUtilsTest {
  private static final List<String> list_4items = Arrays.asList("A", "B", "C", "D");
  private static final List<String> list_5items = Arrays.asList("C", "D", "E", "F", "G");

  @Test
  public void unionWithNullValues() {
    List<String> result = ListUtils.union(null, null);
    assertTrue(result.isEmpty());
  }

  @Test
  public void unionWithLeftNullValues() {
    List<String> result = ListUtils.union(null, list_5items);
    assertTrue(result.equals(Arrays.asList("C", "D", "E", "F", "G")));
  }

  @Test
  public void unionWithRightNullValues() {
    List<String> result = ListUtils.union(list_4items, null);
    assertTrue(result.equals(Arrays.asList("A", "B", "C", "D")));
  }

  @Test
  public void unionWithValidValues() {
    assertTrue(ListUtils.union(list_4items, list_5items).equals(Arrays.asList("A", "B", "C", "D", "E", "F", "G")));
    assertTrue(ListUtils.complement(new ArrayList<String>(), new ArrayList<>()).equals(Arrays.asList()));
  }

  @Test
  public void intersectionWithNullValues() {
    List<String> result = ListUtils.intersection(null, null);
    assertTrue(result.isEmpty());
  }

  @Test
  public void intersectionWithLeftNullValues() {
    List<String> result = ListUtils.intersection(list_4items, null);
    assertTrue(result.isEmpty());
  }

  @Test
  public void intersectionWithRightNullValues() {
    List<String> result = ListUtils.intersection(null, list_5items);
    assertTrue(result.isEmpty());
  }

  @Test
  public void intersectionWithValidValues() {
    assertTrue(ListUtils.intersection(list_4items, list_5items).equals(Arrays.asList("C", "D")));
    assertTrue(ListUtils.complement(new ArrayList<String>(), new ArrayList<>()).equals(Arrays.asList()));
  }

  @Test
  public void complementWithNullValues() {
    List<String> result = ListUtils.complement(null, null);
    assertTrue(result.isEmpty());
  }

  @Test
  public void complementWithLeftNullValues() {
    List<String> result = ListUtils.complement(list_4items, null);
    assertTrue(result.isEmpty());
  }

  @Test
  public void complementWithRightNullValues() {
    List<String> result = ListUtils.complement(null, list_5items);
    assertTrue(result.equals(Arrays.asList("C", "D", "E", "F", "G")));
  }

  @Test
  public void complementWithValidValues() {
    assertTrue(ListUtils.complement(list_4items, list_5items).equals(Arrays.asList("E", "F", "G")));
    assertTrue(ListUtils.complement(new ArrayList<String>(), new ArrayList<>()).equals(Arrays.asList()));
  }
}
