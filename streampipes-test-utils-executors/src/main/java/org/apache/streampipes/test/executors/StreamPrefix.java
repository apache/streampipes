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
package org.apache.streampipes.test.executors;

/**
 * Provides utility methods to append stream prefixes to property key values. This class is used to be used in unit
 * tests. Consider integrating prefix configuration into the application's logic, possibly within the TestConfiguration
 * class
 */
public class StreamPrefix {
  public static final String S0 = "s0";
  public static final String S1 = "s1";

  /**
   * Appends the S0 prefix to a given property value.
   *
   * @param propertyValue
   *          The value to which the S0 prefix will be appended.
   * @return A string with the S0 prefix followed by the property value.
   */
  public static String s0(String propertyValue) {
    return addPrefix(S0, propertyValue);
  }

  /**
   * Appends the S1 prefix to a given property value.
   *
   * @param propertyValue
   *          The value to which the S1 prefix will be appended.
   * @return A string with the S1 prefix followed by the property value.
   */
  public static String s1(String propertyValue) {
    return addPrefix(S1, propertyValue);
  }

  private static String addPrefix(String prefix, String propertyValue) {
    return prefix + "::" + propertyValue;
  }
}
