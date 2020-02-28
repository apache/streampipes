/* Copyright (C) 2012  Egon Willighagen <egonw@users.sf.net>
 *
 * License: new BSD
 */
package com.github.jqudt;

import java.io.Serializable;
import java.net.URI;

public class Unit implements Serializable{

	private URI resource;
	private String label;
	private String abbreviation;
	private String symbol;
	private URI type;

	private Multiplier multiplier;

	public URI getResource() {
		return resource;
	}

	public void setResource(URI resource) {
		this.resource = resource;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Multiplier getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(Multiplier multiplier) {
		this.multiplier = multiplier;
	}

	public URI getType() {
		return type;
	}

	public void setType(URI type) {
		this.type = type;
	}

	public String toString() {
		return (this.getAbbreviation() == null ? "" : this.getAbbreviation());
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Unit)) return false;

		return (obj.hashCode() == this.hashCode());
	}

	public int hashCode() {
		return resource == null ? "".hashCode() : resource.toString().hashCode();
	}

}
