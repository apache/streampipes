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

package org.apache.streampipes.smp.parser;

import org.apache.streampipes.smp.model.AssetModel;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TestDocumentationParser {


  @Test
  public void testPipelineElementNameReplacement() throws IOException {
    ClassLoader classLoader = this.getClass().getClassLoader();
    AssetModel assetModel = new AssetModel("abc", "Numerical Filter",
        "Numerical Filter Description");

    String originalContent = IOUtils.toString(classLoader.getResourceAsStream("documentation.md"));
    String expectedContent = IOUtils.toString(classLoader.getResourceAsStream("expected.documentation.md"));
    String content =
        new DocumentationParser(assetModel)
            .parseAndStoreDocumentation(originalContent);

    assertEquals(expectedContent, content);
  }
}
