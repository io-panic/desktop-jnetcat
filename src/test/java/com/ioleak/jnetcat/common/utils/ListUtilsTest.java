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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.CONCURRENT)
public class ListUtilsTest {

  private static final List<String> FIXED_LIST_4_ITEMS = Arrays.asList("A", "B", "C", "D");
  private static final List<String> FIXED_LIST_5_ITEMS = Arrays.asList("C", "D", "E", "F", "G");

  @Test
  public void union_NullValues_Empty() {
    List<String> result = ListUtils.union(null, null);
    assertTrue(result.isEmpty());
  }

  @Test
  public void union_LeftNullValues_EqualsToSelf() {
    List<String> result = ListUtils.union(null, FIXED_LIST_5_ITEMS);
    assertEquals(Arrays.asList("C", "D", "E", "F", "G"), result);
  }

  @Test
  public void union_RightNullValues_EqualsToSelf() {
    List<String> result = ListUtils.union(FIXED_LIST_4_ITEMS, null);
    assertEquals(Arrays.asList("A", "B", "C", "D"), result);
  }

  @Test
  public void union_ValidValues_Merge() {
    assertTrue(ListUtils.complement(new ArrayList<String>(), new ArrayList<>()).equals(Arrays.asList()));
    assertEquals(Arrays.asList("A", "B", "C", "D", "E", "F", "G"), ListUtils.union(FIXED_LIST_4_ITEMS, FIXED_LIST_5_ITEMS));
  }

  @Test
  public void intersection_NullValues_Empty() {
    List<String> result = ListUtils.intersection(null, null);
    assertTrue(result.isEmpty());
  }

  @Test
  public void intersection_LeftNullValues_Empty() {
    List<String> result = ListUtils.intersection(FIXED_LIST_4_ITEMS, null);
    assertTrue(result.isEmpty());
  }

  @Test
  public void intersection_RightNullValues_Empty() {
    List<String> result = ListUtils.intersection(null, FIXED_LIST_5_ITEMS);
    assertTrue(result.isEmpty());
  }

  @Test
  public void intersection_WithValidValues_ValidResult() {
    assertTrue(ListUtils.complement(new ArrayList<String>(), new ArrayList<>()).equals(Arrays.asList()));
    assertEquals(Arrays.asList("C", "D"), ListUtils.intersection(FIXED_LIST_4_ITEMS, FIXED_LIST_5_ITEMS));
  }

  @Test
  public void complement_NullValues_Empty() {
    List<String> result = ListUtils.complement(null, null);
    assertTrue(result.isEmpty());
  }

  @Test
  public void complement_LeftNullValues_Empty() {
    List<String> result = ListUtils.complement(FIXED_LIST_4_ITEMS, null);
    assertTrue(result.isEmpty());
  }

  @Test
  public void complement_RightNullValues_RightValue() {
    List<String> result = ListUtils.complement(null, FIXED_LIST_5_ITEMS);
    assertEquals(Arrays.asList("C", "D", "E", "F", "G"), result);
  }

  @Test
  public void complement_ValidValues_ValidResult() {
    assertTrue(ListUtils.complement(new ArrayList<String>(), new ArrayList<>()).equals(Arrays.asList()));
    assertEquals(Arrays.asList("E", "F", "G"), ListUtils.complement(FIXED_LIST_4_ITEMS, FIXED_LIST_5_ITEMS));
  }
}
