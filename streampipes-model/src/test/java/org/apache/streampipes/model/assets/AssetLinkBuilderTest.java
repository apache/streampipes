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
package org.apache.streampipes.model.assets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AssetLinkBuilderTest {

  @Test
  public void withoutEditingDisabled() {
    AssetLink assetLink = AssetLinkBuilder.create().withResourceId("123").withLinkType("type").withLinkLabel("label")
            .withQueryHint("hint").withEditingDisabled(false).build();

    Assertions.assertEquals("123", assetLink.getResourceId());
    Assertions.assertEquals("type", assetLink.getLinkType());
    Assertions.assertEquals("label", assetLink.getLinkLabel());
    Assertions.assertEquals("hint", assetLink.getQueryHint());
    Assertions.assertFalse(assetLink.isEditingDisabled());
  }

  @Test
  public void withEditingDisabled() {
    AssetLink assetLink = AssetLinkBuilder.create().withResourceId("456").withLinkType("anotherType")
            .withLinkLabel("anotherLabel").withQueryHint("anotherHint").withEditingDisabled(true).build();

    Assertions.assertEquals("456", assetLink.getResourceId());
    Assertions.assertEquals("anotherType", assetLink.getLinkType());
    Assertions.assertEquals("anotherLabel", assetLink.getLinkLabel());
    Assertions.assertEquals("anotherHint", assetLink.getQueryHint());
    Assertions.assertTrue(assetLink.isEditingDisabled());
  }
}
