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

import org.apache.streampipes.model.file.FileMetadata;
import org.apache.streampipes.storage.api.IFileMetadataStorage;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestFileManager {

  private FileManager fileManager;
  private IFileMetadataStorage fileMetadataStorage;

  @BeforeEach
  public void setup() {
    fileMetadataStorage = mock(IFileMetadataStorage.class);
    fileManager = new FileManager(fileMetadataStorage);
  }

  @Test
  public void getAllFiles_returnsAllFiles() {
    var expected = prepareFileMetadataStorageWithTwoSampleFiles();

    var result = fileManager.getAllFiles();

    assertEquals(expected, result);
  }

  @Test
  public void getAllFiles_returnsAllFilesWhenFiletypesIsNull() {
    var expected = prepareFileMetadataStorageWithTwoSampleFiles();

    var result = fileManager.getAllFiles(null);

    assertEquals(expected, result);
  }

  @Test
  public void getAllFiles_returnsFilteredFilesWhenFiletypesIsNotNull() {
    var files = prepareFileMetadataStorageWithTwoSampleFiles();

    List<FileMetadata> result = fileManager.getAllFiles("csv");

    assertEquals(1, result.size());
    assertEquals(files.get(0), result.get(0));
  }

  @Test
  public void getAllFiles_returnsEmptyListWhenNoMatchingFiletypes() {
    prepareFileMetadataStorageWithTwoSampleFiles();

    var result = fileManager.getAllFiles("xml");

    assertEquals(0, result.size());
  }

  private List<FileMetadata> prepareFileMetadataStorageWithTwoSampleFiles() {
    List<FileMetadata> allFiles = Arrays.asList(createFile("csv"), createFile("json"));
    when(fileMetadataStorage.getAllFileMetadataDescriptions()).thenReturn(allFiles);

    return allFiles;
  }

  private FileMetadata createFile(String fileType) {
    FileMetadata file = new FileMetadata();
    file.setFiletype(fileType);
    return file;
  }


  @Test
  public void storeFile_throwsExceptionForInvalidFileType() {
    var filename = "testFile.invalid";

    assertThrows(IllegalArgumentException.class, () ->
        fileManager.storeFile("", filename, mock(InputStream.class)));
  }

  @Test
  public void testCleanFileWithoutBom() throws IOException {
    var expected = "test";
    var inputStream = IOUtils.toInputStream(expected, StandardCharsets.UTF_8);
    var resultStream = fileManager.cleanFile(inputStream, "CSV");
    var resultString = IOUtils.toString(resultStream, StandardCharsets.UTF_8);

    assertEquals(expected, resultString);
  }

  @Test
  public void testCleanFileWithBom() throws IOException {
    var expected = "test";
    var utf8Bom = "\uFEFF";
    var inputString = utf8Bom + expected;
    var inputStream = IOUtils.toInputStream(inputString, StandardCharsets.UTF_8);
    var resultStream = fileManager.cleanFile(inputStream, "CSV");
    var resultString = IOUtils.toString(resultStream, StandardCharsets.UTF_8);

    assertEquals(expected, resultString);
  }

  @Test
  public void testCleanFileWithBomAndUmlauts() throws IOException {
    var expected = "testäüö";
    var utf8Bom = "\uFEFF";
    var inputString = utf8Bom + expected;
    var inputStream = IOUtils.toInputStream(inputString, StandardCharsets.UTF_8);
    var resultStream = fileManager.cleanFile(inputStream, "CSV");
    var resultString = IOUtils.toString(resultStream, StandardCharsets.UTF_8);

    assertEquals(expected, resultString);
  }

  @Test
  public void sanitizeFilename_replacesNonAlphanumericCharactersWithUnderscore() {
    var filename = "file@name#with$special%characters";
    var sanitizedFilename = fileManager.sanitizeFilename(filename);
    assertEquals("file_name_with_special_characters", sanitizedFilename);
  }

  @Test
  public void sanitizeFilename_keepsAlphanumericAndDotAndHyphenCharacters() {
    var filename = "file.name-with_alphanumeric123";
    var sanitizedFilename = fileManager.sanitizeFilename(filename);
    assertEquals(filename, sanitizedFilename);
  }

  @Test
  public void sanitizeFilename_returnsUnderscoreForFilenameWithAllSpecialCharacters() {
    var filename = "@#$%^&*()";
    var sanitizedFilename = fileManager.sanitizeFilename(filename);
    assertEquals("_________", sanitizedFilename);
  }

  @Test
  public void sanitizeFilename_returnsEmptyStringForEmptyFilename() {
    var filename = "";
    var sanitizedFilename = fileManager.sanitizeFilename(filename);
    assertEquals("", sanitizedFilename);
  }

  @Test
  public void sanitizeFilename_removesSingleParentDirectory() {
    var filename = "../file.csv";
    var sanitizedFilename = fileManager.sanitizeFilename(filename);
    assertEquals(".._file.csv", sanitizedFilename);
  }

  @Test
  public void sanitizeFilename_removesDoubleParentDirectoryy() {
    var filename = "../../file";
    var sanitizedFilename = fileManager.sanitizeFilename(filename);
    assertEquals(".._.._file", sanitizedFilename);
  }

  @Test
  public void validateFileName_returnsTrueForCsv() {
    assertTrue(fileManager.validateFileType("file.csv"));
  }

  @Test
  public void validateFileName_returnsTrueForJson() {
    assertTrue(fileManager.validateFileType("file.json"));
  }

  @Test
  public void validateFileName_returnsFalseForSh() {
    assertFalse(fileManager.validateFileType("file.sh"));
  }
}