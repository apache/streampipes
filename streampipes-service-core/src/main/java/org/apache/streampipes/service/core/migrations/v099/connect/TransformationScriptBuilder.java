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

package org.apache.streampipes.service.core.migrations.v099.connect;

import java.util.stream.Collectors;

public class TransformationScriptBuilder {
  private StringBuilder sb;

  private boolean scriptActive;

  private TransformationScriptBuilder() {
    scriptActive = false;
  }

  public static TransformationScriptBuilder create() {
    TransformationScriptBuilder builder = new TransformationScriptBuilder();
    builder.sb = new StringBuilder();
    builder.sb.append("function transform(event, out, ctx) {\n");
    return builder;
  }

  public TransformationScriptBuilder appendLine(String line) {
    if (!line.startsWith("//")) {
      scriptActive = true;
    }

    sb.append("  ").append(line).append("\n");
    return this;
  }

  // Used to check if any script lines were added
  public boolean isScriptActive() {
    return scriptActive;
  }

  public String build() {
    sb.append("  out.collect(event);\n");
    sb.append("}");
    return sb.toString();
  }


  /**
   * Put the script body into a loop if the adapter used the old array field key.
   * This executes the script for each item in the array field, emitting one event per item.
   * Is only executed if the selected format was json array key.
   */
  public static String wrapWithArrayFieldLoopIfNeeded(String scriptBody, String arrayFieldKey) {
    if (arrayFieldKey == null || arrayFieldKey.isBlank()) {
      return scriptBody;
    }

    var escapedKey = escapeJsString(arrayFieldKey);

    var removedFirstAndLastLine = scriptBody.lines()
                            .skip(1)
                            .limit(Math.max(0, scriptBody.lines().count() - 3))
                            .collect(Collectors.joining("\n"));

    return """
      // Migration wrapper:
      // If event['%s'] is an array, emit one event per entry (fan-out). Otherwise process the event normally.
      function transform(event, out, ctx) {
        const items = event['%s'];
        if (Array.isArray(items)) {
          for (const item of items) {
            const child = process(item);
            out.collect(child);
          }
          return;
        }
      }

      function process(event) {
      %s
        return event;
      }
      """.formatted(escapedKey, escapedKey, indent(removedFirstAndLastLine));
  }


  private static String escapeJsString(String s) {
    return s.replace("\\", "\\\\").replace("'", "\\'");
  }

  private static String indent(String text) {
    String pad = " ".repeat(2);
    return text.lines()
               .map(line -> pad + line)
               .reduce((a, b) -> a + "\n" + b)
               .orElse("");
  }

}
