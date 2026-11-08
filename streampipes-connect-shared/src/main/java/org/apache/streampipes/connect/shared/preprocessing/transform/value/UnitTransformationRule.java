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
import org.apache.streampipes.units.UnitConversion;
import org.apache.streampipes.units.UnitConverter;
import org.apache.streampipes.units.catalog.UnitCatalog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Converts a numeric event property from one measurement unit to another.
 *
 * <p>Both units are resolved and checked for compatibility when the rule is created, so an
 * unknown unit or a conversion between different quantities fails when the adapter starts.</p>
 */
public class UnitTransformationRule extends SupportsNestedTransformationRule {

  private static final Logger logger = LoggerFactory.getLogger(UnitTransformationRule.class);

  private final List<String> eventKey;
  private final UnitConversion conversion;

  public UnitTransformationRule(UnitTransformRuleDescription description) {
    this(
        Utils.toKeyArray(description.getRuntimeKey()),
        description.getFromUnitRessourceURL(),
        description.getToUnitRessourceURL()
    );
  }

  /**
   * @throws org.apache.streampipes.units.UnknownMeasurementUnitException if a unit is unknown
   * @throws IllegalArgumentException if the units measure different quantities
   */
  public UnitTransformationRule(List<String> keys,
                                String fromUnitRessourceURL,
                                String toUnitRessourceURL) {
    var catalog = UnitCatalog.builtIn();
    this.conversion = UnitConverter.between(
        catalog.get(fromUnitRessourceURL),
        catalog.get(toUnitRessourceURL)
    );
    this.eventKey = keys;
  }

  @Override
  protected List<String> getEventKeys() {
    return eventKey;
  }

  @Override
  protected void applyTransformation(Map<String, Object> event, List<String> eventKey) {
    var key = eventKey.get(0);
    if (!event.containsKey(key) || event.get(key) == null) {
      return;
    }

    var originalValue = event.get(key);
    try {
      double value = Double.parseDouble(String.valueOf(originalValue));

      event.put(key, conversion.applyAsDouble(value));
    } catch (NumberFormatException e) {
      logger.debug(
          "Could not convert value '{}' from unit '{}' to '{}' for event property '{}'",
          originalValue,
          conversion.from(),
          conversion.to(),
          key
      );
    }
  }
}
