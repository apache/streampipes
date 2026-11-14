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

package org.apache.streampipes.couchdb.client;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Builds percent-encoded request paths.
 */
final class CouchPaths {

  private static final String DESIGN_PREFIX = "_design/";
  private static final String LOCAL_PREFIX = "_local/";
  private static final char[] HEX = "0123456789ABCDEF".toCharArray();

  private CouchPaths() {
  }

  /**
   * Encodes a single path segment; everything except RFC 3986 unreserved characters is escaped, including
   * {@code /} and {@code +}.
   */
  static String segment(String value) {
    var result = new StringBuilder(value.length() + 8);
    for (byte b : value.getBytes(StandardCharsets.UTF_8)) {
      int c = b & 0xFF;
      if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')
          || c == '-' || c == '.' || c == '_' || c == '~') {
        result.append((char) c);
      } else {
        result.append('%').append(HEX[c >> 4]).append(HEX[c & 0xF]);
      }
    }
    return result.toString();
  }

  static String database(String name) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Database name must not be empty");
    }
    return "/" + segment(name);
  }

  static String document(String databasePath, String id) {
    if (id == null || id.isEmpty()) {
      throw new IllegalArgumentException("Document id must not be empty");
    }
    if (id.startsWith(DESIGN_PREFIX) && id.length() > DESIGN_PREFIX.length()) {
      return databasePath + "/" + DESIGN_PREFIX + segment(id.substring(DESIGN_PREFIX.length()));
    }
    if (id.startsWith(LOCAL_PREFIX) && id.length() > LOCAL_PREFIX.length()) {
      return databasePath + "/" + LOCAL_PREFIX + segment(id.substring(LOCAL_PREFIX.length()));
    }
    return databasePath + "/" + segment(id);
  }

  /**
   * Encodes an attachment name; {@code /} separates segments as CouchDB allows it in attachment names.
   */
  static String attachment(String name) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Attachment name must not be empty");
    }
    var joiner = new StringJoiner("/");
    for (String part : name.split("/", -1)) {
      joiner.add(segment(part));
    }
    return joiner.toString();
  }

  static String query(Map<String, String> params) {
    if (params.isEmpty()) {
      return "";
    }
    var joiner = new StringJoiner("&", "?", "");
    params.forEach((key, value) -> joiner.add(key + "=" + segment(value)));
    return joiner.toString();
  }
}
