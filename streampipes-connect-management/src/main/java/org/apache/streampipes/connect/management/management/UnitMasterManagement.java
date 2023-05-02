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
import org.apache.streampipes.units.UnitProvider;

import com.github.jqudt.Unit;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class UnitMasterManagement {

  private static final Logger logger = LoggerFactory.getLogger(UnitMasterManagement.class);
  private Gson gson;

  public UnitMasterManagement() {
    gson = new Gson();
  }

  public String getFittingUnits(UnitDescription unitDescription) throws AdapterException {
    List<UnitDescription> unitDescriptionList = new LinkedList<>();
    Unit unit;

    if (unitDescription.getResource() == null) {
      throw new AdapterException("The resource cannot be null");
    }
    try {
      unit = UnitProvider.INSTANCE.getUnit(unitDescription.getResource());
    } catch (IllegalStateException e) {
      throw new AdapterException("Invalid URI: " + unitDescription.getResource());
    }
    List<Unit> units = UnitProvider.INSTANCE.getUnitsByType(unit.getType());


    for (Iterator iter = units.iterator(); iter.hasNext(); ) {
      Unit unitTmp = (Unit) iter.next();
      try {
        UnitDescription unitDescriptionTmp = new UnitDescription(unitTmp.getResource().toString(), unitTmp.getLabel());
        unitDescriptionList.add(unitDescriptionTmp);
      } catch (NullPointerException e) {
        logger.error("Unit has no resource and/or Label");
      }
    }

    return gson.toJson(unitDescriptionList);
  }

}
