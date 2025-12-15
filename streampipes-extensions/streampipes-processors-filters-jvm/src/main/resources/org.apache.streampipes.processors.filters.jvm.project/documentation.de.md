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

## Projektion

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Projektion-Prozessor ermöglicht es Ihnen, eine Teilmenge von Feldern aus Eingabe-Ereignissen auszuwählen und ein neues Ereignis nur mit den angegebenen Feldern zu erstellen. Dieser Prozessor ist wichtig für:
* Reduzierung des Datenvolumens
* Fokussierung auf relevante Felder
* Datenschutz
* Stream-Optimierung

***

## Erforderliche Eingabe
Der Prozessor funktioniert mit jedem Eingabe-Ereignisstrom, der ein oder mehrere Felder enthält.

***

## Konfiguration
Bei der Pipeline-Entwicklung können Sie auswählen, welche Felder im Ausgabe-Ereignis enthalten sein sollen.

## Ausgabe
Der Prozessor erstellt ein neues Ereignis, das nur die ausgewählten Felder aus dem Eingabe-Ereignis enthält.

### Beispiel

#### Eingabe-Ereignis
```json
{
  "temperature": 25.5,
  "humidity": 60,
  "pressure": 1013,
  "timestamp": 1586380104915,
  "device_id": "sensor_001",
  "location": "room_101"
}
```

#### Konfiguration
Ausgewählte Felder:
* temperature
* humidity
* timestamp

#### Ausgabe-Ereignis
```json
{
  "temperature": 25.5,
  "humidity": 60,
  "timestamp": 1586380104915
}
```

## Anwendungsfälle

1. **Datenvolumenreduzierung**
   * Entfernen unnötiger Felder
   * Reduzierung der Netzwerkbandbreite
   * Optimierung der Speichernutzung
   * Verbesserung der Verarbeitungsgeschwindigkeit

2. **Datenschutz**
   * Entfernen sensibler Felder
   * Anonymisierung von Daten
   * Kontrolle der Datenexposition
   * Einhaltung von Vorschriften

3. **Stream-Optimierung**
   * Fokussierung auf relevante Daten
   * Reduzierung der nachgelagerten Verarbeitung
   * Verbesserung der Pipeline-Effizienz
   * Optimierung der Ressourcennutzung

## Hinweise

* Der Prozessor behält die ursprünglichen Werte der ausgewählten Felder bei
* Nicht ausgewählte Felder werden vollständig aus der Ausgabe entfernt
* Die Reihenfolge der Felder in der Ausgabe kann sich von der Eingabe unterscheiden
* Alle Feldtypen werden unterstützt
* Der Prozessor kann mit beliebig vielen Feldern umgehen 