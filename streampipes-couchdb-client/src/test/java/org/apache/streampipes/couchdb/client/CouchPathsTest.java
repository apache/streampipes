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

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CouchPathsTest {

  @Test
  public void encodesEverythingButUnreservedCharacters() {
    assertEquals("a%2Fb%2Bc%20d%3Fe%23f", CouchPaths.segment("a/b+c d?e#f"));
    assertEquals("abc-._~123", CouchPaths.segment("abc-._~123"));
    assertEquals("%C3%A4%E2%82%AC", CouchPaths.segment("ä€"));
  }

  @Test
  public void keepsDesignAndLocalPrefixes() {
    assertEquals("/db/_design/users", CouchPaths.document("/db", "_design/users"));
    assertEquals("/db/_local/x%2Fy", CouchPaths.document("/db", "_local/x/y"));
    assertEquals("/db/_design%2F", CouchPaths.document("/db", "_design/"));
    assertEquals("/db/sp%3Aadapter%2F1", CouchPaths.document("/db", "sp:adapter/1"));
  }

  @Test
  public void encodesDatabaseNames() {
    assertEquals("/data-lake", CouchPaths.database("data-lake"));
    assertEquals("/a%2Fb", CouchPaths.database("a/b"));
    assertThrows(IllegalArgumentException.class, () -> CouchPaths.database(""));
  }

  @Test
  public void keepsSlashesInAttachmentNames() {
    assertEquals("dir/my%20image.jpg", CouchPaths.attachment("dir/my image.jpg"));
  }

  @Test
  public void buildsQueryStrings() {
    var params = new LinkedHashMap<String, String>();
    params.put("key", "\"a b\"");
    params.put("end_key", "[\"t\",{}]");
    assertEquals("?key=%22a%20b%22&end_key=%5B%22t%22%2C%7B%7D%5D", CouchPaths.query(params));
    assertEquals("", CouchPaths.query(new LinkedHashMap<>()));
  }
}
