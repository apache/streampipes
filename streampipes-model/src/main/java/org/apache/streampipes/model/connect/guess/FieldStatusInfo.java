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

package org.apache.streampipes.model.connect.guess;

import java.util.Objects;

public class FieldStatusInfo {

  private FieldStatus fieldStatus;
  private String additionalInfo;
  private boolean changesRequired;

  public FieldStatusInfo() {
  }

  public static FieldStatusInfo good() {
    var info = new FieldStatusInfo();
    info.setFieldStatus(FieldStatus.GOOD);
    return info;
  }

  public static FieldStatusInfo bad(String additionalInfo,
                                    boolean changesRequired) {
    var info = new FieldStatusInfo();
    info.setFieldStatus(FieldStatus.BAD);
    info.setAdditionalInfo(additionalInfo);
    info.setChangesRequired(changesRequired);

    return info;
  }

  public FieldStatus getFieldStatus() {
    return fieldStatus;
  }

  public void setFieldStatus(FieldStatus fieldStatus) {
    this.fieldStatus = fieldStatus;
  }

  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  public boolean isChangesRequired() {
    return changesRequired;
  }

  public void setChangesRequired(boolean changesRequired) {
    this.changesRequired = changesRequired;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FieldStatusInfo that = (FieldStatusInfo) o;
    return changesRequired == that.changesRequired && fieldStatus == that.fieldStatus && Objects.equals(
        additionalInfo, that.additionalInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fieldStatus, additionalInfo, changesRequired);
  }
}
