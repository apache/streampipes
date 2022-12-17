/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.streampipes.manager.file;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class TestFileManager {

  @Test
  public void testCleanFileWithoutBom() throws IOException {
    String expected = "test";
    InputStream inputStream = IOUtils.toInputStream(expected, StandardCharsets.UTF_8);
    InputStream resultStream = FileManager.cleanFile(inputStream, "CSV");
    String resultString = IOUtils.toString(resultStream, StandardCharsets.UTF_8);

    assertEquals(expected, resultString);
  }

  @Test
  public void testCleanFileWithBom() throws IOException {
    String expected = "test";
    String utf8Bom = "\uFEFF";
    String inputString = utf8Bom + expected;
    InputStream inputStream = IOUtils.toInputStream(inputString, StandardCharsets.UTF_8);
    InputStream resultStream = FileManager.cleanFile(inputStream, "CSV");
    String resultString = IOUtils.toString(resultStream, StandardCharsets.UTF_8);

    assertEquals(expected, resultString);
  }

  @Test
  public void testCleanFileWithBomAndUmlauts() throws IOException {
    String expected = "testäüö";
    String utf8Bom = "\uFEFF";
    String inputString = utf8Bom + expected;
    InputStream inputStream = IOUtils.toInputStream(inputString, StandardCharsets.UTF_8);
    InputStream resultStream = FileManager.cleanFile(inputStream, "CSV");
    String resultString = IOUtils.toString(resultStream, StandardCharsets.UTF_8);

    assertEquals(expected, resultString);
  }
}