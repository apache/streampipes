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

import org.apache.streampipes.mail.template.generation.DefaultPlaceholders;
import org.apache.streampipes.mail.template.part.MailTemplatePart;
import org.apache.streampipes.mail.utils.MailUtils;

import java.util.Map;

public class TestMailTemplate extends AbstractMailTemplate {

  @Override
  protected String getTitle() {
    return "Mail settings verification";
  }

  @Override
  protected String getPreHeader() {
    return "Your " + MailUtils.extractAppName() + " mail configuration is working!";
  }

  @Override
  protected void addPlaceholders(Map<String, String> placeholders) {
    placeholders.put(DefaultPlaceholders.TEXT.key(), makeText());
  }

  @Override
  protected void addTemplateParts(Map<String, MailTemplatePart> templateParts) {
    templateParts.put(DefaultPlaceholders.INNER.key(), MailTemplatePart.MAIL_TEMPLATE_INNER_PLAIN);
  }

  private String makeText() {
    return "This is a test mail - if you receive this mail, your mail configuration is working.";
  }

}
