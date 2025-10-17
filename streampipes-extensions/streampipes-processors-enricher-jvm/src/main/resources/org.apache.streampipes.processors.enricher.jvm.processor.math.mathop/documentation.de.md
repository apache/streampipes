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

## Mathematische Operation

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Mathematische Operation-Prozessor führt arithmetische Berechnungen zwischen zwei numerischen Feldern in einem Ereignis durch. Er:
* Unterstützt grundlegende arithmetische Operationen (+, -, *, /, %)
* Funktioniert mit jedem numerischen Feldtyp
* Behält die ursprünglichen Ereignisdaten bei
* Fügt Berechnungsergebnisse als neue Felder hinzu

***

## Erforderliche Eingabe
Der Prozessor benötigt einen Eingabe-Ereignisstrom, der mindestens zwei numerische Felder für die Durchführung von Berechnungen enthält.

***

## Konfiguration

### Linker Operand
Wählen Sie das Feld aus dem Eingabe-Ereignis aus, das als linker Operand in der Berechnung verwendet werden soll.

### Rechter Operand
Wählen Sie das Feld aus dem Eingabe-Ereignis aus, das als rechter Operand in der Berechnung verwendet werden soll.

### Operation
Wählen Sie eine der folgenden arithmetischen Operationen:
* Addition (+)
* Subtraktion (-)
* Multiplikation (*)
* Division (/)
* Modulo (%)

## Ausgabe
Der Prozessor leitet das Eingabe-Ereignis mit einem zusätzlichen Feld namens `calculationResult` weiter, das das Ergebnis der arithmetischen Operation enthält.

### Beispiel

#### Eingabe-Ereignis
```json
{
  "temperature": 25.5,
  "humidity": 60,
  "timestamp": 1586380105115
}
```

#### Konfiguration
* Linker Operand: `temperature`
* Rechter Operand: `humidity`
* Operation: `*`

#### Ausgabe-Ereignis
```json
{
  "temperature": 25.5,
  "humidity": 60,
  "timestamp": 1586380105115,
  "calculationResult": 1530.0
}
```

## Anwendungsfälle

1. **Datentransformation**
   * Berechnung abgeleiteter Metriken
   * Einheitenumrechnung
   * Skalierung von Messwerten
   * Normalisierung von Werten

2. **Geschäftslogik**
   * Kostenberechnung
   * Berechnung von Leistungsmetriken
   * Auswertung von Geschäftsregeln
   * Generierung abgeleiteter Werte

3. **Sensordatenverarbeitung**
   * Kombinieren von Sensorwerten
   * Berechnung von Durchschnitten
   * Normalisierung von Messungen
   * Skalierung von Sensorwerten

## Hinweise

* Beide Operanden müssen numerische Werte sein
* Division durch null führt zu einem Fehler
* Ergebnisse werden als doppelt genaue Fließkommazahlen gespeichert
* Die ursprüngliche Ereignisstruktur bleibt erhalten
* Die Berechnung wird für jedes eingehende Ereignis durchgeführt
* Das Ergebnisfeld heißt immer `calculationResult` 