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

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedHashMap;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(OrderAnnotation.class)
public class JsonUtilsTest {

  private static final String JSON_DATA = "{\r\n"
                                          + "  \"firstName\" : \"XXXX\",\r\n"
                                          + "  \"lastName\" : \"YYYYY\",\r\n"
                                          + "  \"age\" : 35,\r\n"
                                          + "  \"money\" : 5.95\r\n"
                                          + "}";

  @Test
  public void objectToJson_ValidObject_JsonString() {
    JsonObjectTest jsonObjectTest = new JsonObjectTest("XXXX", "YYYYY", 35, 5.95);
    String json = JsonUtils.objectToJson(jsonObjectTest);

    assertEquals(json, JSON_DATA);
  }

  @Test
  public void objectToJson_NullOrNew_JsonEmptyString() {
    assertEquals(JsonUtils.JSON_EMPTY, JsonUtils.objectToJson(null));
    assertEquals(JsonUtils.JSON_EMPTY, JsonUtils.objectToJson(new Object()));
  }

  @Test
  public void jsonToObject_JsonString_ObjectValues() {
    JsonObjectTest jsonObjectTest = JsonUtils.jsonToObject(JSON_DATA, JsonObjectTest.class);

    assertEquals("XXXX", jsonObjectTest.getFirstName());
    assertEquals("YYYYY", jsonObjectTest.getLastName());
    assertEquals(35, jsonObjectTest.getAge());
    assertEquals(5.95, jsonObjectTest.getMoney());
  }

  @Test
  public void jsonToObject_InvalidDataOrCustomObject_ValidObject() {
    assertTrue(JsonUtils.jsonToObject(null, Object.class) != null);
    assertTrue(JsonUtils.jsonToObject("", Object.class) != null);
    assertTrue((int) (JsonUtils.jsonToObject("{ \"test\": 4 }", LinkedHashMap.class).get("test")) == 4);
  }

  @Test
  @Order(1)
  public void saveJsonToFile_PathToFileWithData_FileExists() {
    Path fileSavedPath = JsonUtils.saveJsonToFile("test/test.json", JSON_DATA);
    assertTrue(fileSavedPath != null && new File(fileSavedPath.toUri()).exists());
  }

  @Test
  @Order(2)
  public void loadJsonFileToString_PathToFile_StringResult() {
    String json = JsonUtils.loadJsonFileToString("test/test.json");
    assertEquals(JSON_DATA, json);
  }

  private static class JsonObjectTest {

    private String firstName;
    private String lastName;
    private int age;
    private double money;

    public JsonObjectTest() {
    }

    public JsonObjectTest(String firstName, String lastName, int age, double money) {
      this.firstName = firstName;
      this.lastName = lastName;
      this.age = age;
      this.money = money;
    }

    public String getFirstName() {
      return firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public int getAge() {
      return age;
    }

    public double getMoney() {
      return money;
    }
  }
}
