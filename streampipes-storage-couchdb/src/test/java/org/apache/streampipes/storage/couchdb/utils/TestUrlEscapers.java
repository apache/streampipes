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

package org.apache.streampipes.storage.couchdb.utils;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class TestUrlEscapers {

  @Test
  public void testUrlPathEscaper() {
    String type = "typeDummy";
    String test = "startkey=[\"" + type + "\"]&endkey=[\"" + type + "\",{}]&include_docs=true";
    String escaped = Utils.escapePathSegment(test);
    String expected = "startkey=%5B%22typeDummy%22%5D&endkey=%5B%22typeDummy%22,%7B%7D%5D&include_docs=true";
    assertEquals(expected, escaped);
  }

  @Test
  public void testUrlPathEscaper2() {
    String from = "fromDummy";
    String test = "startkey=[\"" + from + "\"]&endkey=[\"" + from + "\", {}]&group=true";
    String escaped = Utils.escapePathSegment(test);
    String expected2 = "startkey=%5B%22fromDummy%22%5D&endkey=%5B%22fromDummy%22,%20%7B%7D%5D&group=true";
    assertEquals(expected2, escaped);
  }
}
