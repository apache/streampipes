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

## Schwellenwert-Erkennung

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Schwellenwert-Erkennung-Prozessor überwacht numerische Werte und erkennt, wenn sie definierte Schwellenwerte überschreiten. Er:
* Vergleicht numerische Werte mit einem Schwellenwert
* Fügt ein boolesches Flag hinzu, das den Schwellenwertstatus anzeigt
* Unterstützt verschiedene Vergleichsoperationen
* Behält alle Eingabedaten bei und fügt Schwellenwertinformationen hinzu

***

## Erforderliche Eingabe
Der Prozessor benötigt einen Eingabe-Ereignisstrom, der mindestens ein numerisches Feld zur Überwachung enthält.

***

## Konfiguration

### Feld
Wählen Sie das numerische Feld aus, das auf Schwellenwertüberschreitung überwacht werden soll.

### Operation
Wählen Sie aus sechs Vergleichsoperationen:
* **<** (Kleiner als)
* **<=** (Kleiner als oder gleich)
* **>** (Größer als)
* **>=** (Größer als oder gleich)
* **==** (Gleich)
* **!=** (Ungleich)

### Schwellenwert
Geben Sie den numerischen Schwellenwert an, mit dem verglichen werden soll.

## Ausgabe
Der Prozessor leitet das Eingabe-Ereignis mit einem zusätzlichen booleschen Feld `thresholdDetected` weiter, das anzeigt, ob die Schwellenwertbedingung erfüllt wurde.

### Beispiel

#### Eingabe-Ereignis
```json
{
  "temperature": 25.5,
  "humidity": 60,
  "timestamp": 1586380104915
}
```

#### Konfiguration
* Feld: `temperature`
* Operation: `>`
* Schwellenwert: `25.0`

#### Ausgabe-Ereignis
```json
{
  "temperature": 25.5,
  "humidity": 60,
  "timestamp": 1586380104915,
  "thresholdDetected": true
}
```

## Anwendungsfälle

1. **Überwachung & Alarmierung**
   * Überwachung von Sensorwerten
   * Erkennung von Schwellenwertüberschreitungen
   * Auslösung von Alarmen
   * Verfolgung von Wertebereichen

2. **Qualitätskontrolle**
   * Überwachung von Prozessparametern
   * Erkennung von Werten außerhalb des Bereichs
   * Sicherstellung von Qualitätsstandards
   * Verfolgung der Einhaltung

3. **Datenanalyse**
   * Analyse von Werteverteilungen
   * Verfolgung von Schwellenwertereignissen
   * Überwachung von Trends
   * Identifizierung von Mustern

## Hinweise

* Der Prozessor behält alle Eingabefelder bei
* Das Feld `thresholdDetected` wird immer angehängt
* Fließkommavergleiche verwenden ein kleines Epsilon (0.000001) für Gleichheit
* Alle Ereignisse werden weitergeleitet, unabhängig vom Schwellenwertstatus
* Die Schwellenwertprüfung wird auf dem exakten numerischen Wert durchgeführt 