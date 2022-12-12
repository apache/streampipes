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

package org.apache.streampipes.model.datalake;

public class DataLakeRetentionPolicy {

  private String name;
  private String durationLiteral;
  private boolean isDefault;

  public DataLakeRetentionPolicy(String name, int duration, String durationUnit, boolean isDefault) {
    this.name = name;
    this.durationLiteral = duration + durationUnit;
    this.isDefault = isDefault;
  }

  public DataLakeRetentionPolicy(String name) {
    this.name = name;
    this.isDefault = false;
    setDurationLiteralToInfinity();
  }

  public DataLakeRetentionPolicy(String name, String durationLiteral, boolean isDefault) {
    this.name = name;
    this.durationLiteral = durationLiteral;
    this.isDefault = isDefault;
  }

  public String getDurationLiteral() {
    return durationLiteral;
  }

  public void setDurationLiteral(int duration, String durationUnit) {
    this.durationLiteral = duration + durationUnit;
  }

  public void setDurationLiteralToInfinity() {
    this.durationLiteral = "INF";
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isDefault() {
    return isDefault;
  }

  public void setDefault(boolean isDefault) {
    this.isDefault = isDefault;
  }

  @Override
  public String toString() {
    return String.format("DataLakeRetentionPolicy{name='%s', durationLiteral='%s'}", name, durationLiteral);
  }
}
