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
package org.apache.streampipes.manager.assets;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class TestImagePathReplacer {

  private static final String testContentReplaced = """
      ## Numerical Filter

      <img src="/streampipes-backend/api/v2/pe/app/assets/logo.png"/>

      ## Description

      Lorem ipsu""";

  private static final String appId = "app";


  @Test
  public void testRegexReplacement() {
    String testContent = """
        ## Numerical Filter

        <img src="logo.png"/>

        ## Description

        Lorem ipsu""";
    String newContent = new ImagePathReplacer(testContent, appId).replaceContent();
    assertEquals(testContentReplaced, newContent);

  }
}
