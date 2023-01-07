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

package org.apache.streampipes.smp.generator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestMarkdownTitleRemover {

  private String content = "---\n" + "id: org.apache.streampipes.processor.imageclassification.image-rectifier\n"
                           + "title: Image Rectifier\n" + "sidebar_label: Image Rectifier\n" + "---\n" + "\n"
                           + "## Image Rectifier\n" + "\n" + "<p align=\"center\"> \n"
                           + "    <img src=\"/img/pipeline-elements/org.apache.streampipes.processor."
                           + "imageclassification.image-rectifier/icon.png\" width=\"150px;\"/>\n"
                           + "</p>\n" + "\n" + "***\n" + "\n" + "## Description\n" + "\n"
                           + "Image Rectification: Rectifies  + an image\n" + "Add a detailed description here\n" + "\n"
                           + "***\n" + "\n" + "## Required input\n" + "\n" + "\n" + "***\n" + "\n"
                           + "## Configuration\n" + "\n" + "Describe the configuration parameters here\n" + "\n"
                           + "### 1st parameter\n" + "\n" + "\n" + "### 2nd parameter\n" + "\n" + "## Output";

  private String expected = "---\n" + "id: org.apache.streampipes.processor.imageclassification.image-rectifier\n"
                            + "title: Image Rectifier\n" + "sidebar_label: Image Rectifier\n" + "---\n" + "\n" + "\n"
                            + "\n" + "<p align=\"center\"> \n"
                            + "    <img src=\"/img/pipeline-elements/org.apache.streampipes.processor."
                            + "imageclassification.image-rectifier/icon.png\" width=\"150px;\"/>\n"
                            + "</p>\n" + "\n" + "***\n" + "\n" + "## Description\n" + "\n"
                            + "Image Rectification: Rectifies  + an image\n" + "Add a detailed description here\n"
                            + "\n" + "***\n" + "\n" + "## Required input\n" + "\n" + "\n" + "***\n" + "\n"
                            + "## Configuration\n" + "\n" + "Describe the configuration parameters here\n" + "\n"
                            + "### 1st parameter\n" + "\n" + "\n" + "### 2nd parameter\n" + "\n" + "## Output";

  @Test
  public void testTitleRemover() {

    String removed = new MarkdownTitleRemover(content).removeTitle();

    assertEquals(expected, removed);

  }
}
