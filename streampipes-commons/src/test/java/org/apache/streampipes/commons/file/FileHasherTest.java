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
package org.apache.streampipes.commons.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileHasherTest {

  private FileHasher fileHasher;

  @BeforeEach
  public void setup() {
    this.fileHasher = new FileHasher();
  }

  @Test
  void hash_returnsCorrectHashForFile() throws IOException {
    var file = new File("src/test/resources/test.txt");
    assertEquals("6df4d50a41a5d20bc4faad8a6f09aa8f", fileHasher.hash(file));
  }

  @Test
  void hash_throwsIOExceptionForNonExistingFile() {
    var file = new File("src/test/resources/nonExistingFile.txt");
    assertThrows(IOException.class, () -> fileHasher.hash(file));
  }

  @Test
  void hash_throwsIOExceptionForNullFile() {
    assertThrows(IOException.class, () -> fileHasher.hash(null));
  }
}