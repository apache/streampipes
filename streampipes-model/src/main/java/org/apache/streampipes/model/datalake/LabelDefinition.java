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

public class LabelDefinition {

  private String classLabel;
  private String labelColumn;

  public LabelDefinition(String classLabel, String labelColumn) {
    this.classLabel = classLabel;
    this.labelColumn = labelColumn;
  }

  public String getClassLabel() {
    return classLabel;
  }

  public void setClassLabel(String classLabel) {
    this.classLabel = classLabel;
  }

  public String getLabelColumn() {
    return labelColumn;
  }

  public void setLabelColumn(String labelColumn) {
    this.labelColumn = labelColumn;
  }
}
