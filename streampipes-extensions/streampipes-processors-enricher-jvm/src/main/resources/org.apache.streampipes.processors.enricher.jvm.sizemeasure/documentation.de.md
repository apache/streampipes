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

## Größenmessung

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Größenmessung-Prozessor berechnet und fügt die Größe eingehender Ereignisse hinzu. Er:
* Misst die Ereignisgröße durch Serialisierung
* Unterstützt mehrere Größeneinheiten (Bytes, Kilobytes, Megabytes)
* Behält die ursprünglichen Ereignisdaten bei
* Fügt Größeninformationen als neues Feld hinzu

***

## Erforderliche Eingabe
Der Prozessor funktioniert mit jedem Eingabe-Ereignisstrom, da er die Größe der gesamten Ereignisstruktur misst.

***

## Konfiguration

### Größeneinheit
Wählen Sie die Einheit, in der die Ereignisgröße gemessen werden soll:
* **Bytes**: Rohgröße in Bytes
* **Kilobytes**: Größe geteilt durch 1024 (1 KB = 1024 Bytes)
* **Megabytes**: Größe geteilt durch 1048576 (1 MB = 1024 KB)

## Ausgabe
Der Prozessor leitet das Eingabe-Ereignis mit einem zusätzlichen Feld namens `eventSize` weiter, das die Größe des Ereignisses in der ausgewählten Einheit enthält.

### Beispiel

#### Eingabe-Ereignis
```json
{
  "temperature": 25.5,
  "humidity": 60,
  "timestamp": 1586380105115
}
```

#### Konfiguration
* Größeneinheit: `Kilobytes`

#### Ausgabe-Ereignis
```json
{
  "temperature": 25.5,
  "humidity": 60,
  "timestamp": 1586380105115,
  "eventSize": 0.09375
}
```

## Anwendungsfälle

1. **Leistungsüberwachung**
   * Verfolgung von Ereignisgrößen über die Zeit
   * Überwachung des Datenvolumens
   * Identifizierung großer Ereignisse
   * Optimierung der Datenübertragung

2. **Ressourcenplanung**
   * Schätzung des Speicherbedarfs
   * Planung der Netzwerkkapazität
   * Optimierung der Puffergrößen
   * Skalierung der Infrastruktur

3. **Fehlerbehebung**
   * Identifizierung überdimensionierter Ereignisse
   * Verfolgung des Datenwachstums
   * Überwachung des Serialisierungsaufwands
   * Behebung von Leistungsproblemen

## Hinweise

* Die Größenmessung umfasst alle Ereignisfelder und Metadaten
* Die Größe wird durch Java-Serialisierung berechnet
* Ergebnisse werden als doppelt genaue Fließkommazahlen gespeichert
* Die ursprüngliche Ereignisstruktur bleibt erhalten
* Die Größe wird für jedes eingehende Ereignis gemessen
* Das Ergebnisfeld heißt immer `eventSize`
* Die Größenmessung fügt etwas Verarbeitungsaufwand hinzu 