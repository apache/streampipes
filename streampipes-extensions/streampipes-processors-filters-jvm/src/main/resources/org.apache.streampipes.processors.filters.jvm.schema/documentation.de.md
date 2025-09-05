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

## Zusammenführung nach Schema

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Zusammenführung nach Schema-Prozessor kombiniert Ereignisse aus zwei Eingabeströmen, die dasselbe Ereignisschema teilen. Er stellt die Datenkonsistenz sicher, indem er nur Ereignisse zusammenführt, die identische Strukturen haben. Dieser Prozessor ist wichtig für:
* Schema-Validierung
* Durchsetzung der Datenkonsistenz
* Stream-Synchronisierung
* Überprüfung der Ereignisstruktur

***

## Erforderliche Eingabe
Der Prozessor benötigt zwei Eingabeströme, die folgendes haben müssen:
* Identische Ereignisschemas
* Übereinstimmende Eigenschaftsnamen und -typen
* Kompatible Datenstrukturen

***

## Konfiguration
Es ist keine zusätzliche Konfiguration erforderlich. Der Prozessor:
* Validiert automatisch die Schema-Kompatibilität
* Stellt strukturelle Konsistenz sicher
* Erhält die Datenintegrität

## Ausgabe
Der Prozessor leitet Ereignisse aus beiden Eingabeströmen weiter und stellt sicher, dass sie ihre ursprüngliche Struktur und Werte beibehalten.

### Beispiel

#### Eingabestrom 1 Ereignis
```json
{
  "temperature": 25.5,
  "humidity": 60,
  "timestamp": 1586380104915
}
```

#### Eingabestrom 2 Ereignis
```json
{
  "temperature": 26.0,
  "humidity": 65,
  "timestamp": 1586380105015
}
```

#### Ausgabe-Ereignisse
Beide Ereignisse werden weitergeleitet, da sie dasselbe Schema teilen:
```json
{
  "temperature": 25.5,
  "humidity": 60,
  "timestamp": 1586380104915
}
```
```json
{
  "temperature": 26.0,
  "humidity": 65,
  "timestamp": 1586380105015
}
```

## Anwendungsfälle

1. **Datenvalidierung**
   * Sicherstellung konsistenter Datenstrukturen
   * Validierung von Ereignisschemas
   * Aufrechterhaltung der Datenqualität
   * Durchsetzung der Schema-Konformität

2. **Stream-Synchronisierung**
   * Kombinieren kompatibler Datenströme
   * Zusammenführen homogener Datenquellen
   * Aufrechterhaltung der Datenkonsistenz
   * Sicherstellung struktureller Ausrichtung

3. **Qualitätssicherung**
   * Überprüfung der Datenstrukturintegrität
   * Validierung von Ereignisformaten
   * Sicherstellung der Schema-Kompatibilität
   * Aufrechterhaltung von Datenstandards

## Hinweise

* Der Prozessor wirft eine Ausnahme, wenn die Schemas nicht übereinstimmen
* Alle Ereignisse behalten ihre ursprüngliche Struktur
* Es werden keine Datentransformationen durchgeführt
* Ereignisse werden unverändert aus beiden Strömen weitergeleitet
* Die Schema-Validierung erfolgt zur Laufzeit 