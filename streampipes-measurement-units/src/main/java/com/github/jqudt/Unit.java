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
