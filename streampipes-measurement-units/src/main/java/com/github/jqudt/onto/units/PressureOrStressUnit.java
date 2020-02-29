/* Copyright (C) 2012  Egon Willighagen <egonw@users.sf.net>
 *
 * License: new BSD
 */
package com.github.jqudt.onto.units;

import com.github.jqudt.Unit;
import com.github.jqudt.onto.UnitFactory;

public class PressureOrStressUnit {
    private PressureOrStressUnit() {}

    public static Unit PASCAL = UnitFactory.getInstance().getUnit("http://qudt.org/vocab/unit#Pascal");

}
