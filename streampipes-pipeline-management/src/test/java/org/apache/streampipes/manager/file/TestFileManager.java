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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.streampipes.commons.file.FileHasher;
import org.apache.streampipes.model.file.FileMetadata;
import org.apache.streampipes.storage.api.CRUDStorage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class TestFileManager {

  private FileManager fileManager;
  private CRUDStorage<FileMetadata> fileMetadataStorage;
  private FileHandler fileHandler;
  private FileHasher fileHasher;

  private static final String TEST_USER = "testUser";

  @BeforeEach
  public void setup() {
    fileMetadataStorage = mock(CRUDStorage.class);
    fileHandler = mock(FileHandler.class);
    fileHasher = mock(FileHasher.class);
    fileManager = new FileManager(fileMetadataStorage, fileHandler, fileHasher);
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
    List<FileMetadata> allFiles = Arrays.asList(createFileMetadata("csv"), createFileMetadata("json"));
    when(fileMetadataStorage.findAll()).thenReturn(allFiles);

    return allFiles;
  }

  private FileMetadata createFileMetadata(String fileType) {
    FileMetadata file = new FileMetadata();
    file.setFiletype(fileType);
    return file;
  }

  @Test
  public void getFile_returnsExistingFile() {
    var filename = "existingFile.txt";
    var expectedFile = new File(filename);
    when(fileHandler.getFile(filename)).thenReturn(expectedFile);

    var result = fileManager.getFile(filename);

    assertEquals(expectedFile, result);
  }

  @Test
  public void getFile_returnsNullForNonExistingFile() {
    var filename = "fileDoesNotExist.txt";
    when(fileHandler.getFile(filename)).thenReturn(null);

    assertNull(fileManager.getFile(filename));
  }

  @Test
  public void storeFile_throwsExceptionForInvalidFileType() {
    var filename = "testFile.invalid";

    assertThrows(IllegalArgumentException.class, () -> fileManager.storeFile("", filename, mock(InputStream.class)));
  }

  @Test
  public void storeFile_storesFileWithValidInput() throws IOException {
    var filename = "testFile.csv";

    var fileMetadata = fileManager.storeFile(TEST_USER, filename, mock(InputStream.class));

    assertEquals(TEST_USER, fileMetadata.getCreatedByUser());
    assertEquals(filename, fileMetadata.getFilename());
    assertEquals("csv", fileMetadata.getFiletype());
    verify(fileHandler, times(1)).storeFile(eq(filename), any(InputStream.class));
    verify(fileMetadataStorage, times(1)).persist(any(FileMetadata.class));
  }

  @Test
  public void storeFile_sanitizesFilename() throws IOException {
    var filename = "test@File.csv";
    var expectedSanitizedFilename = "test_File.csv";

    var fileMetadata = fileManager.storeFile(TEST_USER, filename, mock(InputStream.class));

    assertEquals(expectedSanitizedFilename, fileMetadata.getFilename());
    verify(fileHandler, times(1)).storeFile(eq(expectedSanitizedFilename), any(InputStream.class));
    verify(fileMetadataStorage, times(1)).persist(any(FileMetadata.class));
  }

  /**
   * This test validates that the storeFile method removes the BOM from the file before it is stored.
   */
  @Test
  public void storeFile_removesBom() throws IOException {
    var expectedContent = "test content";
    // prepare input stream with BOM
    var fileInputStream = new ByteArrayInputStream(("\uFEFF" + expectedContent).getBytes(StandardCharsets.UTF_8));

    fileManager.storeFile(TEST_USER, "testfile.csv", fileInputStream);

    var inputStreamCaptor = ArgumentCaptor.forClass(InputStream.class);
    verify(fileHandler, times(1)).storeFile(any(), inputStreamCaptor.capture());

    // Convert the captured InputStream to a String
    var capturedInputStream = inputStreamCaptor.getValue();
    var capturedContent = IOUtils.toString(capturedInputStream, StandardCharsets.UTF_8);

    // Assert that the captured content is equal to the expected content
    assertEquals(expectedContent, capturedContent);
  }

  @Test
  public void deleteFile_removesExistingFile() {
    var id = "existingFileId";
    var fileMetadata = new FileMetadata();
    fileMetadata.setFilename("existingFile.txt");

    when(fileMetadataStorage.getElementById(id)).thenReturn(fileMetadata);

    fileManager.deleteFile(id);

    verify(fileHandler, times(1)).deleteFile(fileMetadata.getFilename());
    verify(fileMetadataStorage, times(1)).deleteElementById(id);
  }

  @Test
  public void deleteFile_doesNothingForNonExistingFile() {
    var id = "nonExistingFileId";

    when(fileMetadataStorage.getElementById(id)).thenReturn(null);

    fileManager.deleteFile(id);

    verify(fileHandler, times(0)).deleteFile(anyString());
    verify(fileMetadataStorage, times(0)).deleteElementById(id);
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

  @Test
  public void checkFileContentChanged_returnsTrueWhenContentHasChanged() throws IOException {
    var filename = "testFile.csv";
    var originalHash = "originalHash";
    var changedHash = "changedHash";

    when(fileHandler.getFile(filename)).thenReturn(new File(filename));
    when(fileHasher.hash(any(File.class))).thenReturn(changedHash);

    var result = fileManager.checkFileContentChanged(filename, originalHash);

    assertTrue(result);
  }

  @Test
  public void checkFileContentChanged_returnsFalseWhenContentHasNotChanged() throws IOException {
    var filename = "testFile.csv";
    var originalHash = "originalHash";

    when(fileHandler.getFile(filename)).thenReturn(new File(filename));
    when(fileHasher.hash(any(File.class))).thenReturn(originalHash);

    var result = fileManager.checkFileContentChanged(filename, originalHash);

    assertFalse(result);
  }

}
