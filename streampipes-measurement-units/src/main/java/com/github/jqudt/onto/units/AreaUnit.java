/* Copyright (C) 2012  Egon Willighagen <egonw@users.sf.net>
 *
 * License: new BSD
 */
package com.github.jqudt.onto.units;

import com.github.jqudt.Unit;
import com.github.jqudt.onto.UnitFactory;

public class AreaUnit {

	public static Unit SQUARE_METER = UnitFactory
					.getInstance()
					.getUnit("http://qudt.org/vocab/unit#SquareMeter");

	public static Unit SQUARE_ANGSTROM = UnitFactory
					.getInstance()
					.getUnit("http://www.openphacts.org/units/SquareAngstrom");

	private AreaUnit() {}


}
