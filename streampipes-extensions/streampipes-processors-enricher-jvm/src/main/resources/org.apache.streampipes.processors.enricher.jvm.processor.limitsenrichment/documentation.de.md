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

## Qualitätsregelkarten-Anreicherung

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung

Der Qualitätsregelkarten-Anreicherung-Prozessor fügt benutzerdefinierte Regel- und Warnlimiten zu eingehenden Ereignissen hinzu.
Diese Limiten können in Qualitätsregelkarten zur Überwachung von Sensorwerten verwendet werden.

***

## Erforderliche Eingabe

Dieser Prozessor funktioniert mit jedem Ereignisstrom. Er fügt vordefinierte Grenzwerte zu den Ereignissen hinzu, die später für
Qualitätsregelzwecke verwendet werden.

***

## Konfiguration

#### Obere Regelgrenze

Geben Sie die obere Regelgrenze für den Qualitätsregelprozess an. Dieser Wert definiert den maximalen Schwellenwert für akzeptables
Prozessverhalten.

#### Obere Warnlinie

Geben Sie die obere Warnlinie für den Qualitätsregelprozess an. Dieser Wert zeigt an, wenn sich der Prozess der
oberen Regelgrenze nähert.

#### Untere Warnlinie

Geben Sie die untere Warnlinie für den Qualitätsregelprozess an. Dieser Wert zeigt an, wenn sich der Prozess der
unteren Regelgrenze nähert.

#### Untere Regelgrenze

Geben Sie die untere Regelgrenze für den Qualitätsregelprozess an. Dieser Wert definiert den minimalen Schwellenwert für akzeptables
Prozessverhalten.

***

## Ausgabe

Der Prozessor fügt die angegebenen Regel- und Warnlimiten zu jedem Eingabe-Ereignis hinzu. Diese angereicherten Ereignisse können in
der nachgelagerten Verarbeitung zur Erstellung von Qualitätsregelkarten oder anderen Überwachungswerkzeugen verwendet werden.

***

## Beispiel

### Benutzerkonfiguration
- **Obere Regelgrenze**: `80.0`
- **Obere Warnlinie**: `70.0`
- **Untere Warnlinie**: `30.0`
- **Untere Regelgrenze**: `20.0`

### Eingabe-Ereignis
```json
{
  "timestamp": 1627891234000,
  "temperature": 65.0
}
```

### Ausgabe-Ereignis
```json
{
  "timestamp": 1627891234000,
  "temperature": 65.0,
  "upperControlLimit": 80.0,
  "upperWarningLimit": 70.0,
  "lowerWarningLimit": 30.0,
  "lowerControlLimit": 20.0
}
``` 