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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ioleak.jnetcat.common.Logging;

public class JsonUtils {

  public static final String JSON_EMPTY = "{ }";

  private static final ObjectMapper objectMapper = new ObjectMapper()
          .enable(SerializationFeature.INDENT_OUTPUT)
          .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

  private JsonUtils() {
  }

  public static String objectToJson(Object object) {
    String json = JSON_EMPTY;

    try {
      if (object != null) {
        json = objectMapper.writeValueAsString(object);
      }
    } catch (JsonProcessingException ex) {
      Logging.getLogger().error("Cannot convert object to JSON string", ex);
    }

    return json;
  }

  public static <T> T jsonToObject(String jsonData, Class<T> clazz) {
    T object = null;

    if (StringUtils.isNullOrEmpty(jsonData)) {
      try {
        object = clazz.getDeclaredConstructor().newInstance();
      } catch (NoSuchMethodException | SecurityException | InstantiationException
              | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        Logging.getLogger().error("Unable to start a new instance, invalid JSON?", ex);
      }
    } else {
      try {
        object = (T) objectMapper.readValue(jsonData, clazz);
      } catch (JsonProcessingException ex) {
        Logging.getLogger().error(String.format("Unable to parse JSON data (msg: %s)", ex.getMessage()));
      }
    }

    return object;
  }

  public static String loadJsonFileToString(String relativePathToFile) {
    String jsonContent = JSON_EMPTY;

    URL pathToFile = getAbsolutePathTo(relativePathToFile);

    try {
      jsonContent = loadJsonFileToString(new File(pathToFile.toURI()));
    } catch (URISyntaxException ex) {
      Logging.getLogger().error("Cannot read file", ex);
    }

    return jsonContent;
  }

  public static String loadJsonFileToString(File jsonFile) {
    String jsonContent = JSON_EMPTY;

    try (InputStream inputStream = new FileInputStream(jsonFile)) {
      ByteArrayOutputStream result = new ByteArrayOutputStream();

      byte[] buffer = new byte[1024];
      for (int length; (length = inputStream.read(buffer)) != -1;) {
        result.write(buffer, 0, length);
      }

      jsonContent = result.toString("UTF-8");
    } catch (FileNotFoundException ex) {
      Logging.getLogger().error(String.format("File (%s) cannot be found", jsonFile.getAbsolutePath()));
    } catch (IOException ex) {
      Logging.getLogger().error(String.format("An error occured while trying to open/read file: %s", jsonFile.getAbsolutePath()), ex);
    }

    return jsonContent;
  }

  public static Path saveJsonToFile(String relativePathToFile, String jsonData) {
    Path fileSavedPath = null;

    try {
      URL pathToFile = getAbsolutePathTo(relativePathToFile);
      createDirectories(pathToFile);

      fileSavedPath = Paths.get(pathToFile.toURI());
      Files.writeString(fileSavedPath, jsonData, StandardOpenOption.CREATE);
      Logging.getLogger().debug(String.format("JSON succesfully saved to file: %s", fileSavedPath));
    } catch (URISyntaxException | IOException ex) {
      Logging.getLogger().error(String.format("Unable to save JSON into file %s", relativePathToFile), ex);
    }

    return fileSavedPath;
  }

  public static URL getAbsolutePathTo(String relativeFilePath) {
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    URL url = classloader.getResource(relativeFilePath);

    if (url == null) {
      Path currentDir = Paths.get("").toAbsolutePath();
      Path configFile = Paths.get(currentDir.toString(), relativeFilePath);

      try {
        url = configFile.toUri().toURL();
      } catch (MalformedURLException ex) {
        Logging.getLogger().error(String.format("Unable to get a valid path to the file %s", relativeFilePath));
      }
    }

    return url;
  }

  private static void createDirectories(URL url) {
    try {
      File file = new File(url.toURI());
      file.getParentFile().mkdirs();
    } catch (URISyntaxException ex) {
      Logging.getLogger().error(String.format("Unable to create subdirectories for %s", url), ex);
    }
  }
}
