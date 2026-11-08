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

package org.apache.streampipes.export.resolver;

import org.apache.streampipes.commons.constants.GenericDocTypes;
import org.apache.streampipes.model.export.AssetExportConfiguration;
import org.apache.streampipes.storage.api.system.IGenericStorage;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GenericStorageDocumentResolverTest {

  private static final String EXPORTED_LINK_TYPE = """
      {"_id":"source-id","appDocType":"asset-link-type","linkType":"site"}
      """;

  @Test
  void skipsAssetLinkTypeAlreadyPresentOnTarget() throws Exception {
    var storage = mock(IGenericStorage.class);
    when(storage.findAll(GenericDocTypes.DOC_ASSET_LINK_TYPE))
        .thenReturn(List.of(Map.of("_id", "target-id", "linkType", "site")));

    new GenericStorageDocumentResolver(storage).writeDocument(EXPORTED_LINK_TYPE, new AssetExportConfiguration());

    verify(storage, never()).create(anyString());
  }

  @Test
  void importsAssetLinkTypeWhenTargetDoesNotHaveItsLinkType() throws Exception {
    var storage = mock(IGenericStorage.class);
    when(storage.findAll(GenericDocTypes.DOC_ASSET_LINK_TYPE)).thenReturn(List.of());

    new GenericStorageDocumentResolver(storage).writeDocument(EXPORTED_LINK_TYPE, new AssetExportConfiguration());

    verify(storage).create(EXPORTED_LINK_TYPE);
  }

  @Test
  void doesNotImportWhenExistingTypesCannotBeChecked() throws Exception {
    var storage = mock(IGenericStorage.class);
    when(storage.findAll(GenericDocTypes.DOC_ASSET_LINK_TYPE)).thenThrow(new IOException("synthetic lookup failure"));

    assertThrows(IllegalStateException.class,
        () -> new GenericStorageDocumentResolver(storage).writeDocument(EXPORTED_LINK_TYPE,
            new AssetExportConfiguration()));

    verify(storage, never()).create(anyString());
  }
}
