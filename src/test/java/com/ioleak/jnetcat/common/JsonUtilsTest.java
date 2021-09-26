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

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedHashMap;

import com.ioleak.jnetcat.common.utils.JsonUtils;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

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
  public void convertObjectToJson() {
    JsonObjectTest jsonObjectTest = new JsonObjectTest("XXXX", "YYYYY", 35, 5.95);
    String json = JsonUtils.objectToJson(jsonObjectTest);

    assertTrue(json.equals(JSON_DATA));
  }

  @Test
  public void convertObjectToJsonWithInvalidValue() {
    assertTrue(JsonUtils.objectToJson(null).equals(JsonUtils.JSON_EMPTY));
    assertTrue(JsonUtils.objectToJson(new Object()).equals(JsonUtils.JSON_EMPTY));
  }

  @Test
  public void convertJsonToObject() {
    JsonObjectTest jsonObjectTest = JsonUtils.jsonToObject(JSON_DATA, JsonObjectTest.class);

    assertTrue(jsonObjectTest.getFirstName().equals("XXXX"));
    assertTrue(jsonObjectTest.getLastName().equals("YYYYY"));
    assertTrue(jsonObjectTest.getAge() == 35);
    assertTrue(jsonObjectTest.getMoney() == 5.95);
  }

  @Test
  public void convertJsonToObjectWithInvalidValue() {
    assertTrue(JsonUtils.jsonToObject(null, Object.class) != null);
    assertTrue(JsonUtils.jsonToObject("", Object.class) != null);
    assertTrue((int) (JsonUtils.jsonToObject("{ \"test\": 4 }", LinkedHashMap.class).get("test")) == 4);
  }

  @Test
  @Order(1)
  public void testJSonFileCreation() {
    Path fileSavedPath = JsonUtils.saveJsonToFile("test/test.json", JSON_DATA);
    assertTrue(fileSavedPath != null && new File(fileSavedPath.toUri()).exists());
  }

  @Test
  @Order(2)
  public void testJsonFileLoad() {
    String json = JsonUtils.loadJsonFileToString("test/test.json");
    assertTrue(json.equals(JSON_DATA));
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
