<!--
  ~ Licensed to the Apache Software Foundation (ASF) under one or more
  ~ contributor license agreements.  See the NOTICE file distributed with
  ~ this work for additional information regarding copyright ownership.
  ~ The ASF licenses this file to You under the Apache License, Version 2.0
  ~ (the "License"); you may not use this file except in compliance with
  ~ the License.  You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  ~
  -->

## Statische Mathematik

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung

Der Statische Mathematik-Prozessor führt arithmetische Berechnungen zwischen einem numerischen Feld und einem statischen Wert durch. Er:
* Unterstützt grundlegende arithmetische Operationen (+, -, *, /, %)
* Funktioniert mit jedem numerischen Feldtyp
* Verwendet einen konfigurierbaren statischen Wert als einen Operanden
* Aktualisiert das Eingabefeld mit dem Berechnungsergebnis
* Behält andere Ereignisfelder bei

***

## Erforderliche Eingabe

Der Prozessor benötigt einen Eingabe-Ereignisstrom, der mindestens ein numerisches Feld für die Durchführung von Berechnungen enthält.

***

## Konfiguration

### Linker Operand

Wähle das Feld aus dem Eingabe-Ereignis aus, das als linker Operand in der Berechnung verwendet werden soll.

### Rechter Operand-Wert

Gib den statischen numerischen Wert an, der als rechter Operand in der Berechnung verwendet werden soll.

### Operation

Wähle eine der folgenden arithmetischen Operationen:
* Addition (+)
* Subtraktion (-)
* Multiplikation (*)
* Division (/)
* Modulo (%)

## Ausgabe

Der Prozessor aktualisiert das ausgewählte Eingabefeld mit dem Ergebnis der arithmetischen Operation.

### Beispiel

#### Eingabe-Ereignis
```json
{
  "temperature": 25.5,
  "timestamp": 1586380105115
}
```

#### Konfiguration
* Linker Operand: `temperature`
* Rechter Operand-Wert: `2.0`
* Operation: `*`

#### Ausgabe-Ereignis
```json
{
  "temperature": 51.0,
  "timestamp": 1586380105115
}
```

## Anwendungsfälle

1. **Einheitenumrechnung**
   * Umrechnung zwischen Maßeinheiten
   * Skalierung von Werten
   * Normalisierung von Daten
   * Anwendung von Umrechnungsfaktoren

2. **Datentransformation**
   * Anwendung konstanter Offsets
   * Skalierung von Messungen
   * Anpassung von Werten
   * Normalisierung von Bereichen

3. **Signalverarbeitung**
   * Verstärkung von Signalen
   * Dämpfung von Werten
   * Anwendung von Verstärkungen
   * Signalkonditionierung

## Hinweise

* Der Prozessor aktualisiert das Eingabefeld direkt
* Alle Berechnungen werden mit doppelter Genauigkeit durchgeführt
* Division durch null führt zu einem Fehler
* Die Modulo-Operation funktioniert mit Fließkommazahlen
* Der ursprüngliche Feldname wird beibehalten
* Andere Ereignisfelder bleiben unverändert 