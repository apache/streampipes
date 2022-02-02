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
package org.apache.streampipes.wrapper.siddhi.query.expression;

import org.apache.streampipes.wrapper.siddhi.constants.SiddhiStreamSelector;
import org.apache.streampipes.wrapper.siddhi.model.EventPropertyDef;
import org.apache.streampipes.wrapper.siddhi.utils.SiddhiUtils;

public class PropertyExpression extends PropertyExpressionBase {

  private final String siddhiPropertyName;

  public PropertyExpression(String streamName, String property) {
    this.siddhiPropertyName = join(".", streamName, property);
  }

  public PropertyExpression(String streamName, String property, String eventIndex) {
    this.siddhiPropertyName = join(".", streamName + "[" + eventIndex + "]", property);
  }

  public PropertyExpression(String property) {
    this.siddhiPropertyName = property;
  }

  public PropertyExpression(EventPropertyDef propertyDef) {
    this.siddhiPropertyName = propertyDef.getSelectorPrefix() + propertyDef.getFieldName();
  }

  public PropertyExpression(SiddhiStreamSelector selector, String propertyName) {
    this.siddhiPropertyName = selector.getPrefix() + propertyName;
  }

  @Override
  public String toSiddhiEpl() {
    return SiddhiUtils.prepareProperty(siddhiPropertyName);
  }
}
