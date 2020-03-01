/* Copyright (C) 2012  Egon Willighagen <egonw@users.sf.net>
 *
 * License: new BSD
 */
package com.github.jqudt.onto.units;

import com.github.jqudt.Unit;
import com.github.jqudt.onto.UnitFactory;

public class VolumeUnit {

	private VolumeUnit() {}

	public static Unit LITER = UnitFactory.getInstance().getUnit("http://qudt.org/vocab/unit#Liter");
	public static Unit MICROLITER = UnitFactory.getInstance().getUnit("http://www.openphacts.org/units/Microliter");
	public static Unit MILLILITER = UnitFactory.getInstance().getUnit("http://www.openphacts.org/units/Milliliter");

}
