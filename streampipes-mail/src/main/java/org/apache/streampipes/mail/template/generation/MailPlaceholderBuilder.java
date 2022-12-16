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
package org.apache.streampipes.mail.template.generation;

import java.util.HashMap;
import java.util.Map;

public class MailPlaceholderBuilder {

  private Map<String, String> placeholders;

  private MailPlaceholderBuilder(String title) {
    this.placeholders = new HashMap<>();
    this.placeholders.put(DefaultPlaceholders.TITLE.key(), title);
  }

  public static MailPlaceholderBuilder create(String title) {
    return new MailPlaceholderBuilder(title);
  }

  public MailPlaceholderBuilder add(String key, String value) {
    this.placeholders.put(key, value);

    return this;
  }

  public MailPlaceholderBuilder add(DefaultPlaceholders key, String value) {
    return add(key.key(), value);
  }

  public Map<String, String> build() {
    return placeholders;
  }
}
