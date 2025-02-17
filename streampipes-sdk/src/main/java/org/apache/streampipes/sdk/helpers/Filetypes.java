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
package org.apache.streampipes.sdk.helpers;

import org.apache.streampipes.commons.environment.Environments;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Filetypes {

  CSV("csv"),
  JPG("jpg", "jpeg"),
  JSON("json"),
  XLS("xls"),
  XLSX("xlsx"),
  XML("xml"),
  YAML("yaml", "yml"),
  ZIP("zip");

  private final List<String> fileExtensions;

  Filetypes(String... fileExtensions) {
    this.fileExtensions = Arrays.asList(fileExtensions);
  }

  public List<String> getFileExtensions() {
    return fileExtensions;
  }

  public static Set<String> getAllFileExtensions() {
    var filetypes = Stream.of(Filetypes.values())
        .flatMap(filetype -> filetype.getFileExtensions().stream())
        .collect(Collectors.toSet());
    filetypes.addAll(getSupportedFiletypesFromEnv());
    return filetypes;
  }

  private static Set<String> getSupportedFiletypesFromEnv() {
    return Optional.ofNullable(Environments.getEnvironment()
            .getAllowedUploadFiletypes()
            .getValueOrDefault())
        .stream()
        .flatMap(allowedFiletypes -> Arrays.stream(allowedFiletypes.split(",")))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toSet());
  }
}
