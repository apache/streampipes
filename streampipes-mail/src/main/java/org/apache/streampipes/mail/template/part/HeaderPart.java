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
package org.apache.streampipes.mail.template.part;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import j2html.tags.ContainerTag;

import java.io.IOException;

import static j2html.TagCreator.*;

public class HeaderPart extends AbstractPart {

  private String title;

  public HeaderPart(String title) {
    this.title = title;
  }

  @Override
  public ContainerTag toTag() {
    return head(title(title), style(readCssFileFromResources()));
  }

  private String readCssFileFromResources() {
    try {
      return Resources.toString(Resources.getResource("style.css"), Charsets.UTF_8);
    } catch (IOException e) {
      return "";
    }
  }
}
