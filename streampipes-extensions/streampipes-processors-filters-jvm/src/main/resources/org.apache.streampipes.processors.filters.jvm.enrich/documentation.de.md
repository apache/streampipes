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

## Durch Anreicherung zusammenführen

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Durch-Anreicherung-zusammenführen-Prozessor kombiniert zwei Ereignisströme, indem ein Strom mit Eigenschaften des anderen angereichert wird. Er unterstützt:
* Echtzeit-Anreicherung von Ereignisströmen
* Verfolgung des letzten Ereigniszustands
* Benutzerdefinierte Ausgabefeldauswahl
* Dynamische Ereigniszusammensetzung
* Zustandsbehaftete Ereignisverarbeitung

Dieser Prozessor ist wichtig für:
* Anreicherung von Ereignissen mit zusätzlichem Kontext
* Kombinieren verwandter Datenströme
* Erstellen einheitlicher Ereignisansichten
* Aufbau zusammengesetzter Ereignisstrukturen

***

## Erforderliche Eingabe
Der Prozessor benötigt zwei Eingabeströme:
* Erster Strom: Jeder Ereignisstrom mit mindestens einer Eigenschaft
* Zweiter Strom: Jeder Ereignisstrom mit mindestens einer Eigenschaft

***

## Konfiguration

### Stromauswahl
Während der Pipeline-Modellierung können Sie:
* Auswählen, welcher Strom angereichert werden soll (Strom 1 oder Strom 2)
* Wählen, welche Felder aus jedem Strom in die Ausgabe aufgenommen werden sollen
* Der Prozessor behält die Ereignisfrequenz des ausgewählten Stroms bei

## Ausgabe
Der Prozessor erstellt ein neues Ereignis, das die ausgewählten Felder aus beiden Eingabeströmen enthält. Die Ausgabe wird generiert, wenn:
* Ein neues Ereignis aus dem ausgewählten Strom eintrifft
* Das letzte Ereignis aus dem anderen Strom verfügbar ist

### Beispiel

#### Eingabeereignisstrom 1
```json
{
  "deviceId": "sensor01",
  "temperature": 25.5,
  "timestamp": 1586380104915
}
```

#### Eingabeereignisstrom 2
```json
{
  "location": "room1",
  "humidity": 45.2,
  "timestamp": 1586380104915
}
```

#### Konfiguration
* Ausgewählter Strom: Strom 1
* Ausgabefelder: deviceId, temperature, location, humidity

#### Ausgabeereignis
```json
{
  "deviceId": "sensor01",
  "temperature": 25.5,
  "location": "room1",
  "humidity": 45.2
}
```

## Anwendungsfälle

1. **Ereignisanreicherung**
   * Hinzufügen von Kontext zu Sensorablesungen
   * Kombinieren verwandter Metriken
   * Erstellen einheitlicher Ansichten
   * Verbinden von Ereignisströmen

2. **Datenintegration**
   * Zusammenführen von Sensordaten
   * Kombinieren verwandter Ereignisse
   * Erstellen zusammengesetzter Ereignisse
   * Aufbau reicher Ereignisstrukturen

## Hinweise

* Ereignisse werden in Echtzeit angereichert
* Der letzte Ereigniszustand wird aufrechterhalten
* Ausgabefelder sind konfigurierbar
* Die ursprüngliche Ereignisstruktur wird beibehalten
* Ereignisse werden mit der Frequenz des ausgewählten Stroms weitergeleitet
* Der Zustand wird beim Stoppen der Pipeline gelöscht 