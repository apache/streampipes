/* Copyright (C) 2012  Egon Willighagen <egonw@users.sf.net>
 *
 * License: new BSD
 */
package com.github.jqudt.onto.units;

import com.github.jqudt.Unit;
import com.github.jqudt.onto.UnitFactory;

public class ConcentrationUnit {

	public static Unit MOLE_PER_CUBIC_METER = UnitFactory.getInstance().getUnit("http://qudt.org/vocab/unit#MolePerCubicMeter");

	public static Unit MOLAR = UnitFactory.getInstance().getUnit("http://www.openphacts.org/units/Molar");
	public static Unit MILLIMOLAR = UnitFactory.getInstance().getUnit("http://www.openphacts.org/units/Millimolar");
	public static Unit NANOMOLAR = UnitFactory.getInstance().getUnit("http://www.openphacts.org/units/Nanomolar");
	public static Unit MICROMOLAR = UnitFactory.getInstance().getUnit("http://www.openphacts.org/units/Micromolar");

	public static Unit GRAM_PER_LITER = UnitFactory.getInstance().getUnit("http://www.openphacts.org/units/GramPerLiter");
	public static Unit MICROGRAM_PER_MILLILITER = UnitFactory.getInstance().getUnit("http://www.openphacts.org/units/MicrogramPerMilliliter");
	public static Unit PICOGRAM_PER_MILLILITER = UnitFactory.getInstance().getUnit("http://www.openphacts.org/units/PicogramPerMilliliter");

	private ConcentrationUnit() {}

}
