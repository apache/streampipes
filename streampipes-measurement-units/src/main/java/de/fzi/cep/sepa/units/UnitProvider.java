package de.fzi.cep.sepa.units;

import java.util.ArrayList;
import java.util.List;

import com.github.jqudt.Unit;
import com.github.jqudt.onto.UnitFactory;

public enum UnitProvider {

	INSTANCE;
	
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
}
