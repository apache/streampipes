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
package org.apache.streampipes.commons.resources;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

public class Resources {

  public static String asString(String resourceName, Charset charset) throws IOException {
    var loader = getLoader();
    return IOUtils.resourceToString(resourceName, charset, loader);
  }

  public static URL asUrl(String resourceName) throws IOException {
    var loader = getLoader();
    return IOUtils.resourceToURL(resourceName, loader);
  }

  private static ClassLoader getLoader() {
    return firstNonNull(Thread.currentThread().getContextClassLoader(), Resources.class.getClassLoader());
  }

  private static <T> T firstNonNull(T first, T second) {
    if (first != null) {
      return first;
    } else if (second != null) {
      return second;
    } else {
      throw new NullPointerException("Both parameters are null");
    }
  }
}
