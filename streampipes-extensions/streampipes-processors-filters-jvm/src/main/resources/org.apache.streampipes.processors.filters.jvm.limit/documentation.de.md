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

## Ratenbegrenzung

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Ratenbegrenzungs-Prozessor steuert die Häufigkeit von Ereignissen in einem Datenstrom durch Anwendung verschiedener Fensterstrategien. Er unterstützt:
* Zeitbasierte Ratenbegrenzung
* Ereignisanzahlbasierte Ratenbegrenzung
* Cron-basierte Ratenbegrenzung
* Gruppenbasierte Ratenbegrenzung
* Mehrere Ereignisauswahlstrategien

Dieser Prozessor ist wichtig für:
* Steuerung von Datenflussraten
* Reduzierung der Systemlast
* Implementierung von Stichprobenstrategien
* Verwaltung der Ressourcennutzung

***

## Erforderliche Eingabe
Der Prozessor arbeitet mit jedem Eingabeereignisstrom.

***

## Konfiguration

### Gruppierungseinstellungen
* **Gruppierung aktivieren**: Wenn aktiviert, wird die Ratenbegrenzung separat für jede Gruppe angewendet
* **Gruppierungsfeld**: Als Gruppierungsschlüssel zu verwendendes Feld (nur bei aktivierter Gruppierung verwendet)

### Fensterkonfiguration
Wählen Sie einen der folgenden Fenstertypen:

1. **Zeitfenster**
   * Fenstergröße in Millisekunden
   * Ereignisse werden nach Zeitintervallen gruppiert
   * Beispiel: 1000ms-Fenster gibt Ereignisse jede Sekunde aus

2. **Längenfenster**
   * Fenstergröße in Ereignisanzahl
   * Ereignisse werden nach Anzahl gruppiert
   * Beispiel: Fenstergröße von 10 gibt alle 10 Ereignisse aus

3. **Cron-Fenster**
   * Cron-Ausdruck für die Planung
   * Ereignisse werden nach Zeitplan gruppiert
   * Beispiel: `0 * * ? * *` gibt jede Minute aus

### Ereignisauswahl
Wählen Sie, wie Ereignisse aus jedem Fenster ausgewählt werden:
* **First**: Gibt das erste Ereignis im Fenster aus
* **Last**: Gibt das letzte Ereignis im Fenster aus
* **All**: Gibt alle Ereignisse im Fenster aus

## Ausgabe
Der Prozessor gibt Ereignisse basierend auf der konfigurierten Fenster- und Auswahlstrategie aus. Die Ausgabe behält die ursprüngliche Ereignisstruktur bei.

### Beispiel

#### Eingabeereignisse
```json
{
  "deviceId": "sensor01",
  "temperature": 25.5,
  "timestamp": 1586380104915
}
{
  "deviceId": "sensor01",
  "temperature": 26.0,
  "timestamp": 1586380105015
}
{
  "deviceId": "sensor01",
  "temperature": 25.8,
  "timestamp": 1586380105115
}
```

#### Konfiguration
* Fenstertyp: Zeitfenster
* Fenstergröße: 1000ms
* Ereignisauswahl: Last
* Gruppierung: Deaktiviert

#### Ausgabeereignisse
```json
{
  "deviceId": "sensor01",
  "temperature": 25.8,
  "timestamp": 1586380105115
}
```

## Anwendungsfälle

1. **Datenstichproben**
   * Reduzierung hochfrequenter Sensordaten
   * Stichproben großer Ereignisströme
   * Steuerung von Datenflussraten
   * Implementierung periodischer Berichte

2. **Ressourcenverwaltung**
   * Verhinderung von Systemüberlastung
   * Verwaltung der nachgelagerten Verarbeitung
   * Steuerung von Speicherraten
   * Optimierung der Netzwerknutzung

## Hinweise

* Fenster werden unabhängig für jede Gruppe verarbeitet
* Ereignisauswahl wird nach Fensterabschluss angewendet
* Zustand wird pro Gruppe aufrechterhalten
* Fenster werden nach der Verarbeitung gelöscht
* Cron-Ausdrücke folgen dem Quartz-Scheduler-Format
* Zeitfenster verwenden die Systemzeit
* Längenfenster zählen alle Ereignisse 