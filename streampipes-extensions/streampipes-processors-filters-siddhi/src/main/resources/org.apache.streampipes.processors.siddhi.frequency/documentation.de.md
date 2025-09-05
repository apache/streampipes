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

## Frequenzüberwachung

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Frequenzüberwachungs-Prozessor erkennt, wenn Ereignisse innerhalb eines angegebenen Zeitfensters nicht mehr eintreffen. Er:
* Überwacht die Ereigniseintrittsfrequenz
* Erkennt, wenn Ereignisse nicht mehr eintreffen
* Unterstützt konfigurierbare Zeitfenster
* Funktioniert mit jedem Eingabeereignisstrom
* Behält die ursprünglichen Ereignisdaten bei

***

## Erforderliche Eingabe
Der Prozessor funktioniert mit jedem Eingabeereignisstrom. Es werden keine spezifischen Eingabeanforderungen benötigt.

***

## Konfiguration

### Zeitfensterlänge
Geben Sie die Dauer des Zeitfensters in Sekunden an. Wenn innerhalb dieses Fensters keine Ereignisse eintreffen, erkennt der Prozessor eine Frequenzänderung.

### Zeiteinheit
Wählen Sie die Zeiteinheit für die Fenstergröße:
* Stunden (Std)
* Minuten (Min)
* Sekunden (Sek)

## Ausgabe
Der Prozessor gibt das ursprüngliche Ereignis aus, wenn innerhalb des angegebenen Zeitfensters keine Ereignisse eintreffen.

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

#### Ausgabeereignis
Der Prozessor gibt das letzte empfangene Ereignis aus, wenn innerhalb von 30 Sekunden keine neuen Ereignisse eintreffen.

## Anwendungsfälle

1. **Systemüberwachung**
   * Sensorenausfälle erkennen
   * Datenquellengesundheit überwachen
   * Verbindungsprobleme identifizieren
   * Systemverfügbarkeit verfolgen

2. **Alarmgenerierung**
   * Bei Datenlücken Alarme auslösen
   * Bei Systemausfällen benachrichtigen
   * Über Dienstunterbrechungen berichten
   * Datenflusskontinuität überwachen

3. **Qualitätssicherung**
   * Kontinuierlichen Datenfluss sicherstellen
   * Zuverlässigkeit der Datenerfassung überwachen
   * Systemleistung verfolgen
   * Datenquellengesundheit validieren

## Hinweise

* Der Prozessor erkennt das Fehlen von Ereignissen
* Das Zeitfenster ist in Stunden, Minuten oder Sekunden konfigurierbar
* Die ursprünglichen Ereignisdaten werden in der Ausgabe beibehalten
* Der Prozessor funktioniert mit jedem Typ von Eingabeereignis
* Ergebnisse werden ausgegeben, wenn das Zeitfenster ohne neue Ereignisse abläuft 