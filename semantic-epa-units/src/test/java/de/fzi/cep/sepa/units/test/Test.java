package de.fzi.cep.sepa.units.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.github.jqudt.Quantity;
import com.github.jqudt.Unit;
import com.github.jqudt.onto.UnitFactory;
import com.github.jqudt.onto.units.TemperatureUnit;

import de.fzi.cep.sepa.units.UnitCollector;

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
