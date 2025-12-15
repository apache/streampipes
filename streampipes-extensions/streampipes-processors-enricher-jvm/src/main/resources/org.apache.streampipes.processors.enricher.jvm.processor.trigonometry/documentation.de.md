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

## Trigonometrische Funktionen

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Trigonometrische Funktionen-Prozessor führt trigonometrische Berechnungen an numerischen Werten durch. Er:
* Unterstützt grundlegende trigonometrische Funktionen (sin, cos, tan)
* Funktioniert mit jedem numerischen Feldtyp
* Behält die ursprünglichen Ereignisdaten bei
* Fügt Berechnungsergebnisse als neue Felder hinzu

***

## Erforderliche Eingabe
Der Prozessor benötigt einen Eingabe-Ereignisstrom, der mindestens ein numerisches Feld für die Durchführung trigonometrischer Berechnungen enthält.

***

## Konfiguration

### Alpha
Wählen Sie das Feld aus dem Eingabe-Ereignis aus, das als Winkel (in Radiant) für die trigonometrische Berechnung verwendet werden soll.

### Operation
Wählen Sie eine der folgenden trigonometrischen Funktionen:
* **sin**: Berechnet den Sinus des Winkels
* **cos**: Berechnet den Kosinus des Winkels
* **tan**: Berechnet den Tangens des Winkels

## Ausgabe
Der Prozessor leitet das Eingabe-Ereignis mit einem zusätzlichen Feld namens `trigonometryResult` weiter, das das Ergebnis der trigonometrischen Berechnung enthält.

### Beispiel

#### Eingabe-Ereignis
```json
{
  "angle": 1.57,
  "timestamp": 1586380105115
}
```

#### Konfiguration
* Alpha: `angle`
* Operation: `sin`

#### Ausgabe-Ereignis
```json
{
  "angle": 1.57,
  "timestamp": 1586380105115,
  "trigonometryResult": 0.9999996829318346
}
```

## Anwendungsfälle

1. **Signalverarbeitung**
   * Wellenformanalyse
   * Signalfilterung
   * Phasenberechnungen
   * Frequenzanalyse

2. **Geometrische Berechnungen**
   * Winkelumrechnungen
   * Entfernungsberechnungen
   * Positionsverfolgung
   * Navigationssysteme

3. **Wissenschaftliche Berechnungen**
   * Physiksimulationen
   * Ingenieurberechnungen
   * Mathematische Modellierung
   * Datenanalyse

## Hinweise

* Eingabewinkel müssen in Radiant angegeben werden
* Ergebnisse werden als doppelt genaue Fließkommazahlen gespeichert
* Die ursprüngliche Ereignisstruktur bleibt erhalten
* Die Berechnung wird für jedes eingehende Ereignis durchgeführt
* Das Ergebnisfeld heißt immer `trigonometryResult` 