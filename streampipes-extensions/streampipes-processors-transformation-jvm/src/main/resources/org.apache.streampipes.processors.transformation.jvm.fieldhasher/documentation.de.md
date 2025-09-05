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

## Feld-Hasher

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung

Der Feld-Hasher-Prozessor ist eine Datentransformationskomponente, die kryptographische Hash-Funktionen auf String-Werte in einem Datenstrom anwendet. Er kann verwendet werden, um sensible Informationen zu kodieren, eindeutige Identifikatoren zu generieren oder Daten für Datenschutz- und Sicherheitszwecke zu transformieren.

Der Prozessor unterstützt drei weit verbreitete Hash-Algorithmen:
* **MD5** - Eine 128-Bit-Hash-Funktion
* **SHA1** - Eine 160-Bit-Hash-Funktion
* **SHA2** - Eine 256-Bit-Hash-Funktion (SHA-256-Implementierung)

***

## Erforderliche Eingabe

Dieser Prozessor benötigt einen Ereignisstrom, der mindestens ein Feld vom Typ String enthält. Das String-Feld wird als Eingabe für die Hash-Funktion verwendet.

***

## Konfiguration

### Feld
Gibt das String-Feld an, das kodiert werden soll. Dieses Feld muss im Eingabe-Ereignisstrom existieren und String-Werte enthalten.

### Hash-Algorithmus
Wähle den Hash-Algorithmus aus, der für die Kodierung des String-Feldes verwendet werden soll. Verfügbare Optionen sind:
* **SHA1** - Erzeugt einen 40-Zeichen langen Hexadezimal-Hash
* **SHA2** - Erzeugt einen 64-Zeichen langen Hexadezimal-Hash
* **MD5** - Erzeugt einen 32-Zeichen langen Hexadezimal-Hash

## Ausgabe

Der Prozessor modifiziert das Eingabe-Ereignis, indem er den Wert des ausgewählten Feldes durch seine gehashte Version ersetzt. Alle anderen Felder im Ereignis bleiben unverändert.

### Beispiel

#### Eingabe-Ereignis
```json
{
  "timestamp": 1617183834000,
  "sensorId": "sensor123",
  "value": 42.5,
  "user": "john.doe@example.com"
}
```

#### Konfiguration
* Feld: user
* Hash-Algorithmus: MD5

#### Ausgabe-Ereignis
```json
{
  "timestamp": 1617183834000,
  "sensorId": "sensor123",
  "value": 42.5,
  "user": "e87f955d3b3499b8b13e901fd61b6b64"
}
```

## Anwendungsfälle

1. **Datenschutz**: Hash personenbezogener Daten (PII) vor Speicherung oder Übertragung
2. **Datenanonymisierung**: Erstellung anonymer Identifikatoren aus Benutzerdaten
3. **Prüfsummen-Generierung**: Generierung von Prüfsummen für Datenvalidierung
4. **Eindeutige ID-Erstellung**: Erstellung eindeutiger Identifikatoren aus Kombinationen von Feldern

## Hinweise

* Die Hash-Funktionen sind Einweg-Transformationen - der ursprüngliche Wert kann nicht aus dem Hash wiederhergestellt werden
* Die gleiche Eingabe erzeugt immer den gleichen Hash-Wert
* Unterschiedliche Hash-Algorithmen bieten unterschiedliche Kollisionsresistenz und Ausgabelängen
* Für sicherheitskritische Anwendungen sollte SHA2 verwendet werden, da es stärkere kryptographische Eigenschaften bietet 