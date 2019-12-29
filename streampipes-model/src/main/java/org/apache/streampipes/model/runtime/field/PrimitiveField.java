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
package org.apache.streampipes.model.runtime.field;

public class PrimitiveField extends AbstractField<Object> {

  public PrimitiveField(String fieldNameIn, String fieldNameOut, Object value) {
    super(fieldNameIn, fieldNameOut, value);
  }

  public String getAsString() {
    return asString(value);
  }

  public Long getAsLong() {
    return Long.parseLong(asString(value));
  }

  public Integer getAsInt() {
    return Integer.parseInt(asString(value));
  }

  public Float getAsFloat() {
    return Float.parseFloat(asString(value));
  }

  public Boolean getAsBoolean() {
    return Boolean.parseBoolean(asString(value));
  }

  public Double getAsDouble() {
    return Double.parseDouble(asString(value));
  }

}
