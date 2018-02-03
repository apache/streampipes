/* Copyright (C) 2012  Egon Willighagen <egonw@users.sf.net>
 *
 * License: new BSD
 */
package com.github.jqudt;

import java.io.Serializable;

public class Multiplier implements Serializable{

	private double offset;
	private double multiplier;

	public Multiplier(double offset, double multiplier) {
		this.offset = offset;
		this.multiplier = multiplier;
	}

	public Multiplier() {}

	public double getOffset() {
		return offset;
	}
	public void setOffset(double offset) {
		this.offset = offset;
	}
	public double getMultiplier() {
		return multiplier;
	}
	public void setMultiplier(double multiplier) {
		this.multiplier = multiplier;
	}

}
