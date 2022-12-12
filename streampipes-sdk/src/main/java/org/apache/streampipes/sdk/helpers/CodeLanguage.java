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

package org.apache.streampipes.sdk.helpers;

public enum CodeLanguage {
  None("Write your custom logic here"),
  Python("# Enter your python code here\n\n"
      + "print('Hello, StreamPipes!')"),
  Javascript("function process(event) {\n"
      + "    // do processing here.\n"
      + "    // return processed event.\n"
      + "   // Type 'event' and press Ctrl+Space to see available fields.\n"
      + "   // Example: \n"
      + "    return {timestamp: event.timestamp, tempInCelsius: (event.tempInKelvin - 273.15)};\n"
      + "}");

  private String defaultSkeleton;

  CodeLanguage(String defaultSkeleton) {
    this.defaultSkeleton = defaultSkeleton;
  }

  public String getDefaultSkeleton() {
    return defaultSkeleton;
  }
}
