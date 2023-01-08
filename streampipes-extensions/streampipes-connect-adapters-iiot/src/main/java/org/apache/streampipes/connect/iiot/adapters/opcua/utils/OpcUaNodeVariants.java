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

package org.apache.streampipes.connect.iiot.adapters.opcua.utils;

import jakarta.annotation.Nullable;

/**
 * Enum that maintains different variants of OPC UA nodes. <br>
 * Not yet completed. <br>
 */
public enum OpcUaNodeVariants {
  Property(68),
  EUInformation(887);

  // ID as specified in OPC UA standard
  private final int id;

  private OpcUaNodeVariants(int id) {
    this.id = id;
  }

  public int getId() {
    return this.id;
  }

  @Nullable
  public static OpcUaNodeVariants from(int id) {
    switch (id) {
      case 68:
        return Property;
      case 887:
        return EUInformation;
      default:
        return null;
    }
  }
}
