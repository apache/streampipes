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

## Zählaggregation

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Zählaggregations-Prozessor führt Zähloperationen auf Ereignisströmen durch. Er:
* Zählt Vorkommen von Werten innerhalb eines konfigurierbaren Zeitfensters
* Gruppiert Ereignisse nach einem angegebenen Feld
* Liefert Echtzeit-Zählstatistiken
* Unterstützt flexible Zeitfensterkonfigurationen
* Behält Zeitstempelinformationen bei

***

## Erforderliche Eingabe
Der Prozessor benötigt einen Eingabeereignisstrom mit:
* Einem Zeitstempelfeld für fensterbasierte Verarbeitung
* Mindestens einem Feld zum Zählen der Vorkommen

***

## Konfiguration

### Zu zählendes Feld
Wählen Sie das Feld aus dem Eingabeereignis aus, das für das Zählen der Vorkommen verwendet werden soll. Die Werte dieses Felds werden gruppiert und gezählt.

### Zeitfenstergröße
Geben Sie die Dauer des Zeitfensters für die Aggregation an. Dies bestimmt, wie viele Ereignisse in jeder Zähloperation berücksichtigt werden.

### Zeitfenster-Skala
Wählen Sie die Zeiteinheit für die Fenstergröße:
* Stunden
* Minuten
* Sekunden

## Ausgabe
Der Prozessor gibt Ereignisse aus, die enthalten:
* `timestamp`: Den Zeitstempel des Fensters
* `value`: Den gezählten Wert
* `count`: Die Anzahl der Vorkommen innerhalb des Zeitfensters

### Beispiel

#### Eingabeereignis
```json
{
  "vehicleId": "V123",
  "timestamp": 1586380105115
}
```

#### Konfiguration
* Zu zählendes Feld: `vehicleId`
* Zeitfenstergröße: `5`
* Zeitfenster-Skala: `Minuten`

#### Ausgabeereignis
```json
{
  "timestamp": 1586380105115,
  "value": "V123",
  "count": 12
}
```

## Anwendungsfälle

1. **Verkehrsanalyse**
   * Fahrzeugdurchfahrten an Kreuzungen zählen
   * Verkehrsflussraten überwachen
   * Fahrzeugfrequenz verfolgen

2. **Ereignisüberwachung**
   * Fehlervorkommen zählen
   * Systemereignisse verfolgen
   * Benutzeraktionen überwachen

3. **Ressourcennutzung**
   * API-Aufrufe zählen
   * Dienstanfragen überwachen
   * Ressourcennutzung verfolgen

## Hinweise

* Der Prozessor verwendet gleitende Zeitfenster
* Zählungen werden am Ende jedes Fensters zurückgesetzt
* Zeitstempel werden aus den Eingabeereignissen beibehalten
* Der Prozessor gruppiert Ereignisse nach dem ausgewählten Feld
* Ergebnisse werden am Ende jedes Zeitfensters ausgegeben 