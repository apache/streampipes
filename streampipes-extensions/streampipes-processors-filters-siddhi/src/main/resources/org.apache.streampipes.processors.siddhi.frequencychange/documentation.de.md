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

## Frequenzänderungs-Überwachung

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Frequenzänderungs-Überwachungs-Prozessor erkennt signifikante Änderungen in der Ereigniseintrittsfrequenz. Er:
* Überwacht Ereigniseintrittsmuster
* Erkennt prozentuale Frequenzänderungen
* Unterstützt konfigurierbare Zeitfenster
* Funktioniert mit jedem Eingabeereignisstrom
* Behält die ursprünglichen Ereignisdaten bei

***

## Erforderliche Eingabe
Der Prozessor funktioniert mit jedem Eingabeereignisstrom. Es werden keine spezifischen Eingabeanforderungen benötigt.

***

## Konfiguration

### Zeitfensterlänge
Geben Sie die Dauer des Zeitfensters in Sekunden an. Der Prozessor überwacht Frequenzänderungen innerhalb dieses Fensters.

### Zeiteinheit
Wählen Sie die Zeiteinheit für die Fenstergröße:
* Stunden (Std)
* Minuten (Min)
* Sekunden (Sek)

### Prozentsatz der Zunahme/Abnahme
Geben Sie den Schwellenwert für die Frequenzänderungserkennung an:
* Wert stellt die prozentuale Änderung dar (z.B. 100 bedeutet 100% Zunahme)
* Bereich: 0-500%
* Schrittweite: 1%

## Ausgabe
Der Prozessor gibt das ursprüngliche Ereignis aus, wenn eine signifikante Frequenzänderung innerhalb des angegebenen Zeitfensters erkannt wird.

### Beispiel

#### Eingabeereignis
```json
{
  "temperature": 25.5,
  "timestamp": 1586380105115
}
```

#### Konfiguration
* Zeitfensterlänge: `30`
* Zeiteinheit: `Sek`
* Prozentsatz der Zunahme/Abnahme: `100`

#### Ausgabeereignis
Der Prozessor gibt das Ereignis aus, wenn sich die Frequenz der Ereignisse innerhalb eines 30-Sekunden-Fensters um 100% oder mehr ändert.

## Anwendungsfälle

1. **Anomalieerkennung**
   * Ungewöhnliche Ereignismuster identifizieren
   * Plötzliche Änderungen im Datenfluss erkennen
   * Systemverhaltensänderungen überwachen
   * Ereignisraten-Anomalien verfolgen

2. **Leistungsüberwachung**
   * Systemdurchsatzänderungen überwachen
   * Datenverarbeitungsraten verfolgen
   * Engpässe identifizieren
   * Systemreaktionsfähigkeit messen

3. **Qualitätssicherung**
   * Konsistenten Datenfluss sicherstellen
   * Zuverlässigkeit der Datenerfassung überwachen
   * Systemleistung verfolgen
   * Datenquellengesundheit validieren

## Hinweise

* Der Prozessor erkennt prozentuale Änderungen in der Ereignisfrequenz
* Das Zeitfenster ist in Stunden, Minuten oder Sekunden konfigurierbar
* Die ursprünglichen Ereignisdaten werden in der Ausgabe beibehalten
* Der Prozessor funktioniert mit jedem Typ von Eingabeereignis
* Ergebnisse werden ausgegeben, wenn der Frequenzänderungsschwellenwert überschritten wird 