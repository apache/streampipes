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

package org.apache.streampipes.extensions.connectors.opcua.config.security;

import org.eclipse.milo.opcua.stack.core.security.FileBasedTrustListManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SharedTrustListManagerProviderTest {

  @TempDir
  Path tempDir;

  @Test
  void reusesTrustListManagerForSameDirectory() throws Exception {
    var factory = mock(SharedTrustListManagerProvider.TrustListManagerFactory.class);
    var trustListManager = mock(FileBasedTrustListManager.class);
    var trustListDirectory = tempDir.resolve("pki");
    when(factory.create(trustListDirectory)).thenReturn(trustListManager);

    try (var provider = new SharedTrustListManagerProvider(factory)) {
      var first = provider.getOrCreate(trustListDirectory);
      var second = provider.getOrCreate(trustListDirectory.resolve("."));

      assertSame(first, second);
      verify(factory, times(1)).create(trustListDirectory);
    }

    verify(trustListManager).close();
  }

  @Test
  void createsSeparateManagersForDifferentDirectoriesAndClosesBoth() throws Exception {
    var factory = mock(SharedTrustListManagerProvider.TrustListManagerFactory.class);
    var firstManager = mock(FileBasedTrustListManager.class);
    var secondManager = mock(FileBasedTrustListManager.class);
    var firstDirectory = tempDir.resolve("first");
    var secondDirectory = tempDir.resolve("second");
    when(factory.create(firstDirectory)).thenReturn(firstManager);
    when(factory.create(secondDirectory)).thenReturn(secondManager);

    try (var provider = new SharedTrustListManagerProvider(factory)) {
      assertSame(firstManager, provider.getOrCreate(firstDirectory));
      assertSame(secondManager, provider.getOrCreate(secondDirectory));
    }

    verify(firstManager).close();
    verify(secondManager).close();
  }
}
