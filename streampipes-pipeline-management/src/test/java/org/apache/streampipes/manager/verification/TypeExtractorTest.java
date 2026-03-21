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

package org.apache.streampipes.manager.verification;

import org.apache.streampipes.commons.exceptions.SepaParseException;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.verification.extractor.TypeExtractor;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.storage.api.pipeline.IPipelineElementDescriptionStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TypeExtractorTest {

  private IPipelineElementDescriptionStorage storageApi;
  private final ExtensionServiceRequestManager requestManager = mock(ExtensionServiceRequestManager.class);

  @BeforeEach
  public void setUp() {
    storageApi = mock(IPipelineElementDescriptionStorage.class);
  }

  @Test
  public void verifyAndUpdateDataStream() throws SepaParseException {
    var message = new TypeExtractor(payload(SpDataStream.class), storageApi, requestManager)
        .getTypeVerifier()
        .verifyAndUpdate();

    assertTrue(message.isSuccess());
    verify(storageApi).update(any(SpDataStream.class));
  }

  @Test
  public void verifyAndUpdateDataProcessor() throws SepaParseException {
    var message = new TypeExtractor(payload(DataProcessorDescription.class), storageApi, requestManager)
        .getTypeVerifier()
        .verifyAndUpdate();

    assertTrue(message.isSuccess());
    verify(storageApi).update(any(DataProcessorDescription.class));
  }

  @Test
  public void verifyAndUpdateDataSink() throws SepaParseException {
    var message = new TypeExtractor(payload(DataSinkDescription.class), storageApi, requestManager)
        .getTypeVerifier()
        .verifyAndUpdate();

    assertTrue(message.isSuccess());
    verify(storageApi).update(any(DataSinkDescription.class));
  }

  @Test
  public void verifyAndUpdateAdapter() throws SepaParseException {
    var message = new TypeExtractor(payload(AdapterDescription.class), storageApi, requestManager)
        .getTypeVerifier()
        .verifyAndUpdate();

    assertTrue(message.isSuccess());
    verify(storageApi).update(any(AdapterDescription.class));
  }

  @Test
  public void verifyAndUpdateWithoutNameOrIcon_onlyContainsStorageSuccessNotification() throws SepaParseException {
    var message = new TypeExtractor(payload(DataProcessorDescription.class), storageApi, requestManager)
        .getTypeVerifier()
        .verifyAndUpdate();

    assertEquals(1, message.getNotifications().size());
    assertEquals(NotificationType.STORAGE_SUCCESS.title(), message.getNotifications().get(0).getTitle());
  }

  @Test
  public void missingClassProperty_throwsSepaParseException() {
    assertThrows(
        SepaParseException.class,
        () -> new TypeExtractor(
            "{\"name\":\"test\"}", storageApi, requestManager).getTypeVerifier()
    );
  }

  private String payload(Class<?> clazz) {
    return "{"
        + "\"@class\":\"" + clazz.getCanonicalName() + "\","
        + "\"appId\":\"test-app\","
        + "\"elementId\":\"test-element\""
        + "}";
  }
}
