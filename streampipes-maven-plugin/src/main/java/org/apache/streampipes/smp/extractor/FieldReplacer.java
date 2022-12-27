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

package org.apache.streampipes.smp.extractor;

import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldReplacer {

  private static final String CHECK_FIELD_REGEX = "(DataSinkBuilder|ProcessingElementBuilder)\\"
      + ".create\\"
      + "((.*?),(.*?)\\)";
  private static final Pattern checkFieldPattern = Pattern.compile(CHECK_FIELD_REGEX);
  private String declareModelContent;
  private JavaClassSource source;


  public FieldReplacer(JavaClassSource source, String declareModelContent) {
    this.source = source;
    this.declareModelContent = declareModelContent;
  }

  public String replaceDeclareModelContent() {
    Matcher matcher = checkFieldPattern.matcher(this.declareModelContent);
    while (matcher.find()) {
      String match = matcher.group(2);
      if (!match.startsWith("\"")) {
        declareModelContent = declareModelContent.replaceFirst(match,
            getFieldValue(match));
      }
    }
    return declareModelContent;
  }

  private String getFieldValue(String fieldName) {
    return "\""
        + source.getField(fieldName).getStringInitializer()
        + "\"";
  }
}
