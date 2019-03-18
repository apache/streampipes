/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.manager.assets;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestImagePathReplacer {

  private String TEST_CONTENT = "## Numerical Filter\n" +
          "\n" +
          "<img src=\"logo.png\"/>\n" +
          "\n" +
          "## Description\n" +
          "\n" +
          "Lorem ipsu";
  private String TEST_CONTENT_REPLACED = "## Numerical Filter\n" +
          "\n" +
          "<img src=\"/api/v1/pe/app/assets/logo.png\"/>\n" +
          "\n" +
          "## Description\n" +
          "\n" +
          "Lorem ipsu";

  private String appId = "app";


  @Test
  public void testRegexReplacement() {
    String newContent = new ImagePathReplacer(TEST_CONTENT, appId).replaceContent();
    assertEquals(TEST_CONTENT_REPLACED, newContent);

  }
}
