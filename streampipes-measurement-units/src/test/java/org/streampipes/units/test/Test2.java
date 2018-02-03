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
