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
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestFileManager {

  @Test
  public void testCleanFileWithoutBom() throws IOException {
    var expected = "test";
    var inputStream = IOUtils.toInputStream(expected, StandardCharsets.UTF_8);
    var resultStream = FileManager.cleanFile(inputStream, "CSV");
    var resultString = IOUtils.toString(resultStream, StandardCharsets.UTF_8);

    assertEquals(expected, resultString);
  }

  @Test
  public void testCleanFileWithBom() throws IOException {
    var expected = "test";
    var utf8Bom = "\uFEFF";
    var inputString = utf8Bom + expected;
    var inputStream = IOUtils.toInputStream(inputString, StandardCharsets.UTF_8);
    var resultStream = FileManager.cleanFile(inputStream, "CSV");
    var resultString = IOUtils.toString(resultStream, StandardCharsets.UTF_8);

    assertEquals(expected, resultString);
  }

  @Test
  public void testCleanFileWithBomAndUmlauts() throws IOException {
    var expected = "testäüö";
    var utf8Bom = "\uFEFF";
    var inputString = utf8Bom + expected;
    var inputStream = IOUtils.toInputStream(inputString, StandardCharsets.UTF_8);
    var resultStream = FileManager.cleanFile(inputStream, "CSV");
    var resultString = IOUtils.toString(resultStream, StandardCharsets.UTF_8);

    assertEquals(expected, resultString);
  }

  @Test
  public void sanitizeFilename_replacesNonAlphanumericCharactersWithUnderscore() {
    var filename = "file@name#with$special%characters";
    var sanitizedFilename = FileManager.sanitizeFilename(filename);
    assertEquals("file_name_with_special_characters", sanitizedFilename);
  }

  @Test
  public void sanitizeFilename_keepsAlphanumericAndDotAndHyphenCharacters() {
    var filename = "file.name-with_alphanumeric123";
    var sanitizedFilename = FileManager.sanitizeFilename(filename);
    assertEquals(filename, sanitizedFilename);
  }

  @Test
  public void sanitizeFilename_returnsUnderscoreForFilenameWithAllSpecialCharacters() {
    var filename = "@#$%^&*()";
    var sanitizedFilename = FileManager.sanitizeFilename(filename);
    assertEquals("_________", sanitizedFilename);
  }

  @Test
  public void sanitizeFilename_returnsEmptyStringForEmptyFilename() {
    var filename = "";
    var sanitizedFilename = FileManager.sanitizeFilename(filename);
    assertEquals("", sanitizedFilename);
  }

  @Test
  public void sanitizeFilename_removesSingleParentDirectory() {
    var filename = "../file";
    var sanitizedFilename = FileManager.sanitizeFilename(filename);
    assertEquals(".._file", sanitizedFilename);
  }

  @Test
  public void sanitizeFilename_removesDoubleParentDirectoryy() {
    var filename = "../../file";
    var sanitizedFilename = FileManager.sanitizeFilename(filename);
    assertEquals(".._.._file", sanitizedFilename);
  }
}