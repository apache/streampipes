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

## Sensor-Grenzwert-Alarm

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung

Der Sensor-Grenzwert-Alarm-Prozessor überwacht Sensorwerte in Echtzeit und löst Alarme aus, wenn diese Werte benutzerdefinierte Regel- oder Warnlimiten überschreiten. Dieser Prozessor ist nützlich in Szenarien, in denen eine kontinuierliche Überwachung kritischer Parameter erforderlich ist und sofortige Maßnahmen erforderlich sind, wenn Werte außerhalb akzeptabler Bereiche liegen.

***

## Erforderliche Eingabe

Dieser Prozessor akzeptiert jeden Ereignisstrom, der Sensordaten enthält. Die Ereignisse müssen Felder für Sensorwerte und die entsprechenden oberen und unteren Grenzen enthalten.

***

## Konfiguration

#### Sensorwert

Wählen Sie den zu überwachenden Sensorwert aus. Dies ist die primäre Messung, die gegen die definierten Grenzen geprüft wird.

#### Obere Regelgrenze

Geben Sie die obere Regelgrenze für den Sensor an. Dieser Wert definiert den maximalen Schwellenwert, bei dessen Überschreitung ein Alarm ausgelöst wird.

#### Obere Warnlinie

Geben Sie die obere Warnlinie für den Sensor an. Dieser Wert zeigt an, wenn sich der Sensorwert der oberen Regelgrenze nähert und löst eine Warnung aus.

#### Untere Warnlinie

Geben Sie die untere Warnlinie für den Sensor an. Dieser Wert zeigt an, wenn sich der Sensorwert der unteren Regelgrenze nähert und löst eine Warnung aus.

#### Untere Regelgrenze

Geben Sie die untere Regelgrenze für den Sensor an. Dieser Wert definiert den minimalen Schwellenwert, bei dessen Unterschreitung ein Alarm ausgelöst wird.

***

## Ausgabe

Der Prozessor gibt Ereignisse nur aus, wenn der Sensorwert die angegebenen Grenzen überschreitet. Das Ausgabe-Ereignis enthält die ursprünglichen Sensordaten zusammen mit zusätzlichen Feldern, die angeben:
- **Alarmstatus**: Ob der Sensorwert eine WARNUNG oder Regelgrenze überschritten hat.
- **Überschrittene Grenze**: Welche spezifische Grenze überschritten wurde (z.B. "OBERE_REGELGRENZE" oder "UNTERE_WARNLINIE").

Diese Ausgabe-Ereignisse können für die Auslösung von Benachrichtigungen oder anderen Aktionen in der nachgelagerten Verarbeitung verwendet werden.

***

## Beispiel

### Benutzerkonfiguration
- Feldzuordnung für:
  - **Sensorwert**
  - **Obere Regelgrenze**
  - **Obere Warnlinie**
  - **Untere Warnlinie**
  - **Untere Regelgrenze**

### Eingabe-Ereignis
```json
{
  "timestamp": 1627891234000,
  "sensorValue": 105.0,
  "upperControlLimit": 100.0,
  "upperWarningLimit": 90.0,
  "lowerWarningLimit": 10.0,
  "lowerControlLimit": 0.0
}
```

### Ausgabe-Ereignis
```json
{
  "timestamp": 1627891234000,
  "sensorValue": 105.0,
  "upperControlLimit": 100.0,
  "upperWarningLimit": 90.0,
  "lowerWarningLimit": 10.0,
  "lowerControlLimit": 0.0,
  "alertStatus": "ALERT",
  "limitBreached": "UPPER_CONTROL_LIMIT"
}
``` 