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

package org.apache.streampipes.connect.shared.preprocessing.transform.value;

import org.apache.streampipes.connect.shared.preprocessing.SupportsNestedTransformationRule;
import org.apache.streampipes.connect.shared.preprocessing.utils.Utils;
import org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription;
import org.apache.streampipes.units.UnitProvider;

import com.github.jqudt.Quantity;
import com.github.jqudt.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class UnitTransformationRule extends SupportsNestedTransformationRule {

  private static final Logger logger = LoggerFactory.getLogger(UnitTransformationRule.class);

  private final List<String> eventKey;
  private final Unit unitTypeFrom;
  private final Unit unitTypeTo;

  public UnitTransformationRule(UnitTransformRuleDescription description) {
    this.unitTypeFrom = UnitProvider.INSTANCE.getUnit(description.getFromUnitRessourceURL());
    this.unitTypeTo = UnitProvider.INSTANCE.getUnit(description.getToUnitRessourceURL());
    this.eventKey = Utils.toKeyArray(description.getRuntimeKey());
  }

  public UnitTransformationRule(List<String> keys,
                                String fromUnitRessourceURL, String toUnitRessourceURL) {
    this.unitTypeFrom = UnitProvider.INSTANCE.getUnit(fromUnitRessourceURL);
    this.unitTypeTo = UnitProvider.INSTANCE.getUnit(toUnitRessourceURL);
    this.eventKey = keys;
  }

  @Override
  protected List<String> getEventKeys() {
    return eventKey;
  }

  @Override
  protected void applyTransformation(Map<String, Object> event, List<String> eventKey) {
    try {
      double value = Double.parseDouble(String.valueOf(event.get(eventKey.get(0))));

      Quantity obs = new Quantity(value, unitTypeFrom);
      double newValue = obs.convertTo(unitTypeTo).getValue();

      event.put(eventKey.get(0), newValue);
    } catch (ClassCastException | IllegalAccessException e) {
      logger.error(e.toString());
    }
  }
}
