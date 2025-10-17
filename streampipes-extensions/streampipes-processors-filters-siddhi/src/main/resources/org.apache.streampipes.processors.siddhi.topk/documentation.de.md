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

## Top-K-Analyse

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Top-K-Analyse-Prozessor sammelt eingehende Ereignisse in einem konfigurierbaren Zeitfenster und gibt die Top- oder Bottom-K-Ereignisse basierend auf einem bestimmten Zählwert aus. Er:
* Aggregiert Ereignisse innerhalb eines Zeitfensters
* Rangiert Ereignisse nach ihren Zählwerten
* Unterstützt sowohl aufsteigende als auch absteigende Sortierung
* Behält die ursprünglichen Ereignisdaten bei
* Funktioniert mit jedem Ereignisstromtyp

***

## Erforderliche Eingabe
Der Prozessor benötigt einen Eingabeereignisstrom mit:
* Einem Feld, das zu zählende Werte enthält
* Einem Feld, das Zählwerte für die Rangierung enthält

***

## Konfiguration

### Feld
Wählen Sie das Feld aus, dessen Werte gezählt und rangiert werden sollen.

### Zählfeld
Wählen Sie das Feld aus, das die für die Rangierung der Ereignisse verwendeten Zählwerte enthält.

### Batch-Fenstergröße
Geben Sie die Anzahl der Ereignisse an, die in jedes Batch-Fenster aufgenommen werden sollen.

### Zeitfenster-Skala
Wählen Sie die Zeiteinheit für das Batch-Fenster:
* Stunden
* Minuten
* Sekunden

### Limit
Geben Sie die maximale Anzahl der auszugebenden Ereignisse an (K-Wert).

### Reihenfolge
Wählen Sie die Sortierreihenfolge:
* Aufsteigend: Gibt die Bottom-K-Ereignisse aus
* Absteigend: Gibt die Top-K-Ereignisse aus

## Ausgabe
Der Prozessor gibt eine Liste der Top- oder Bottom-K-Ereignisse aus jedem Batch-Fenster aus. Jedes Ereignis in der Liste enthält:
* Das Wertefeld aus dem Eingabeereignis
* Den für die Rangierung verwendeten Zählwert

### Beispiel

#### Eingabeereignis
```json
{
  "device_id": "device1",
  "measurement": "temperature",
  "value": 25.5,
  "occurrences": 15,
  "timestamp": 1586380105115
}
```

#### Konfiguration
* Feld: `device_id`
* Zählfeld: `occurrences`
* Batch-Fenstergröße: `60`
* Zeitfenster-Skala: `Sekunden`
* Limit: `3`
* Reihenfolge: `Absteigend`

#### Ausgabeereignis
```json
{
  "top": [
    {
      "value": "device1",
      "count": 15
    },
    {
      "value": "device2",
      "count": 12
    },
    {
      "value": "device3",
      "count": 10
    }
  ]
}
```

## Anwendungsfälle

1. **Leistungsanalyse**
   * Identifizieren der leistungsstärksten Sensoren
   * Überwachen von Hochfrequenzereignissen
   * Verfolgen von Ressourcennutzungsmustern
   * Analysieren von Systemengpässen

2. **Anomalieerkennung**
   * Finden ungewöhnlicher Ereignismuster
   * Identifizieren von Ausreißern
   * Überwachen des Systemverhaltens
   * Erkennen von Anomalien

3. **Ressourcenoptimierung**
   * Identifizieren hochausgelasteter Ressourcen
   * Überwachen der Systemlast
   * Verfolgen von Leistungsmetriken
   * Optimieren der Ressourcenzuweisung

## Hinweise

* Der Prozessor verwendet ein gleitendes Zeitfenster für die Analyse
* Ereignisse werden basierend auf ihren Zählwerten rangiert
* Die Ausgabe enthält die ursprünglichen Ereignisdaten
* Das Zeitfenster ist in Stunden, Minuten oder Sekunden konfigurierbar
* Der Prozessor unterstützt sowohl Top-K- als auch Bottom-K-Analyse 