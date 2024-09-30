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
package org.apache.streampipes.rest.impl;

import org.apache.streampipes.manager.file.FileManager;
import org.apache.streampipes.model.file.FileMetadata;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.exception.SpMessageException;
import org.apache.streampipes.sdk.helpers.Filetypes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.http.HttpStatus;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v2/files")
public class PipelineElementFile extends AbstractAuthGuardedRestResource {

  private final FileManager fileManager;

  public PipelineElementFile() {
    this.fileManager = new FileManager();
  }

  @PostMapping(
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public ResponseEntity<?> storeFile(@RequestPart("file_upload") MultipartFile fileDetail) {
    try {
      FileMetadata metadata =
          fileManager.storeFile(
              getAuthenticatedUsername(),
              fileDetail.getOriginalFilename(),
              fileDetail.getInputStream()
          );
      return ok(metadata);
    } catch (IllegalArgumentException e) {
      return badRequest(Notifications.error(
          String.format(
              "This file type is not supported. Allowed file types are %s.",
              Filetypes.getAllFileExtensions().toString()
          )
      ));
    } catch (Exception e) {
      return fail();
    }
  }

  @DeleteMapping(path = "{fileId}")
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public ResponseEntity<Void> deleteFile(@PathVariable("fileId") String fileId) {
    fileManager.deleteFile(fileId);
    return ok();
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_READ_FILE_PRIVILEGE)
  public ResponseEntity<List<FileMetadata>> getFileInfo(
      @RequestParam(value = "filetypes", required = false) String filetypes
  ) {
    return ok(fileManager.getAllFiles(filetypes));
  }

  @GetMapping(path = "/{filename}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  @Operation(
      summary = "Get file content by file name."
          + "If multiple files with the same name exist, only the first is returned."
          + "This can only be the case when the original file name is provided.", tags = {"Core", "Files"},
      responses = {
          @ApiResponse(
              responseCode = "" + HttpStatus.SC_OK,
              description = "File could be found and is returned"),
          @ApiResponse(
              responseCode = "" + HttpStatus.SC_NOT_FOUND,
              description = "No file with the given file name could be found")
      }
  )
  public ResponseEntity<byte[]> getFile(
      @Parameter(
          in = ParameterIn.PATH,
          description = "The name of the file to be retrieved",
          required = true
      )
      @PathVariable("filename") String filename
  ) {
    try {
      return ok(getFileContents(fileManager.getFile(filename)));
    } catch (IOException e) {
      throw new SpMessageException(
          NOT_FOUND,
          Notifications.error("File not found")
      );
    }
  }

  private byte[] getFileContents(File file) throws IOException {
    return Files.readAllBytes(file.toPath());
  }

  @GetMapping(path = "/allFilenames", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_READ_FILE_PRIVILEGE)
  public ResponseEntity<List<String>> getAllOriginalFilenames() {
    return ok(fileManager.getAllFiles()
        .stream()
        .map(fileMetadata -> fileMetadata.getFilename()
            .toLowerCase())
        .toList());
  }

  @GetMapping(
      path = "/{filename}/checkFileContentChanged/{hash}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_READ_FILE_PRIVILEGE)
  public ResponseEntity<Boolean> checkFileContentChanged(
      @PathVariable(value = "filename") String filename,
      @PathVariable(value = "hash") String hash
  ) {
    try {
      return ok(fileManager.checkFileContentChanged(filename, hash));
    } catch (IOException e) {
      throw new SpMessageException(
          NOT_FOUND,
          Notifications.error("File not found")
      );
    }
  }


}
