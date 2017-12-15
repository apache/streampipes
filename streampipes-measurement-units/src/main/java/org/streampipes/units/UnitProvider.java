package org.streampipes.units;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.github.jqudt.Unit;
import com.github.jqudt.onto.UnitFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum UnitProvider {

	INSTANCE;

	static Logger LOG = LoggerFactory.getLogger(UnitCollector.class);

	private List<Unit> availableUnitTypes = new ArrayList<>();
	private List<Unit> availableUnits = new ArrayList<>();
	
	private UnitFactory factory;
	
	UnitProvider() {
		factory = UnitFactory.getInstance();
		UnitCollector collector = new UnitCollector();
		this.availableUnits.addAll(collector.getAvailableUnits());
		this.availableUnitTypes.addAll(collector.getAvailableUnitTypes());
	}
	
	public List<Unit> getAvailableUnitTypes() {
		return availableUnitTypes;
	}
	
	public List<Unit> getAvailableUnits() {
		return availableUnits;
	}
	
	public Unit getUnit(String resourceUri) {
		return factory.getUnit(resourceUri);
	}

	public Unit getUnitByLabel(String label) {
		try {
			return availableUnits.stream()
					.filter((Unit unit) -> unit.getLabel().equals(label))
					.findFirst()
					.get();
		} catch (NoSuchElementException e) {
			LOG.error("No unit with label \"" + label + "\" found");
			return null;
		}

	}
}
