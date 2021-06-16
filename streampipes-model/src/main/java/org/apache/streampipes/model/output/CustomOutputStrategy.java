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

package org.apache.streampipes.model.output;

import java.util.ArrayList;
import java.util.List;

public class CustomOutputStrategy extends OutputStrategy {

  private static final long serialVersionUID = -5858193127308435472L;

  private List<String> selectedPropertyKeys;

  private boolean outputRight;

  private List<String> availablePropertyKeys;

  public CustomOutputStrategy() {
    super();
    this.selectedPropertyKeys = new ArrayList<>();
    this.availablePropertyKeys = new ArrayList<>();
  }

  public CustomOutputStrategy(boolean outputRight) {
    this();
    this.outputRight = outputRight;
  }

  public CustomOutputStrategy(CustomOutputStrategy other) {
    super(other);
    this.selectedPropertyKeys = other.getSelectedPropertyKeys();
    this.availablePropertyKeys = other.getAvailablePropertyKeys();
    this.outputRight = other.isOutputRight();
  }

  public CustomOutputStrategy(List<String> selectedPropertyKeys) {
    this();
    this.selectedPropertyKeys = selectedPropertyKeys;
  }

  public boolean isOutputRight() {
    return outputRight;
  }

  public void setOutputRight(boolean outputRight) {
    this.outputRight = outputRight;
  }

  public List<String> getSelectedPropertyKeys() {
    return selectedPropertyKeys;
  }

  public void setSelectedPropertyKeys(List<String> selectedPropertyKeys) {
    this.selectedPropertyKeys = selectedPropertyKeys;
  }

  public List<String> getAvailablePropertyKeys() {
    return availablePropertyKeys;
  }

  public void setAvailablePropertyKeys(List<String> availablePropertyKeys) {
    this.availablePropertyKeys = availablePropertyKeys;
  }
}
