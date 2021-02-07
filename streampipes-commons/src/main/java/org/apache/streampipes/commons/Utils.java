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

package org.apache.streampipes.commons;

import org.apache.commons.lang.RandomStringUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {

  public static List<URI> createURI(String... uris) {
    List<URI> result = new ArrayList<>();
    for (String uri : uris) {
      result.add(URI.create(uri));
    }
    return result;
  }

  @SafeVarargs
  public static <T> List<T> createList(T... objects) {
    List<T> result = new ArrayList<>();
    Collections.addAll(result, objects);
    return result;
  }

  public static String getRandomString() {
    return RandomStringUtils.randomAlphabetic(10);
  }

}
