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
package com.github.jqudt.onto.units;

import com.github.jqudt.Unit;
import com.github.jqudt.onto.UnitFactory;

public class AreaUnit {

	private AreaUnit() {}

	public static Unit SQUARE_METER = UnitFactory.getInstance().getUnit("http://qudt.org/vocab/unit#SquareMeter");
	public static Unit SQUARE_ANGSTROM = UnitFactory.getInstance().getUnit("http://www.openphacts.org/units/SquareAngstrom");

}
