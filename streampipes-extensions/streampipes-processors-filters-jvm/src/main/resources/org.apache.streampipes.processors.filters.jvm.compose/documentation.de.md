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

## Zusammenfügen

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Zusammenführen-Prozessor führt zwei Ereignisströme durch Kombination ihrer Eigenschaften zusammen. Er unterstützt:
* Echtzeit-Zusammenführung von Ereignisströmen
* Verfolgung des letzten Ereigniszustands
* Benutzerdefinierte Ausgabefeldauswahl
* Dynamische Ereigniszusammensetzung
* Zustandsbehaftete Ereignisverarbeitung

Dieser Prozessor ist wichtig für:
* Kombinieren von Daten aus mehreren Quellen
* Erstellen einheitlicher Ereignisansichten
* Zusammenführen verwandter Ereignisströme
* Aufbau zusammengesetzter Ereignisstrukturen

***

## Erforderliche Eingabe
Der Prozessor benötigt zwei Eingabeströme:
* Erster Strom: Jeder Ereignisstrom mit mindestens einer Eigenschaft
* Zweiter Strom: Jeder Ereignisstrom mit mindestens einer Eigenschaft

***

## Konfiguration

### Ausgabewahl
Während der Pipeline-Modellierung können Sie auswählen, welche Felder aus jedem Strom in das Ausgabeereignis aufgenommen werden sollen. Der Prozessor wird:
* Das letzte Ereignis aus jedem Strom im Speicher behalten
* Ausgewählte Felder bei Ankunft neuer Ereignisse zusammenführen
* Das kombinierte Ereignis mit den ausgewählten Feldern weiterleiten

## Ausgabe
Der Prozessor erstellt ein neues Ereignis, das die ausgewählten Felder aus beiden Eingabeströmen enthält. Die Ausgabe wird generiert, wenn:
* Ein neues Ereignis aus einem der Ströme eintrifft
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

1. **Datenintegration**
   * Kombinieren von Sensorablesungen
   * Zusammenführen verwandter Metriken
   * Erstellen einheitlicher Ansichten
   * Verbinden von Ereignisströmen

2. **Ereignisanreicherung**
   * Hinzufügen von Kontext zu Ereignissen
   * Kombinieren verwandter Daten
   * Erstellen zusammengesetzter Ereignisse
   * Aufbau reicher Ereignisstrukturen

## Hinweise

* Ereignisse werden in Echtzeit zusammengeführt
* Der letzte Ereigniszustand wird aufrechterhalten
* Ausgabefelder sind konfigurierbar
* Die ursprüngliche Ereignisstruktur wird beibehalten
* Ereignisse werden bei jeder neuen Eingabe weitergeleitet
* Der Zustand wird beim Stoppen der Pipeline gelöscht 