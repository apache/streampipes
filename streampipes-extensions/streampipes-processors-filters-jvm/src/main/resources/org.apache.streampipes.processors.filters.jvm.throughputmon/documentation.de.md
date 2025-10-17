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

## Durchsatz-Monitor

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Durchsatz-Monitor-Prozessor berechnet und meldet Durchsatzstatistiken für die Ereignisverarbeitung. Er:
* Misst Ereignisverarbeitungsraten
* Verfolgt Stapelverarbeitungsfenster
* Berechnet Ereignisse pro Sekunde
* Liefert detaillierte Zeitinformationen

***

## Erforderliche Eingabe
Der Prozessor funktioniert mit jedem Eingabe-Ereignisstrom, da er sich auf die Messung des Ereignisflusses und nicht auf den Ereignisinhalt konzentriert.

***

## Konfiguration

### Stapelgröße
Gibt die Anzahl der Ereignisse an, die gesammelt werden sollen, bevor die Durchsatzstatistiken berechnet werden. Dies bestimmt:
* Wie häufig der Durchsatz berechnet wird
* Die Granularität der Messungen
* Den Kompromiss zwischen Genauigkeit und Berichtshäufigkeit

## Ausgabe
Der Prozessor gibt ein neues Ereignis aus, das enthält:
* `timestamp`: Aktueller Systemzeitstempel
* `starttime`: Startzeit des Stapelfensters
* `endtime`: Endzeit des Stapelfensters
* `duration`: Dauer des Stapelfensters in Millisekunden
* `eventcount`: Anzahl der im Fenster verarbeiteten Ereignisse
* `throughput`: Ereignisse pro Sekunde (berechnet als eventcount / (duration/1000))

### Beispiel

#### Eingabe-Ereignisse (3 Ereignisse mit Stapelgröße 3)
```json
{
  "sensorValue": 42,
  "timestamp": 1586380104915
}
{
  "sensorValue": 43,
  "timestamp": 1586380105015
}
{
  "sensorValue": 44,
  "timestamp": 1586380105115
}
```

#### Konfiguration
* Stapelgröße: `3`

#### Ausgabe-Ereignis
```json
{
  "timestamp": 1586380105115,
  "starttime": 1586380104915,
  "endtime": 1586380105115,
  "duration": 200,
  "eventcount": 3,
  "throughput": 15.0
}
```

## Anwendungsfälle

1. **Leistungsüberwachung**
   * Überwachung des Pipeline-Durchsatzes
   * Verfolgung von Verarbeitungsraten
   * Identifizierung von Engpässen
   * Messung der Systemleistung

2. **Lasttest**
   * Messung der Verarbeitungskapazität
   * Testen von Systemgrenzen
   * Validierung der Leistung
   * Benchmarking von Verbesserungen

3. **Ressourcenplanung**
   * Schätzung des Ressourcenbedarfs
   * Kapazitätsplanung
   * Optimierung von Konfigurationen
   * Skalierung der Infrastruktur

## Hinweise

* Der Prozessor verwendet einen gleitenden Fensteransatz
* Der Durchsatz wird als Ereignisse pro Sekunde berechnet
* Alle Zeitangaben sind in Millisekunden
* Die Stapelgröße beeinflusst die Messgranularität
* Ereignisse werden beim Eintreffen gezählt
* Der Prozessor setzt nach jedem Stapelfenster zurück 