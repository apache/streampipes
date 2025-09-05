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

## Gleitender Durchschnitt

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Gleitender-Durchschnitt-Prozessor glättet numerische Datenströme durch Berechnung des Mittelwerts oder Medians der letzten n Werte. Dieser Prozessor ist wichtig für:
* Reduzierung von Rauschen in Sensordaten
* Glättung von Schwankungen
* Identifizierung von Trends
* Verbesserung der Datenqualität

***

## Erforderliche Eingabe
Ein numerisches Feld ist im Eingabestrom erforderlich.

***

## Konfiguration

### Numerisches Feld
* Wählen Sie das zu glättende numerische Feld aus
* Das Feld muss numerische Werte enthalten

### N-Wert
* Gibt die Fenstergröße an (Anzahl der zu berücksichtigenden vorherigen Werte)
* Größere Werte erzeugen glattere Ausgaben, erhöhen aber die Latenz
* Kleinere Werte sind reaktionsschneller, können aber mehr Rauschen zeigen

### Methode
Wählen Sie zwischen zwei Glättungsmethoden:
* **Mittelwert**: Berechnet den arithmetischen Durchschnitt der letzten n Werte
* **Median**: Verwendet den mittleren Wert der letzten n Werte (besser für Ausreißer)

## Ausgabe
Der Prozessor fügt ein neues Feld namens "filterResult" hinzu, das den geglätteten Wert enthält.

### Beispiel

#### Eingabeereignisse
```json
{
  "temperature": 25.5,
  "timestamp": 1586380104915
}
{
  "temperature": 26.0,
  "timestamp": 1586380105015
}
{
  "temperature": 25.8,
  "timestamp": 1586380105115
}
```

#### Konfiguration
* Numerisches Feld: temperature
* N-Wert: 3
* Methode: Mittelwert

#### Ausgabeereignisse
```json
{
  "temperature": 25.5,
  "timestamp": 1586380104915,
  "filterResult": 25.5
}
{
  "temperature": 26.0,
  "timestamp": 1586380105015,
  "filterResult": 25.75
}
{
  "temperature": 25.8,
  "timestamp": 1586380105115,
  "filterResult": 25.77
}
```

## Anwendungsfälle

1. **Sensordatenverarbeitung**
   * Glättung von Temperaturmessungen
   * Filterung von Rauschen aus Messungen
   * Stabilisierung von Sensorausgaben
   * Verbesserung der Datenqualität

2. **Trendanalyse**
   * Identifizierung langfristiger Muster
   * Reduzierung kurzfristiger Schwankungen
   * Hervorhebung signifikanter Änderungen
   * Überwachung des Systemverhaltens

## Hinweise

* Der Prozessor verwaltet ein gleitendes Fenster der letzten n Werte
* Die Mittelwertmethode ist empfindlicher gegenüber Ausreißern
* Die Medianmethode ist robuster gegenüber Ausreißern
* Die Fenstergröße beeinflusst die Glättungsintensität
* Originalwerte werden in der Ausgabe beibehalten
* Die ersten n-1 Ereignisse haben teilweise Fenster 