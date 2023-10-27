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

package org.apache.streampipes.model.connect.rules;

public enum TransformationRulePriority {

  ADD_TIMESTAMP(100),
  ADD_VALUE(110),

  RENAME(210),
  CREATE_NESTED(230),
  MOVE(235),
  DELETE(240),

  CHANGE_UNIT(310),
  TIMESTAMP_TRANSFORMATION(320),
  CORRECTION_VALUE(330),
  CHANGE_DATATYPE(340),

  REMOVE_DUPLICATES(410),
  EVENT_RATE(420);

  private final int code;

  TransformationRulePriority(int code) {
    this.code = code;
  }

  public int getCode() {
    return this.code;
  }
}
