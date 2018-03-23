/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.units.test;

import com.github.jqudt.Unit;
import org.streampipes.units.UnitProvider;

import java.util.List;

public class Test2 {

    public static void main(String[] args ) throws IllegalArgumentException, IllegalAccessException {
        List availableUnits = UnitProvider.INSTANCE.getAvailableUnits();

        Unit unit = (Unit)availableUnits.get(123);
        Unit unit2 = UnitProvider.INSTANCE.getUnitByLabel(unit.getLabel());

        System.out.println(unit.getResource().equals(unit2.getResource()));

        UnitProvider.INSTANCE.getUnitByLabel("Kelvin");
        UnitProvider.INSTANCE.getUnitByLabel("Degree Celsius12");


    }

}
