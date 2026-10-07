<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to you under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

# Kamelet transform steps

The optional **Transform Steps (YAML)** field supports a data-only subset, evaluated
before delivery to the configured Kamelet sink. Empty input and `[]` forward the
mapped message unchanged. Existing event-map, JSON and mapped-field payload modes
and header mappings remain available.

Supported steps are `setBody`, `setHeader` and `removeHeader` (also spelled
`set-body`, `set-header` and `remove-header`). Each list entry contains exactly one
step. `setHeader` and `removeHeader` require a literal `name` containing only ASCII
letters, digits, underscores or hyphens (1–128 characters). Names starting with
`Camel`, regardless of case, are reserved for endpoint control and rejected.

`setBody` and `setHeader` require exactly one of:

- `constant`: a quoted YAML string, used literally without placeholder expansion.
- `simple`: exactly `${body}` or `${body[field]}`. The latter reads one top-level
  map key containing ASCII letters, digits, underscores or hyphens. It returns
  null for an absent key or a non-map body. No methods, nested access or string
  interpolation are evaluated.

```yaml
- setHeader:
    name: example
    simple: "${body[myField]}"
- setBody:
    constant: "literal payload"
- removeHeader:
    name: temporary
```

All other steps, options, expression languages, user-selected endpoints and nested
DSL constructs are rejected before installing the route. YAML must contain a
single document; duplicate keys and aliases are rejected. Limits are 16,384 Java
string characters, 64 steps and four collection levels.

This is a restricted data transformation interpreter, not a sandbox for arbitrary
Camel code. Installed Kamelet templates, their components, endpoint parameters
and extension code remain trusted and are not constrained by this interpreter.
Previously accepted transforms outside this subset must be replaced with supported
data mappings or implemented in an operator-reviewed extension.
