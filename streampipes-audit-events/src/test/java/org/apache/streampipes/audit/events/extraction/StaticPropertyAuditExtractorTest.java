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

package org.apache.streampipes.audit.events.extraction;

import org.apache.streampipes.model.staticproperty.SecretStaticProperty;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StaticPropertyAuditExtractorTest {
  @Test
  void decryptsOnlyEncryptedSecretsWithoutMutatingProperties() {
    var calls = new AtomicInteger();
    var extractor = new StaticPropertyAuditExtractor(value -> {
      calls.incrementAndGet();
      assertEquals("ciphertext", value);
      return "plaintext";
    });
    var encrypted = new SecretStaticProperty("encrypted", "", "");
    encrypted.setEncrypted(true);
    encrypted.setValue("ciphertext");
    var plain = new SecretStaticProperty("plain", "", "");
    plain.setEncrypted(false);
    plain.setValue("plaintext");
    var extracted = extractor.extract(List.of(encrypted, plain));
    assertEquals(1, calls.get());
    assertEquals("plaintext", extracted.get("encrypted").value());
    assertEquals(extracted.get("encrypted"), extracted.get("plain"));
    assertTrue(extracted.get("encrypted").secret());
    assertEquals("ciphertext", encrypted.getValue());
    assertTrue(encrypted.getEncrypted());
  }

  @Test
  void requiresAnExplicitDecryptionStrategy() {
    assertThrows(NullPointerException.class, () -> new StaticPropertyAuditExtractor(null));
  }

  @Test
  void propagatesDecryptionFailureToTheRecordingBoundary() {
    var extractor = new StaticPropertyAuditExtractor(value -> {
      throw new IllegalStateException("decryption unavailable");
    });
    var encrypted = new SecretStaticProperty("secret", "", "");
    encrypted.setEncrypted(true);
    encrypted.setValue("ciphertext");
    assertThrows(IllegalStateException.class, () -> extractor.extract(List.of(encrypted)));
  }
}
