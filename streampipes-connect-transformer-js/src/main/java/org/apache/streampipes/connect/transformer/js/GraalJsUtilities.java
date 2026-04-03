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

package org.apache.streampipes.connect.transformer.js;

final class GraalJsUtilities {

  private static final String UTILITIES_SOURCE = """
      Object.defineProperty(globalThis, 'utils', {
        value: Object.freeze({
          addTimestamp(event, fieldName = 'timestamp') {
            event[fieldName] = Date.now();
            return event;
          },
          rename(event, oldName, newName) {
            event[newName] = event[oldName];
            delete event[oldName];
            return event;
          },
          remove(event, fieldName) {
            delete event[fieldName];
            return event;
          },
          parseTimestamp(event, sourceField, targetField = 'timestamp') {
            const parsedTimestamp = new Date(event[sourceField]).getTime();
            if (Number.isNaN(parsedTimestamp)) {
              throw new Error(`Could not parse timestamp from field '${sourceField}'`);
            }
            event[targetField] = parsedTimestamp;
            return event;
          }
        }),
        writable: false,
        configurable: false
      });
      """;

  private GraalJsUtilities() {
  }

  static String source() {
    return UTILITIES_SOURCE;
  }
}
