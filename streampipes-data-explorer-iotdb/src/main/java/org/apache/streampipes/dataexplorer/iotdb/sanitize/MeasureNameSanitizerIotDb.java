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
package org.apache.streampipes.dataexplorer.iotdb.sanitize;

/**
 * Ensures that measurement names comply with IoTDB path specifications.
 * <p>
 * Measurement names in IoTDB paths should consist of characters that are either:
 * <ul>
 * <li>Word characters (letters, digits, or underscore), or</li>
 * <li>Chinese characters (Unicode range: u2E80-u9FFF).</li>
 * </ul>
 * <p>
 * {@see <a href="https://iotdb.apache.org/UserGuide/latest/Basic-Concept/Data-Model-and-Terminology.html#path">IoTDB
 * Data Model and Terminology</a>}
 */
public class MeasureNameSanitizerIotDb {

  /**
   * Sanitizes the given measurement name to comply with IoTDB path specifications.
   *
   * @param measureName
   *          The measurement name to be sanitized
   * @return The sanitized measurement name with non-compliant characters replaced by underscores
   */
  public String sanitize(String measureName) {

    // matches any character that is not a word character (\w, equivalent to [a-zA-Z0-9_]) or
    // a Chinese character (\u2E80-\u9FFF).
    // according to https://iotdb.apache.org/UserGuide/latest/Basic-Concept/Data-Model-and-Terminology.html#path
    String allowedCharacterPattern = "[^\\w\\u2E80-\\u9FFF]";

    return measureName.replaceAll(allowedCharacterPattern, "_");
  }
}
