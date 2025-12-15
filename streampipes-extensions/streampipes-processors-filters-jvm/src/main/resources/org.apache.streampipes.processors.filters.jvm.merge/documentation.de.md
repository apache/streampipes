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

## Nach Zeit zusammenführen

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Nach-Zeit-zusammenführen-Prozessor kombiniert Ereignisse aus zwei Datenströmen basierend auf ihren Zeitstempeln. Er führt Ereignisse zusammen, wenn ihre Zeitstempel innerhalb eines festgelegten Zeitintervalls voneinander liegen. Dieser Prozessor ist wichtig für:
* Synchronisieren von Daten aus mehreren Quellen
* Korrelieren von Ereignissen über verschiedene Ströme hinweg
* Erstellen einheitlicher Ansichten zeitlich ausgerichteter Daten
* Implementieren zeitbasierter Ereignisabgleichung

<p align="center"> 
    <img width="300px;" src="merge_description.png" class="pe-image-documentation"/>
</p>
***

## Erforderliche Eingabe
Jeder Eingabestrom muss ein Zeitstempelfeld enthalten, das für den Abgleich von Ereignissen verwendet werden kann.

***

## Konfiguration

### Zeitstempelauswahl
* **Zeitstempel Strom 1**: Wählen Sie das Zeitstempelfeld aus dem ersten Eingabestrom
* **Zeitstempel Strom 2**: Wählen Sie das Zeitstempelfeld aus dem zweiten Eingabestrom

### Zeitintervall
* Gibt den maximalen Zeitunterschied (in Millisekunden) zwischen Ereignissen an, damit sie als Übereinstimmung betrachtet werden
* Ereignisse werden zusammengeführt, wenn: |zeitstempel_strom_1 - zeitstempel_strom_2| < intervall
* Beispiel: Bei Intervall = 1000ms werden Ereignisse innerhalb von 1 Sekunde voneinander zusammengeführt

## Ausgabe
Der Prozessor erstellt ein neues Ereignis, das alle Felder aus beiden Eingabeereignissen enthält, wenn ihre Zeitstempel innerhalb des festgelegten Intervalls übereinstimmen.

### Beispiel

#### Eingabeereignisse
Strom 1:
```json
{
  "deviceId": "sensor01",
  "temperature": 25.5,
  "timestamp": 1586380104915
}
```

Strom 2:
```json
{
  "location": "room1",
  "humidity": 45,
  "timestamp": 1586380105015
}
```

#### Konfiguration
* Zeitstempel Strom 1: timestamp
* Zeitstempel Strom 2: timestamp
* Zeitintervall: 1000ms

#### Ausgabeereignis
```json
{
  "deviceId": "sensor01",
  "temperature": 25.5,
  "location": "room1",
  "humidity": 45,
  "timestamp": 1586380105015
}
```

## Anwendungsfälle

1. **Sensordatenkorrelation**
   * Kombinieren von Temperatur- und Feuchtigkeitsablesungen
   * Zusammenführen von Standort- und Umgebungsdaten
   * Synchronisieren mehrerer Sensorströme
   * Erstellen einheitlicher Sensoransichten

2. **Ereignissynchronisation**
   * Ausrichten von Ereignissen aus verschiedenen Quellen
   * Abgleichen verwandter Ereignisse über Ströme hinweg
   * Erstellen zeitlich ausgerichteter Datenansichten
   * Implementieren temporaler Joins

## Hinweise

* Ereignisse werden basierend auf absolutem Zeitunterschied abgeglichen
* Pufferverwaltung verhindert Speicherüberlauf
* Ereignisse außerhalb des Zeitintervalls werden nicht zusammengeführt
* Die ursprüngliche Ereignisstruktur wird in der Ausgabe beibehalten
* Zeitstempel müssen in Millisekunden sein
* Beide Ströme müssen gültige Zeitstempelfelder haben 