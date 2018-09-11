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


    private List<String> key;
    private  String fromUnit;
    private  String toUnit;

    public UnitTransformationRule(List<String> key, String fromUnit, String toUnit) {
        this.key = key;
        this.fromUnit = fromUnit;
        this.toUnit = toUnit;
    }

    @Override
    public Map<String, Object> transform(Map<String, Object> event) {
        return transform(event, key);
    }

    private Map<String, Object> transform(Map<String, Object> event, List<String> keys) {

        if (keys.size() == 1) {
            try {

                double value = (double) event.get(keys.get(0));

                Unit unitTypeFrom = UnitProvider.INSTANCE.getUnitByLabel(fromUnit);
                Unit unitTypeTo = UnitProvider.INSTANCE.getUnitByLabel(toUnit);

                Quantity obs = new Quantity(value, unitTypeFrom);
                double newValue = obs.convertTo(unitTypeTo).getValue();

                event.put(keys.get(0), newValue);
                logger.info(String.valueOf( event.get(keys.get(0))));
            } catch (ClassCastException e) {
                logger.error(e.toString());
            } catch (IllegalAccessException e) {
                logger.error(e.toString());
            }
            return event;

        } else {
            String key = keys.get(0);
            List<String> newKeysTmpList = keys.subList(1, keys.size());

            Map<String, Object> newSubEvent =
                    transform((Map<String, Object>) event.get(keys.get(0)), newKeysTmpList);

            event.remove(key);
            event.put(key, newSubEvent);

            return event;
        }

    }
}
