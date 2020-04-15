/* Copyright (C) 2012  Egon Willighagen <egonw@users.sf.net>
 *
 * License: new BSD
 */
package com.github.jqudt.onto.units;

import com.github.jqudt.Unit;
import com.github.jqudt.onto.UnitFactory;

public class TemperatureUnit {

	private TemperatureUnit() {}

	public static Unit KELVIN = UnitFactory.getInstance().getUnit("http://qudt.org/vocab/unit#Kelvin");
	public static Unit CELSIUS = UnitFactory.getInstance().getUnit("http://qudt.org/vocab/unit#DegreeCelsius");
	public static Unit FAHRENHEIT = UnitFactory.getInstance().getUnit("http://qudt.org/vocab/unit#DegreeFahrenheit");

}
