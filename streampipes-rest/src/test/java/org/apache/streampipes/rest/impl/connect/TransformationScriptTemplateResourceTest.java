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

package org.apache.streampipes.rest.impl.connect;

import org.apache.streampipes.model.connect.ConnectTransformationScriptTemplate;
import org.apache.streampipes.storage.api.system.ITransformationScriptTemplateStorage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class TransformationScriptTemplateResourceTest {

  private final ITransformationScriptTemplateStorage storage = mock(ITransformationScriptTemplateStorage.class);
  private final TransformationScriptTemplateResource resource = new TransformationScriptTemplateResource(storage);

  @Test
  void rejectsMismatchedIdBeforeWriting() {
    var template = new ConnectTransformationScriptTemplate();
    template.setElementId("body-id");

    assertThrows(IllegalArgumentException.class, () -> resource.update("path-id", template));
    verifyNoInteractions(storage);
  }

  @Test
  void updatesThroughInjectedStorage() {
    var template = new ConnectTransformationScriptTemplate();
    template.setElementId("template");
    when(storage.updateElement(template)).thenReturn(template);

    assertSame(template, resource.update("template", template));
    verify(storage).updateElement(template);
  }

  @Test
  void deletesStoredTemplate() {
    var template = new ConnectTransformationScriptTemplate();
    when(storage.getElementById("template")).thenReturn(template);

    resource.delete("template");

    verify(storage).deleteElement(template);
  }
}
