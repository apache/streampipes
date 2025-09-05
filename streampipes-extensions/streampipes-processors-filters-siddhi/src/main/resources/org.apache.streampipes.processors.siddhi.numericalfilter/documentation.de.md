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

## Numerischer Filter (Siddhi)

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Numerische Filter-Prozessor filtert numerische Werte basierend auf einem vorgegebenen Schwellenwert. Dafür verwendet er die leichte CEP-Engine Siddhi durch Ausführung einer Siddhi-Abfrage, z.B.:

```
// Filterabfrage zum Herausfiltern aller Ereignisse, die die Bedingung nicht erfüllen
from inputStreamName[numberField<10]
select *
```

***

## Erforderliche Eingabe
Der Prozessor funktioniert mit jedem Eingabeereignis, das ein Feld mit einem numerischen Wert enthält.

***

## Konfiguration

### Feld
Gibt den Feldnamen an, auf den die Filteroperation angewendet werden soll.

### Operation
Gibt die Filteroperation an, die auf das Feld angewendet werden soll.

### Schwellenwert
Gibt den Schwellenwert an.

## Ausgabe
Der Prozessor gibt das Eingabeereignis aus, wenn es die Filterbedingung erfüllt.

## Anwendungsfälle

1. **Datenfilterung**
   * Wertebereiche überwachen
   * Schwellenwerte überprüfen
   * Anomalien erkennen
   * Datenqualität sicherstellen

2. **Echtzeitüberwachung**
   * Sensordaten filtern
   * Messwerte validieren
   * Grenzwerte überwachen
   * Abweichungen erkennen

3. **Datenvorverarbeitung**
   * Rauschunterdrückung
   * Ausreißererkennung
   * Datenbereinigung
   * Wertebereichsanpassung

## Hinweise

* Der Prozessor verwendet die Siddhi CEP-Engine für effiziente Filterung
* Die Filteroperation kann auf jedes numerische Feld angewendet werden
* Ereignisse, die die Bedingung nicht erfüllen, werden verworfen
* Die ursprünglichen Ereignisdaten werden in der Ausgabe beibehalten
* Der Prozessor unterstützt verschiedene Vergleichsoperationen 