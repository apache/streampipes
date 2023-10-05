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

package org.apache.streampipes.connect.shared.preprocessing.utils;

import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Utils {

  public static String getLastKey(String s) {
    String[] list = s.split("\\.");
    if (list.length == 0) {
      return s;
    } else {
      return list[list.length - 1];
    }
  }

  public static List<String> toKeyArray(String s) {
    String[] split = s.split("\\.");
    if (split.length == 0) {
      return List.of(s);
    } else {
      return Arrays.asList(split);
    }
  }

  public static List<TransformationRuleDescription> sortByPriority(List<TransformationRuleDescription> rules) {
    return rules
        .stream()
        .sorted(Comparator.comparingInt(TransformationRuleDescription::getRulePriority))
        .toList();
  }

}
