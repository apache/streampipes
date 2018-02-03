/* Copyright (C) 2012  Egon Willighagen <egonw@users.sf.net>
 *
 * License: new BSD
 */
package com.github.jqudt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Quantity {

	static Logger LOG = LoggerFactory.getLogger(Quantity.class);

	private double value;
	private Unit unit;

	private Quantity() {}

	public Quantity(double value, Unit unit) {
		this.value = value;
		this.unit = unit;
	}

	public double getValue() {
		return value;
	}
	private void setValue(double value) {
		this.value = value;
	}
	public Unit getUnit() {
		return unit;
	}
	private void setUnit(Unit unit) {
		this.unit = unit;
	}

	public Quantity convertTo(Unit newUnit) throws IllegalArgumentException, IllegalAccessException {
		if (newUnit == null) {
			LOG.error("Target unit cannot be null");
			throw new IllegalArgumentException(
					"Target unit cannot be null"
			);
		}

		if (unit == null) {
			LOG.error("This measurement does not have units defined");
			throw new IllegalAccessException(
					"This measurement does not have units defined"
			);
		}

		if (unit.getResource().equals(newUnit.getResource())) return this; // nothing to be done

		if (!unit.getType().equals(newUnit.getType())) {
			LOG.error("The new unit does not have the same parent type " +
					"(source: " + unit.getType() + "; target: " + newUnit.getType() + ")");
			throw new IllegalAccessException(
					"The new unit does not have the same parent type " +
							"(source: " + unit.getType() + "; target: " + newUnit.getType() + ")"
			);
		}

		Quantity newMeasurement = new Quantity();
		newMeasurement.setUnit(newUnit);
		newMeasurement.setValue(
			((value
			// convert to the base unit
			* unit.getMultiplier().getMultiplier() + unit.getMultiplier().getOffset())
			// convert the base unit to the new unit
			- newUnit.getMultiplier().getOffset()) / newUnit.getMultiplier().getMultiplier() 
		);

		return newMeasurement;
	}

	public String toString() {
		return "" + getValue() + " " + getUnit().toString();
	}

}
