/* Copyright (C) 2012  Egon Willighagen <egonw@users.sf.net>
 *
 * License: new BSD
 */
package com.github.jqudt.onto.units;

import com.github.jqudt.Unit;
import com.github.jqudt.onto.UnitFactory;

public class LengthUnit {

  private LengthUnit() {
  }

  public static Unit NM = UnitFactory
          .getInstance()
          .getUnit("http://www.openphacts.org/units/Nanometer");

}
