/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.codegeneration.utils;

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.JavaFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Utils {

  private static final String PROPERTY_SEPARATOR = "-";
  private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

  public static String readResourceFile(String fileName) {

    StringBuilder sb = new StringBuilder();
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    InputStream input = classLoader.getResourceAsStream(fileName);
    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    String line;
    try {
      while ((line = reader.readLine()) != null) {
        sb.append(line);
        sb.append("\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return sb.toString();
  }

  public static String readFile(String fileName) {
    BufferedReader br = null;

    try {
      br = new BufferedReader(new FileReader(fileName));
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();

      while (line != null) {
        sb.append(line);
        sb.append("\n");
        line = br.readLine();
      }
      return sb.toString();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        br.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return "Couldn't read file: " + fileName;
  }

  public static void writeToFile(String content, String file) {
    Path path = Paths.get(file);
    byte[] b = content.getBytes();
    try {
      Files.write(path, b);
    } catch (IOException e) {
      LOG.error("Could not write to file", e);
    }
  }

  public static void writeToFile(JavaFile content, String location) {
    try {
      content.writeTo(new File(location));
    } catch (IOException e) {
      e.printStackTrace();
      LOG.error("Error: Could not write to file: {}", location, e);
    }
  }

  public static String toCamelCase(String propertyName) {
    if (propertyName.contains("-")) {
      return CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, propertyName);
    } else {
      return propertyName;
    }

  }


}
