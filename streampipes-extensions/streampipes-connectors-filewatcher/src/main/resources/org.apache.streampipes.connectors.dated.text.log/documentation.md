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

The Dated Text Log adapter watches one directory for newline-terminated text records.
Create one adapter instance per machine directory and set its timezone to the machine's local timezone.

## Match the filename and extract the date

The **Filename expression** is a Java regular expression (regex) applied to the whole filename.
Text in parentheses forms a *capture group*. Use exactly one capture group here, around the date.
The **Filename date format** tells the adapter how to interpret the captured characters.
Enter the expressions below directly in the adapter form, using a single backslash as shown.

| Example filename | Filename expression | Captured date | Filename date format |
| --- | --- | --- | --- |
| `prefix_2026_10_29.log` (default) | `prefix_(\d{4}_\d{2}_\d{2})\.log` | `2026_10_29` | `uuuu_MM_dd` |
| `his_2026_10_29.log` | `his_(\d{4}_\d{2}_\d{2})\.log` | `2026_10_29` | `uuuu_MM_dd` |
| `events-29102026.txt` | `events-(\d{8})\.txt` | `29102026` | `ddMMuuuu` |

The prefix and separators match literal text. `\d{4}` matches four digits and `\.` matches a literal dot.
If the same stream uses alternative prefixes, `(?:his|history)_(\d{4}_\d{2}_\d{2})\.log` accepts either
`his_` or `history_`. The `(?:...)` groups the choices without creating a second capture.

## Map a log line to event fields

The **Line expression** matches the whole line. Each capturing pair of parentheses supplies one
entry in **Output fields**, in left-to-right order. For the following alarm, use the default line
expression, output fields and **Time format** (`HH:mm:ss`).

```text
06:12:00, CNC 1, Channel 0, CNCWARNING, Errornumber 55, 1, Störung, Achse 1
```

**Line expression**

```text
(\d{2}:\d{2}:\d{2}),\s*(.+?),\s*Errornumber\s+(\d+),\s*(\d+),\s*(.*)
```

**Output fields**

```text
timestamp:time,source:string,error_number:integer,state:integer,message:string
```

| Group | Captured text | Output |
| --- | --- | --- |
| 1 | `06:12:00` | `timestamp` combines this time with the filename date and machine timezone to produce Unix milliseconds. |
| 2 | `CNC 1, Channel 0, CNCWARNING` | `source` retains the full source, including internal commas. |
| 3 | `55` | `error_number` is the integer 55. |
| 4 | `1` | `state` is the integer 1. |
| 5 | `Störung, Achse 1` | `message` retains the rest of the line. |

Here `\s*` allows optional whitespace after a comma. The reluctant capture `(.+?)` takes the shortest
source that lets the following `,\s*Errornumber\s+` match. This boundary works for both `PLC_RC2`
and multi-part CNC sources. The final `(.*)` captures the remaining message, including an empty message.

## Adapt the expression to another layout

Suppose `events-29102026.txt` contains the UTF-8 line `spindle|061200|-12.5`.
Use the filename settings from the third row above and set **File encoding** to `UTF-8`.

| Setting | Value |
| --- | --- |
| Time format | `HHmmss` |
| Output fields | `sensor:string,observed:time,value:double` |

**Line expression**

```text
([^|]+)\|(\d{6})\|(-?\d+(?:\.\d+)?)
```

`[^|]+` reads the sensor name up to the first pipe, and `\|` matches the pipe separator.
The last capture accepts negative or positive values such as `-12.5` and `12`. Its optional decimal
part uses a non-capturing group, so the line still produces exactly three fields. Here the timestamp
is the second capture and the output field is named `observed`.

## Check the configuration

- Use schema sampling with a representative, newline-terminated record. If sampling finds no records,
  check the filename expression against the basename, including the extension, and confirm the encoding.
- If the capture count is wrong, count capturing parentheses from left to right. Use `(?:...)` for
  alternatives or optional text that does not supply an output field. Each output capture must
  participate in every match. For an optional message after a fixed delimiter, `(.*)` can capture
  an empty string.
- If conversion fails, compare the captured text with its field type and format. Use exactly one
  `time` field. Other types are `string`, `integer`, `long` and `double`. Date and time formats use
  Java DateTimeFormatter syntax, with `MM` for month and `mm` for minutes.

## Reading and resuming files

Schema sampling reads the first complete record from matching files. Ingestion accepts LF and CRLF,
skips empty lines and leading byte-order marks, and waits for a newline before emitting a trailing record.
A malformed complete record stops the poll with its filename and line number.

The watcher reads daily files in date order, using the filename to break ties. It keeps the active filename,
its date, content fingerprint, and last processed record in the adapter checkpoint. Appends resume from
that record. A replacement of the active file is read from the beginning. An incomplete trailing line
delays rollover until the writer finishes the line or the file is removed.

Use this adapter when each daily file is final by the time ingestion advances to the next file.
Earlier dates added or modified after that point are skipped. A crash between collection and
checkpoint persistence can replay the last event.
