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

public class IotDbNameSanitizer {

  private final IotDbReservedKeywords reservedKeywords = new IotDbReservedKeywords();

  /**
   * Checks if a runtime name conflicts with any reserved keyword. If a conflict is found,
   * the runtime name is suffixed with an underscore.
   *
   * @param runtimeName the runtime name to be checked for conflicts with reserved keywords
   * @return the modified runtime name with underscore suffix if conflict exists, otherwise returns the original runtime name
   */
  public String renameReservedKeywords(String runtimeName){
    var containsKeyword = reservedKeywords.getAll()
        .stream()
        .anyMatch(keyword -> keyword.equalsIgnoreCase(runtimeName));

    return containsKeyword ? runtimeName + "_" : runtimeName;
  }
}
