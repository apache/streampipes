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
package org.apache.streampipes.mail.template;

import org.apache.streampipes.mail.template.generation.MailTemplateBuilder;

import java.util.Map;

public class CustomMailTemplate extends AbstractMailTemplate {

  private final String title;
  private final String preheader;
  private final String content;

  public CustomMailTemplate(String title, String preheader, String content) {
    this.title = title;
    this.preheader = preheader;
    this.content = content;
  }

  @Override
  protected String getTitle() {
    return title;
  }

  @Override
  protected String getPreHeader() {
    return preheader;
  }

  @Override
  protected void addPlaceholders(Map<String, String> placeholders) {
  }

  @Override
  protected void configureTemplate(MailTemplateBuilder builder) {
    builder.withInnerPart(content);
  }
}
