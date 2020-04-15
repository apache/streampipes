/* Copyright (C) 2012  Egon Willighagen <egonw@users.sf.net>
 *
 * License: new BSD
 */
package com.github.jqudt.onto.units;

import com.github.jqudt.Unit;
import com.github.jqudt.onto.UnitFactory;

public class MassUnit {

  public static Unit KILOGRAM = UnitFactory.getInstance().getUnit("http://qudt.org/vocab/unit#Kilogram");
  public static Unit GRAM = UnitFactory.getInstance().getUnit("http://qudt.org/vocab/unit#Gram");
  public static Unit MILLIGRAM = UnitFactory.getInstance().getUnit("http://www.openphacts.org/units/Milligram");
  public static Unit MICROGRAM = UnitFactory.getInstance().getUnit("http://www.openphacts.org/units/Microgram");
  public static Unit NANOGRAM = UnitFactory.getInstance().getUnit("http://www.openphacts.org/units/Nanogram");
  public static Unit PICOGRAM = UnitFactory.getInstance().getUnit("http://www.openphacts.org/units/Picogram");
  public static Unit FEMTOGRAM = UnitFactory.getInstance().getUnit("http://www.openphacts.org/units/Femtogram");

  private MassUnit() {
  }

}
