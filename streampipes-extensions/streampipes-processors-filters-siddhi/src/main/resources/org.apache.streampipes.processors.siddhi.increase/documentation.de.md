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

## Trendanalyse

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Trendanalyse-Prozessor überwacht numerische Werte und erkennt signifikante Zunahmen oder Abnahmen innerhalb eines konfigurierbaren Zeitfensters. Er:
* Erkennt prozentuale Wertänderungen
* Unterstützt sowohl Zunahme- als auch Abnahmeerkennung
* Verwendet konfigurierbare Zeitfenster
* Behält die ursprünglichen Ereignisdaten bei
* Funktioniert mit jedem numerischen Feld

***

## Erforderliche Eingabe
Der Prozessor benötigt einen Eingabeereignisstrom mit mindestens einem numerischen Feld zur Trendüberwachung.

***

## Konfiguration

### Zu beobachtender Wert
Wählen Sie das numerische Feld aus, das auf Trends überwacht werden soll.

### Operationstyp
Wählen Sie den zu erkennenden Trendtyp:
* Zunahme: Erkennt, wenn Werte um den angegebenen Prozentsatz zunehmen
* Abnahme: Erkennt, wenn Werte um den angegebenen Prozentsatz abnehmen

### Prozentuale Änderung
Geben Sie den Prozentschwellenwert für die Trenderkennung an:
* Bei Zunahme: Werte müssen um diesen Prozentsatz zunehmen
* Bei Abnahme: Werte müssen um diesen Prozentsatz abnehmen
* Bereich: 0-500%
* Schrittweite: 1%

### Zeitfensterlänge (Sekunden)
Geben Sie die Dauer in Sekunden an, über die der Trend überwacht werden soll.

## Ausgabe
Der Prozessor gibt das ursprüngliche Ereignis aus, wenn ein signifikanter Trend innerhalb des angegebenen Zeitfensters erkannt wird.

### Beispiel

#### Eingabeereignis
```json
{
  "device_id": "device1",
  "measurement": "temperature",
  "value": 25.5,
  "timestamp": 1586380105115
}
```

#### Konfiguration
* Zu beobachtender Wert: `value`
* Operationstyp: `Zunahme`
* Prozentuale Änderung: `10`
* Zeitfensterlänge: `300`

#### Ausgabeereignis
```json
{
  "device_id": "device1",
  "measurement": "temperature",
  "value": 28.0,
  "timestamp": 1586380205115
}
```

## Anwendungsfälle

1. **Anomalieerkennung**
   * Plötzliche Temperaturänderungen erkennen
   * Druckvariationen überwachen
   * Ressourcennutzungsspitzen verfolgen
   * Ungewöhnliche Muster identifizieren

2. **Leistungsüberwachung**
   * Systemmetriken verfolgen
   * Ressourcennutzung überwachen
   * Leistungstrends analysieren
   * Verschlechterung erkennen

3. **Qualitätssicherung**
   * Prozessparameter überwachen
   * Produktqualitätsmetriken verfolgen
   * Prozessabweichungen erkennen
   * Konsistente Ausgabe sicherstellen

## Hinweise

* Der Prozessor erkennt prozentuale Änderungen
* Das Zeitfenster wird in Sekunden angegeben
* Die Ausgabe enthält die ursprünglichen Ereignisdaten
* Der Prozessor funktioniert mit jedem numerischen Feld
* Ergebnisse werden ausgegeben, wenn der Trend erkannt wird 