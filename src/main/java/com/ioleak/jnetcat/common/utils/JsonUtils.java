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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ioleak.jnetcat.common.Logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class JsonUtils {

  private static ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

  public static String objectToJson(Object object) {
    String json = "";

    try {
      json = objectMapper.writeValueAsString(object);
    } catch (IOException ex) {
      Logging.getLogger().error("Cannot convert object to JSON string", ex);
    }

    return json;
  }

  public static <T> T jsonToObject(String relativePathToFile, Class<T> clazz) {
    T object = null;

    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    URL pathToFile = getRelativePath(relativePathToFile, clazz);

    try {
      InputStream inputStream = new FileInputStream(new File(pathToFile.toURI()));

      if (inputStream != null) {
        object = (T) objectMapper.readValue(inputStream, clazz);
      }
    } catch (URISyntaxException | IOException ex) {
      Logging.getLogger().error("Cannot convert json to Object", ex);
    }

    return object;
  }

  public static void saveJsonToFile(String relativePathToFile, String jsonData) {
    try {
      URL pathToFile = getRelativePath(relativePathToFile, JsonUtils.class);
      createDirectories(pathToFile);

      Files.writeString(Paths.get(pathToFile.toURI()), jsonData, StandardOpenOption.CREATE_NEW);
    } catch (URISyntaxException | IOException ex) {
      Logging.getLogger().error(String.format("Unable to save JSON into file %s", relativePathToFile), ex);
    }
  }

  public static <T> URL getRelativePath(String relativePathToFile, Class<T> clazz) {
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    URL url = classloader.getResource(relativePathToFile);

    if (url == null) {
      url = clazz.getProtectionDomain().getCodeSource().getLocation();

      try {
        URI parent = url.toURI().getPath().endsWith("/") ? url.toURI().resolve("..") : url.toURI().resolve(".");
        URI newUri = parent.resolve(parent.getPath() + relativePathToFile);

        url = newUri.toURL();
      } catch (MalformedURLException | URISyntaxException ex) {
        Logging.getLogger().error(String.format("Unable to get a valid path to the file %s", relativePathToFile));
      }
    }

    return url;
  }

  public static void createDirectories(URL url) {
    try {
      File file = new File(url.toURI());
      file.getParentFile().mkdirs();
    } catch (URISyntaxException ex) {
      Logging.getLogger().error(String.format("Unable to create subdirectories for %s", url), ex);
    }
  }
}
