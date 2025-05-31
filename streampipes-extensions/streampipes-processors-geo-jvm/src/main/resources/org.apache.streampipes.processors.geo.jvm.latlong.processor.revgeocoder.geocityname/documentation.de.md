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

## Geo-Stadtname-Rückwärts-Decoder

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung

Dieser Prozessor berechnet den Stadtnamen basierend auf gegebenen Breiten- und Längengradkoordinaten, die als Felder aus einer Nachricht übermittelt werden.
Der Prozessor lädt automatisch die Datei cities1000.zip von <a href="http://download.geonames.org/export/dump/cities1000.zip)" target="_blank">Geonames</a> herunter
(diese Datei wird unter der <a href="https://creativecommons.org/licenses/by/4.0/)" target="_blank">CC BY 4.0 Lizenz</a> bereitgestellt).

***

## Erforderliche Eingabe

Die Eingabe-Nachricht muss Breiten- und Längengradwerte enthalten.

***

## Konfiguration

### Breitengrad

Das Feld, das den Breitengradwert enthält.

### Längengrad

Das Feld, das den Längengradwert enthält.

## Ausgabe

Gibt eine ähnliche Nachricht wie unten aus.

```
{
  'geoname': 'Colombo'
}
``` 