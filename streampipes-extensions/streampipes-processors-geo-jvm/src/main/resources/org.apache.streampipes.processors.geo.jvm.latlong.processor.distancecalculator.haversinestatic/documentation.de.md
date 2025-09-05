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

## Entfernungsrechner Statisch (Haversine)

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung

Berechnet die Entfernung mit der <a href="https://en.wikipedia.org/wiki/Haversine_formula" target="_blank">Haversine-Formel</a> zwischen einem festen Standort (z.B. einem Ort) und einem Breiten-/Längengrad-Paar einer Eingabe-Nachricht.

***

## Erforderliche Eingabe

Benötigt einen Datenstrom, der Breiten- und Längengradwerte bereitstellt.

***

## Konfiguration

Beschreibe die Konfigurationsparameter hier

### Breitengradfeld

Das Feld, das den Breitengradwert enthält.

### Längengradfeld

Das Feld, das den Längengradwert enthält.

### Breitengrad

Der Breitengradwert des festen Standorts

### Längengrad

Der Längengradwert des festen Standorts

## Ausgabe

Gibt eine ähnliche Nachricht wie unten aus.

```
{
  'distance': 12.5
}
``` 