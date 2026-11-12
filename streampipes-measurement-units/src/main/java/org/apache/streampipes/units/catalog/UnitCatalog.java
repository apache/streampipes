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

package org.apache.streampipes.units.catalog;

import org.apache.streampipes.units.UnknownMeasurementUnitException;
import org.apache.streampipes.units.model.MeasurementUnit;
import org.apache.streampipes.units.model.Multiplier;
import org.apache.streampipes.units.model.QuantityKind;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * An immutable catalog of measurement units, grouped by quantity kind.
 *
 * <p>Every unit has a canonical identifier (its {@link MeasurementUnit#resource()}) and any
 * number of aliases. The local name of each identifier (the part after the last {@code #} or
 * {@code /}) is an alias as well. Lookups accept any of them and always return the unit with
 * its canonical identifier.</p>
 */
public final class UnitCatalog {

  private final List<QuantityKind> kinds;
  private final List<MeasurementUnit> units;
  private final Map<URI, List<MeasurementUnit>> unitsByKind;
  private final Map<String, MeasurementUnit> unitsByIdentifier;

  private UnitCatalog(List<QuantityKind> kinds,
                      Map<URI, List<MeasurementUnit>> unitsByKind,
                      Map<String, MeasurementUnit> unitsByIdentifier) {
    this.kinds = List.copyOf(kinds);
    this.unitsByKind = Collections.unmodifiableMap(unitsByKind);
    this.units = unitsByKind.values().stream().flatMap(List::stream).toList();
    this.unitsByIdentifier = Collections.unmodifiableMap(unitsByIdentifier);
  }

  /**
   * The catalog of units that ships with StreamPipes.
   */
  public static UnitCatalog builtIn() {
    return BuiltInUnits.CATALOG;
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Returns the unit for a canonical identifier or alias, or empty if the catalog does not
   * contain it.
   */
  public Optional<MeasurementUnit> find(String identifier) {
    if (identifier == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(unitsByIdentifier.get(identifier.trim()));
  }

  public Optional<MeasurementUnit> find(URI identifier) {
    return identifier == null ? Optional.empty() : find(identifier.toString());
  }

  /**
   * Returns the unit for a canonical identifier or alias.
   *
   * @throws UnknownMeasurementUnitException if the catalog does not contain the unit
   */
  public MeasurementUnit get(String identifier) {
    return find(identifier).orElseThrow(() -> new UnknownMeasurementUnitException(identifier));
  }

  /**
   * Returns the unit for a canonical identifier or alias.
   *
   * @throws UnknownMeasurementUnitException if the catalog does not contain the unit
   */
  public MeasurementUnit get(URI identifier) {
    return get(identifier == null ? null : identifier.toString());
  }

  /**
   * Returns the canonical identifier for an identifier or alias, e.g.
   * {@code http://qudt.org/vocab/unit#DegreeCelsius} for {@code DEG_C}. Use it before storing
   * a unit identifier, so that stored identifiers can be compared.
   *
   * @return the canonical identifier, or empty if the catalog does not contain the unit
   */
  public Optional<URI> canonicalize(String identifier) {
    return find(identifier).map(MeasurementUnit::resource);
  }

  /**
   * Like {@link #canonicalize(String)}, but returns the identifier unchanged if the catalog
   * does not contain it.
   */
  public URI canonicalizeOrKeep(URI identifier) {
    return find(identifier).map(MeasurementUnit::resource).orElse(identifier);
  }

  public List<MeasurementUnit> getUnits() {
    return units;
  }

  public List<QuantityKind> getQuantityKinds() {
    return kinds;
  }

  /**
   * Returns all units of a quantity kind, or an empty list if the kind is unknown.
   */
  public List<MeasurementUnit> getUnitsOfKind(URI kind) {
    return unitsByKind.getOrDefault(kind, List.of());
  }

  /**
   * Returns every identifier (canonical, aliases and local names) that resolves to a unit.
   */
  public Set<String> getIdentifiers() {
    return unitsByIdentifier.keySet();
  }

  static String localName(String identifier) {
    int index = Math.max(identifier.lastIndexOf('#'), identifier.lastIndexOf('/'));
    return identifier.substring(index + 1);
  }

  /**
   * Builds a catalog. Validation happens in {@link #build()}.
   */
  public static final class Builder {

    private final List<KindBuilder> kinds = new ArrayList<>();

    private Builder() {
    }

    public KindBuilder kind(String resource, String label) {
      var kind = new KindBuilder(this, new QuantityKind(URI.create(resource), label));
      kinds.add(kind);
      return kind;
    }

    /**
     * Validates the definitions and creates the catalog.
     *
     * @throws IllegalStateException if a kind does not have exactly one base unit, or if an
     *                               identifier is used twice
     */
    public UnitCatalog build() {
      var quantityKinds = new ArrayList<QuantityKind>();
      var unitsByKind = new LinkedHashMap<URI, List<MeasurementUnit>>();
      var unitsByIdentifier = new LinkedHashMap<String, MeasurementUnit>();

      for (KindBuilder kind : kinds) {
        if (unitsByKind.containsKey(kind.kind.resource())) {
          throw new IllegalStateException("Duplicate quantity kind: " + kind.kind.resource());
        }
        long baseUnits = kind.units.stream().filter(u -> u.base).count();
        if (baseUnits != 1) {
          throw new IllegalStateException(
              "Quantity kind %s must have exactly one base unit, found %d".formatted(
                  kind.kind.resource(), baseUnits));
        }

        var units = new ArrayList<MeasurementUnit>();
        for (UnitBuilder definition : kind.units) {
          var unit = definition.toUnit(kind.kind.resource());
          units.add(unit);
          for (String identifier : definition.identifiers()) {
            var previous = unitsByIdentifier.putIfAbsent(identifier, unit);
            if (previous != null) {
              throw new IllegalStateException(
                  "Identifier %s is used by both %s and %s".formatted(
                      identifier, previous.resource(), unit.resource()));
            }
          }
        }
        quantityKinds.add(kind.kind);
        unitsByKind.put(kind.kind.resource(), List.copyOf(units));
      }
      return new UnitCatalog(quantityKinds, unitsByKind, unitsByIdentifier);
    }
  }

  /**
   * Collects the units of one quantity kind.
   */
  public static final class KindBuilder {

    private final Builder catalog;
    private final QuantityKind kind;
    private final List<UnitBuilder> units = new ArrayList<>();

    private KindBuilder(Builder catalog, QuantityKind kind) {
      this.catalog = catalog;
      this.kind = kind;
    }

    /**
     * Adds the base unit of this kind. All other units of the kind convert to it.
     */
    public UnitBuilder base(String resource, String label, String abbreviation, String symbol) {
      return unit(resource, label, abbreviation, symbol).markAsBase();
    }

    public UnitBuilder unit(String resource, String label, String abbreviation, String symbol) {
      var unit = new UnitBuilder(this, resource, label, abbreviation, symbol);
      units.add(unit);
      return unit;
    }
  }

  /**
   * Defines one unit. Factor and offset default to 1 and 0.
   */
  public static final class UnitBuilder {

    private final KindBuilder kind;
    private final String resource;
    private final String label;
    private final String abbreviation;
    private final String symbol;
    private final List<String> aliases = new ArrayList<>();
    private String factor = "1";
    private String offset = "0";
    private boolean base;

    private UnitBuilder(KindBuilder kind,
                        String resource,
                        String label,
                        String abbreviation,
                        String symbol) {
      this.kind = kind;
      this.resource = resource;
      this.label = label;
      this.abbreviation = abbreviation;
      this.symbol = symbol;
    }

    /**
     * Sets the factor to the base unit, as a {@link FactorExpression}.
     */
    public UnitBuilder factor(String expression) {
      this.factor = expression;
      return this;
    }

    /**
     * Sets the offset to the base unit (applied after the factor), as a
     * {@link FactorExpression}.
     */
    public UnitBuilder offset(String expression) {
      this.offset = expression;
      return this;
    }

    public UnitBuilder alias(String... identifiers) {
      aliases.addAll(List.of(identifiers));
      return this;
    }

    public UnitBuilder base(String resource, String label, String abbreviation, String symbol) {
      return kind.base(resource, label, abbreviation, symbol);
    }

    public UnitBuilder unit(String resource, String label, String abbreviation, String symbol) {
      return kind.unit(resource, label, abbreviation, symbol);
    }

    public KindBuilder kind(String resource, String label) {
      return kind.catalog.kind(resource, label);
    }

    public UnitCatalog build() {
      return kind.catalog.build();
    }

    private UnitBuilder markAsBase() {
      this.base = true;
      return this;
    }

    private Set<String> identifiers() {
      var identifiers = new LinkedHashSet<String>();
      identifiers.add(resource);
      identifiers.addAll(aliases);
      new ArrayList<>(identifiers).forEach(identifier -> identifiers.add(localName(identifier)));
      return identifiers;
    }

    private MeasurementUnit toUnit(URI kindResource) {
      double multiplier = FactorExpression.evaluate(factor);
      double offsetValue = FactorExpression.evaluate(offset);
      if (multiplier == 0) {
        throw new IllegalStateException("Unit %s has a factor of zero".formatted(resource));
      }
      if (base && (multiplier != 1 || offsetValue != 0)) {
        throw new IllegalStateException(
            "Base unit %s must have factor 1 and offset 0".formatted(resource));
      }
      return new MeasurementUnit(
          URI.create(resource),
          label,
          abbreviation,
          symbol,
          kindResource,
          new Multiplier(offsetValue, multiplier));
    }
  }
}
