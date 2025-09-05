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

## Swinging Door Trending (SDT) Filter Prozessor

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>


***

## Beschreibung

Der **Swinging Door Trending (SDT)** Algorithmus ist ein linearer Trendkompressionsalgorithmus.
Im Wesentlichen ersetzt er eine Reihe kontinuierlicher `(Zeitstempel, Wert)`-Punkte durch eine Gerade, die durch Start- und Endpunkte bestimmt wird.

Der **Swinging Door Trending (SDT) Filter Prozessor** kann die charakteristischen Ereignisse des Originalstroms extrahieren und weiterleiten.
Im Allgemeinen kann dieser Filter auch verwendet werden, um die Frequenz der Originaldaten verlustbehaftet zu reduzieren.

***

## Erforderliche Eingaben

Der Prozessor arbeitet mit jedem Eingabeereignis, das **ein Feld mit einem Zeitstempel** und
**ein Feld mit einem numerischen Wert** enthält.

***

## Konfiguration

### Zeitstempelfeld
Gibt den Namen des Zeitstempelfelds an, auf das der SDT-Algorithmus angewendet werden soll.

### Wertefeld
Gibt den Namen des Wertefelds an, auf das der SDT-Algorithmus angewendet werden soll.

### Kompressionsabweichung
Die **Kompressionsabweichung** ist der wichtigste Parameter in SDT und stellt die maximale Differenz
zwischen der aktuellen Probe und dem aktuellen linearen Trend dar.

Die **Kompressionsabweichung** muss größer als 0 sein, um eine Kompression durchzuführen.

### Minimale Kompressionszeit
Die **Minimale Kompressionszeit** ist ein Parameter, der den Zeitabstand zwischen zwei gespeicherten Datenpunkten misst,
der zur Rauschunterdrückung verwendet wird.

Wenn das Zeitintervall zwischen dem aktuellen Punkt und dem letzten gespeicherten Punkt kleiner oder gleich diesem Wert ist,
wird der aktuelle Punkt NICHT gespeichert, unabhängig von der Kompressionsabweichung.

Der Standardwert ist `0` mit der Zeiteinheit ms.

### Maximale Kompressionszeit
Die **Maximale Kompressionszeit** ist ein Parameter, der den Zeitabstand zwischen zwei gespeicherten Datenpunkten misst.

Wenn das Zeitintervall zwischen dem aktuellen Punkt und dem letzten gespeicherten Punkt größer oder gleich diesem Wert ist,
wird der aktuelle Punkt gespeichert, unabhängig von der Kompressionsabweichung.

Der Standardwert ist `9.223.372.036.854.775.807` (`Long.MAX_VALUE`) mit der Zeiteinheit ms.

***

## Ausgabe
Der charakteristische Ereignisstrom, der vom SDT-Filter weitergeleitet wird. 