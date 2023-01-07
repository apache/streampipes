/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.units;

import com.github.jqudt.Unit;
import com.github.jqudt.onto.UnitFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnitCollector {

  private static final Logger LOG = LoggerFactory.getLogger(UnitCollector.class);

  private static final String namespace = "http://qudt.org/schema/qudt#";

  private static final String[] unitTypeClasses = new String[]{
      "DerivedUnit",
      "unit",
      "DimensionlessUnit",
      "ScienceAndEngineeringUnit",
      "ResourceUnit",
      "BaseUnit",
      "SIBaseUnit",
      "DerivedUnit",
      "PhysicalUnit",
      "ComputingUnit",
      "CommunicationsUnit",
      "BiomedicalUnit",
      "RadiologyUnit",
      "NonSIUnit",
      "ChemistryUnit",
      "LogarithmicUnit",
      "SIUnit",
      "AtomicPhysicsUnit",
      "MechanicsUnit",
      "SpaceAndTimeUnit",
      "ElectricityAndMagnetismUnit",
      "ThermodynamicsUnit",
      "PerMeter",
      "SquareMeterKelvin",
      "PerCubicMeter",
      "CurvatureUnit",
      "LengthUnit",
      "VolumeUnit",
      "AngleUnit",
      "AreaAngleUnit",
      "TimeUnit",
      "AccelerationUnit",
      "TimeSquaredUnit",
      "FrequencyUnit",
      "VelocityUnit",
      "TimeAreaUnit",
      "AreaUnit",
      "VolumePerTimeUnit",
      "AreaTimeTemperatureUnit",
      "SpecificHeatVolumeUnit",
      "ThermalEnergyUnit",
      "TemperaturePerTimeUnit",
      "MolarHeatCapacityUnit",
      "LengthTemperatureTimeUnit",
      "ThermalEnergyLengthUnit",
      "ThermalResistivityUnit",
      "TemperatureUnit",
      "ThermalDiffusivityUnit",
      "ThermalConductivityUnit",
      "MassTemperatureUnit",
      "ThermalExpansionUnit",
      "CoefficientOfHeatTransferUnit",
      "SpecificHeatPressureUnit",
      "HeatFlowRateUnit",
      "ThermalResistanceUnit",
      "HeatCapacityAndEntropyUnit",
      "ThermalInsulanceUnit",
      "LengthTemperatureUnit",
      "AreaTemperatureUnit",
      "SpecificHeatCapacityUnit",
      "LinearVelocityUnit",
      "AngularVelocityUnit"
  };

  private Set<Unit> availableUnits = new HashSet<>();
  private Set<Unit> availableUnitTypes = new HashSet<>();

  public UnitCollector() {
    collect();
  }

  private void collect() {
    for (String newUri : unitTypeClasses) {
      try {
        Unit unitType = UnitFactory.getInstance().getUnit(namespace + newUri);
        if (unitType != null) {
          availableUnitTypes.add(unitType);
        }
        List<String> uris = UnitFactory.getInstance().getURIs(namespace + newUri);
        for (String uri : uris) {
          Unit unit = UnitFactory.getInstance().getUnit(uri);
          if (unit != null) {
            availableUnits.add(unit);
          }
        }
      } catch (Exception e) {
        LOG.warn("No entry found");
      }
    }
  }

  public Set<Unit> getAvailableUnits() {
    return availableUnits;
  }

  public Set<Unit> getAvailableUnitTypes() {
    return availableUnitTypes;
  }


}
