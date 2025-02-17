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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public enum UnitProvider {

  INSTANCE;

  private final List<Unit> availableUnitTypes = new ArrayList<>();
  private final List<Unit> availableUnits = new ArrayList<>();

  private final UnitFactory factory;

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

  public List<Unit> getUnitsByType(URI type) {
    return availableUnits
        .stream()
        .filter(u -> u.getType().equals(type))
        .collect(Collectors.toList());
  }
}
