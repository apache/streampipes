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

import j2html.tags.Tag;
import org.apache.streampipes.mail.utils.MailUtils;

import static j2html.TagCreator.a;

public class LinkPart extends AbstractPart{

  private final String path;
  private final String text;

  public LinkPart(String path,
                  String text) {
    this.path = path;
    this.text = text;
  }

  @Override
  public Tag<?> toTag() {
    return a().withHref(makeFullPath()).withText(text);
  }

  private String makeFullPath() {
    return MailUtils.extractBaseUrl() + path;
  }
}
