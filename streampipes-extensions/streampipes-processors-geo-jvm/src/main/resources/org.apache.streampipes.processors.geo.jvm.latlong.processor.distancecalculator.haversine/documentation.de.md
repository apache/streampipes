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

## Entfernungsrechner (Haversine)

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Berechnet die Entfernung zwischen zwei Breiten-/Längengrad-Paaren in einer einzelnen Nachricht mit der
<a href="https://en.wikipedia.org/wiki/Haversine_formula" target="_blank">Haversine-Formel</a>.

***

## Erforderliche Eingabe
Benötigt eine Position eines Punktes auf der Erdoberfläche, die durch die beiden geografischen Koordinaten spezifiziert wird: den Längengrad und den Breitengrad des Punktes.

***

## Konfiguration

### Erster Längengrad
Dies ist die erste geografische Koordinate, die die Ost-West-Position eines Punktes auf der Erdoberfläche spezifiziert.

### Erster Breitengrad
Dies ist die zweite geografische Koordinate, die die Nord-Süd-Position eines Punktes auf der Erdoberfläche spezifiziert.

### Zweiter Längengrad
Dies ist die zweite geografische Koordinate, die die Ost-West-Position eines Punktes auf der Erdoberfläche spezifiziert.

### Zweiter Breitengrad
Dies ist die zweite geografische Koordinate, die die Nord-Süd-Position eines Punktes auf der Erdoberfläche spezifiziert.

## Ausgabe
{
  'distance': 12.2
} 