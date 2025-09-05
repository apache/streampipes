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

## Zustands-Puffer

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung

Der Zustands-Puffer-Prozessor speichert Sensorwerte während bestimmter Zustände zwischen. Er unterstützt:
* Zustandsbasierte Wertepufferung
* Zeitstempelverfolgung
* Sensorwert-Zwischenspeicherung
* Zustandsüberwachung

Dieser Prozessor ist essentiell für:
* Zwischenspeichern von Sensorwerten
* Verfolgen von Zustandsänderungen
* Überwachen von Bedingungen
* Speichern von Messungen

***

## Erforderliche Eingabe

Der Prozessor benötigt einen Datenstrom, der enthält:
* Ein Zeitstempelfeld
* Ein Zustandsfeld
* Mindestens ein Sensorwertfeld zum Zwischenspeichern

***

## Konfiguration

### Zeitstempel

Wähle das Feld aus, das den Zeitstempel der Nachricht enthält. Dies wird verwendet, um zu verfolgen, wann Werte gepuffert werden.

### Zustand

Wähle das Feld aus, das die Zustandsinformationen enthält. Dies bestimmt, wann Werte zwischengespeichert werden.

### Sensorwert zum Zwischenspeichern

Wähle das Sensorwertfeld aus, das während des aktiven Zustands zwischengespeichert werden soll.

## Ausgabe

Der Prozessor erstellt eine neue Nachricht, die enthält:
* Ein Zeitstempelfeld
* Eine Liste gepufferter Werte
* Eine Liste von Zuständen

### Beispiel

#### Eingabe-Nachricht
```json
{
  "deviceId": "sensor01",
  "timestamp": 1586380104915,
  "state": ["active"],
  "temperature": 23.5
}
```

#### Konfiguration
* Zeitstempel: timestamp
* Zustand: state
* Sensorwert zum Zwischenspeichern: temperature

#### Ausgabe-Nachricht (wenn sich der Zustand von "active" zu "inactive" ändert)
```json
{
  "timestamp": 1586380105915,
  "values": [23.5, 24.1, 24.3],
  "state": ["active"]
}
```

## Anwendungsfälle

1. **Zustandsüberwachung**
   * Sensorwerte zwischenspeichern
   * Zustandsänderungen verfolgen
   * Bedingungen überwachen
   * Messungen speichern

2. **Datenanalyse**
   * Zustandsmuster analysieren
   * Wertänderungen verfolgen
   * Bedingungen überwachen
   * Messungen speichern

## Hinweise

* Werte werden während aktiver Zustände zwischengespeichert
* Zeitstempel werden beibehalten
* Zustandsänderungen lösen Aktualisierungen aus
* Verarbeitung ist zustandsbehaftet
* Mehrere Werte können gepuffert werden 