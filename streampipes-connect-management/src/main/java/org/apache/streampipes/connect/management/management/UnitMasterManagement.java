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

package org.apache.streampipes.connect.management.management;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.model.connect.unit.UnitDescription;
import org.apache.streampipes.units.UnknownMeasurementUnitException;
import org.apache.streampipes.units.catalog.UnitCatalog;
import org.apache.streampipes.units.model.MeasurementUnit;

import com.google.gson.Gson;

import java.util.List;

public class UnitMasterManagement {

  private final Gson gson;
  private final UnitCatalog catalog;

  public UnitMasterManagement() {
    this(UnitCatalog.builtIn());
  }

  public UnitMasterManagement(UnitCatalog catalog) {
    this.gson = new Gson();
    this.catalog = catalog;
  }

  public String getFittingUnits(UnitDescription unitDescription) throws AdapterException {
    if (unitDescription.getResource() == null) {
      throw new AdapterException("The resource cannot be null");
    }

    MeasurementUnit unit;
    try {
      unit = catalog.get(unitDescription.getResource());
    } catch (UnknownMeasurementUnitException e) {
      throw new AdapterException("Unknown measurement unit: " + unitDescription.getResource());
    }

    var fittingUnits = toDescriptions(catalog.getUnitsOfKind(unit.type()));
    return gson.toJson(fittingUnits);
  }

  public List<UnitDescription> getAllUnitDescriptions() {
    return toDescriptions(catalog.getUnits());
  }

  private List<UnitDescription> toDescriptions(List<MeasurementUnit> units) {
    return units
        .stream()
        .map(unit -> new UnitDescription(unit.resource().toString(), unit.label()))
        .toList();
  }
}
