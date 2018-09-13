/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.streampipes.connect.adapter.generic.transform;

import com.github.jqudt.Quantity;
import com.github.jqudt.Unit;
import com.github.jqudt.onto.units.TemperatureUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.adapter.Adapter;
import org.streampipes.units.UnitProvider;

import java.util.List;
import java.util.Map;

public class UnitTransformationRule implements TransformationRule {

    private static Logger logger = LoggerFactory.getLogger(Adapter.class);

    private List<String> eventPropertyId;
    private Unit unitTypeFrom;
    private Unit unitTypeTo;

    public UnitTransformationRule(List<String> eventPropertyId, String fromUnit, String toUnit) {
        this.eventPropertyId = eventPropertyId;
        this.unitTypeFrom = UnitProvider.INSTANCE.getUnitByLabel(fromUnit);
        this.unitTypeTo = UnitProvider.INSTANCE.getUnitByLabel(toUnit);
    }

    @Override
    public Map<String, Object> transform(Map<String, Object> event) {
        return transform(event, eventPropertyId);
    }

    private Map<String, Object> transform(Map<String, Object> event, List<String> eventPropertyIds) {

        if (eventPropertyIds.size() == 1) {
            try {
                double value = (double) event.get(eventPropertyIds.get(0));

                Quantity obs = new Quantity(value, unitTypeFrom);
                double newValue = obs.convertTo(unitTypeTo).getValue();

                event.put(eventPropertyIds.get(0), newValue);
                logger.info(String.valueOf( event.get(eventPropertyIds.get(0))));
            } catch (ClassCastException e) {
                logger.error(e.toString());
            } catch (IllegalAccessException e) {
                logger.error(e.toString());
            }
            return event;

        } else {
            String key = eventPropertyIds.get(0);
            List<String> newKeysTmpList = eventPropertyIds.subList(1, eventPropertyIds.size());

            Map<String, Object> newSubEvent =
                    transform((Map<String, Object>) event.get(eventPropertyIds.get(0)), newKeysTmpList);

            event.remove(key);
            event.put(key, newSubEvent);

            return event;
        }

    }
}
