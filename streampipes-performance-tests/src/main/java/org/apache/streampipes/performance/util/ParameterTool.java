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
package org.apache.streampipes.performance.util;

import org.apache.streampipes.performance.model.PerformanceTestSettings;

public class ParameterTool {

  public static PerformanceTestSettings fromArgs(String[] args) {
    return new PerformanceTestSettings(toInt(args[0]), toInt(args[1]), toInt(args[2]), toLong(args[3]), toLong
        (args[4]), toInt(args[5]), args[6]);
  }

  private static Long toLong(String arg) {
    return Long.parseLong(arg);
  }

  private static Integer toInt(String arg) {
    return Integer.parseInt(arg);
  }
}
