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

public class Expressions {

  public static PropertyExpression property(String propertyName) {
    //return new PropertyExpression(propertyName);
    return null;
  }

  public static PropertyExpression property(SiddhiStreamSelector selector, String propertyName) {
    return null;
  }

  public static PropertyExpression property(EventPropertyDef propertyDef) {
    return null;
  }

  public static Expression as(Expression property, String targetName) {
    return null;
  }
}
