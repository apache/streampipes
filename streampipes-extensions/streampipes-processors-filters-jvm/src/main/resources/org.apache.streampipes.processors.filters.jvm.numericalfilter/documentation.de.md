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

## Numerischer Filter

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Numerische-Filter-Prozessor filtert Ereignisse basierend auf numerischen Vergleichen mit einem festgelegten Schwellenwert. Er unterstützt verschiedene Vergleichsoperationen und ist ideal für:
* Schwellenwertbasierte Ereignisfilterung
* Bereichsbasierte Datenauswahl
* Ausreißererkennung
* Wertbasierte Ereignisweiterleitung

***

## Erforderliche Eingabe
Ein Datenstrom, der mindestens ein numerisches Feld zur Filterung enthält.

***

## Konfiguration

### Feld
* Wählen Sie das numerische Feld aus, auf das die Filteroperation angewendet werden soll
* Das Feld muss numerische Werte enthalten

### Operation
Wählen Sie aus den folgenden Vergleichsoperatoren:
* **<** (Kleiner als)
* **<=** (Kleiner als oder gleich)
* **>** (Größer als)
* **>=** (Größer als oder gleich)
* **==** (Gleich)
* **!=** (Ungleich)

### Schwellenwert
* Geben Sie den numerischen Schwellenwert für den Vergleich an
* Der Wert muss eine gültige Zahl sein

## Ausgabe
Der Prozessor leitet das Eingabeereignis nur weiter, wenn der numerische Vergleich wahr ergibt.

### Beispiel

#### Eingabeereignisse
```json
{
  "temperature": 25.5,
  "timestamp": 1586380104915
}
{
  "temperature": 26.0,
  "timestamp": 1586380105015
}
{
  "temperature": 25.8,
  "timestamp": 1586380105115
}
```

#### Beispiel 1: Größer-als-Filter
Konfiguration:
* Feld: temperature
* Operation: >
* Schwellenwert: 25.8

Ausgabeereignisse:
```json
{
  "temperature": 26.0,
  "timestamp": 1586380105015
}
```

#### Beispiel 2: Bereichsfilter
Konfiguration:
* Feld: temperature
* Operation: >=
* Schwellenwert: 25.5

Ausgabeereignisse:
```json
{
  "temperature": 25.5,
  "timestamp": 1586380104915
}
{
  "temperature": 26.0,
  "timestamp": 1586380105015
}
{
  "temperature": 25.8,
  "timestamp": 1586380105115
}
```

#### Beispiel 3: Exakter-Match-Filter
Konfiguration:
* Feld: temperature
* Operation: ==
* Schwellenwert: 25.8

Ausgabeereignisse:
```json
{
  "temperature": 25.8,
  "timestamp": 1586380105115
}
```

## Anwendungsfälle

1. **Schwellenwertüberwachung**
   * Alarmierung bei Überschreitung von Grenzwerten
   * Filterung normaler Messwerte
   * Überwachung kritischer Schwellenwerte
   * Verfolgung von Wertebereichen

2. **Datenqualität**
   * Entfernung von Ausreißern
   * Filterung ungültiger Messungen
   * Sicherstellung von Wertebereichen
   * Validierung von Sensordaten

## Hinweise

* Der Prozessor führt exakte numerische Vergleiche durch
* Bei Gleichheitsprüfungen wird ein kleiner Epsilon-Wert (0.000001) verwendet, um die Fließkommapräzision zu berücksichtigen
* Ereignisse, die die Filterbedingung nicht erfüllen, werden verworfen
* Die ursprüngliche Ereignisstruktur wird in der Ausgabe beibehalten
* Alle numerischen Typen (Integer, Float, Double) werden unterstützt 