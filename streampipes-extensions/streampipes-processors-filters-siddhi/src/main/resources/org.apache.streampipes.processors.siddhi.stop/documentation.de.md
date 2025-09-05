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

## Stromstopp-Erkennung

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Stromstopp-Erkennungs-Prozessor überwacht einen Eingabestrom und löst ein Ereignis aus, wenn für eine bestimmte Dauer keine neuen Ereignisse eintreffen. Er:
* Erkennt Stromunterbrechungen
* Überwacht Ereigniseintrittsmuster
* Löst Alarme bei Stromstopps aus
* Liefert Zeitstempel der Erkennung
* Funktioniert mit jedem Ereignisstromtyp

***

## Erforderliche Eingabe
Der Prozessor funktioniert mit jedem Eingabeereignisstrom und benötigt keine spezifischen Felder.

***

## Konfiguration

### Zeitfensterlänge (Sekunden)
Geben Sie die Dauer in Sekunden an, die auf Ereignisse gewartet werden soll, bevor die Stopp-Erkennung ausgelöst wird. Wenn innerhalb dieses Zeitfensters keine Ereignisse eintreffen, gibt der Prozessor ein Stopp-Erkennungsereignis aus.

## Ausgabe
Der Prozessor gibt ein Ereignis mit einem Zeitstempel und einer Nachricht aus, die anzeigt, dass der Strom gestoppt ist.

### Beispiel

#### Eingabeereignis
```json
{
  "sensor_id": "sensor1",
  "temperature": 25.5,
  "timestamp": 1586380105115
}
```

#### Konfiguration
* Zeitfensterlänge: `60` (Sekunden)

#### Ausgabeereignis
```json
{
  "timestamp": 1586380165115,
  "message": "Ereignisstrom ist gestoppt"
}
```

## Anwendungsfälle

1. **Systemüberwachung**
   * Erkennen von Sensorausfällen
   * Überwachen der Datenquellengesundheit
   * Verfolgen der Stromzuverlässigkeit
   * Identifizieren von Verbindungsproblemen

2. **Alarmgenerierung**
   * Alarme bei Stromstopps auslösen
   * Bei Datenlücken benachrichtigen
   * Systemgesundheit überwachen
   * Anomalien erkennen

3. **Qualitätssicherung**
   * Kontinuierlichen Datenfluss sicherstellen
   * Datenkonsistenz überwachen
   * Stromzuverlässigkeit verfolgen
   * Systemleistung validieren

## Hinweise

* Der Prozessor löst aus, wenn für die angegebene Dauer keine Ereignisse eintreffen
* Die Ausgabe enthält einen Zeitstempel, wann der Stopp erkannt wurde
* Der Prozessor funktioniert mit jedem Ereignisstromtyp
* Das Zeitfenster wird in Sekunden angegeben
* Der Prozessor liefert eine klare Nachricht, die den Stromstopp anzeigt 