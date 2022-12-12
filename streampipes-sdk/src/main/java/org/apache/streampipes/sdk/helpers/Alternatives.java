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
package org.apache.streampipes.sdk.helpers;

import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;

public class Alternatives {

  public static StaticPropertyAlternative from(Label label, StaticProperty staticProperty) {
    StaticPropertyAlternative alternative = from(label);
    alternative.setStaticProperty(staticProperty);
    return alternative;
  }

  public static StaticPropertyAlternative from(Label label, StaticProperty staticProperty, boolean selected) {
    StaticPropertyAlternative alternative = from(label, selected);
    alternative.setStaticProperty(staticProperty);
    return alternative;
  }

  public static StaticPropertyAlternative from(Label label) {
    StaticPropertyAlternative alternative = new StaticPropertyAlternative(label.getInternalId(),
        label.getLabel(), label.getInternalId());
    alternative.setIndex(0);
    return alternative;
  }

  public static StaticPropertyAlternative from(Label label, boolean selected) {
    StaticPropertyAlternative alternative = new StaticPropertyAlternative(label.getInternalId(),
        label.getLabel(), label.getInternalId());
    alternative.setIndex(0);
    alternative.setSelected(selected);
    return alternative;
  }
}
