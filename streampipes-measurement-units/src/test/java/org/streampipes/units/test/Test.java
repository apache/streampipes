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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.github.jqudt.Quantity;
import com.github.jqudt.Unit;
import com.github.jqudt.onto.UnitFactory;
import com.github.jqudt.onto.units.TemperatureUnit;

import org.streampipes.units.UnitCollector;

public class Test {

	public static void main(String[] args ) throws IllegalArgumentException, IllegalAccessException {
		Quantity temp = new Quantity(20, TemperatureUnit.CELSIUS);
		System.out.println(temp + " = " +  temp.convertTo(TemperatureUnit.KELVIN));
		
		
		List<String> units = UnitFactory.getInstance().getURIs("http://qudt.org/schema/qudt#ThermodynamicsUnit");
		units.forEach(u -> System.out.println("\"" +u.replace("http://qudt.org/schema/qudt#", "") +"\","));
//		
//		Unit unit = UnitFactory.getInstance().getUnit("http://qudt.org/vocab/unit#BaseUnit");
//		System.out.println(uniSet<E>etLabel());
		
		Set<Unit> us = new UnitCollector().getAvailableUnits();
		System.out.println(us.size());
		//us.forEach(u -> System.out.println(u.getLabel()));
		
		List<Unit> all = new ArrayList<>();
		all.addAll(us);
		//all.get(3).
		
	}
}
