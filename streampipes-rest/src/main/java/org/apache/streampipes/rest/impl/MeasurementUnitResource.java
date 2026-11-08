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

package org.apache.streampipes.rest.impl;

import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;
import org.apache.streampipes.units.catalog.UnitCatalog;
import org.apache.streampipes.units.model.MeasurementUnit;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/measurement-units")
public class MeasurementUnitResource extends AbstractRestResource {

  private final UnitCatalog catalog = UnitCatalog.builtIn();

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<MeasurementUnit>> getAllMeasurementUnits() {
    return ok(catalog.getUnits());
  }

  /**
   * Returns the measurement unit information for a given unit identifier.
   * @param unitIdentifier The identifier of the measurement unit to retrieve,
   *                       consisting of the unit's ID without the QUDT URI prefix.
   * @return the unit, or 404 if the catalog does not contain it.
   */
  @GetMapping(path = "/{unitIdentifier}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getMeasurementUnitInfo(
      @PathVariable("unitIdentifier") String unitIdentifier) {
    var unit = catalog.find(unitIdentifier);
    if (unit.isPresent()) {
      return ok(unit.get());
    }
    return notFound();
  }
}
