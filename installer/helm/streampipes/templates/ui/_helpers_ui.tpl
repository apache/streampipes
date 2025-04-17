{{/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/}}

{{/*
Maximum of 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "streampipes.ui.name" -}}
{{ include "streampipes.name" . | trunc 55 }}-ui
{{- end }}

{{- define "streampipes.ui.fullname" -}}
{{ include "streampipes.fullname" . | trunc 55 }}-ui
{{- end }}


{{/*
Backend common labels
*/}}
{{- define "streampipes.ui.labels" -}}
{{ include "streampipes.labels" . }}
app.kubernetes.io/component: ui
{{- end }}

{{/*
Backend selector labels
*/}}
{{- define "streampipes.ui.selectorLabels" -}}
{{ include "streampipes.selectorLabels" . }}
app.kubernetes.io/component: ui
{{- end }}


{{/*
Streampipes backend K8s Service
*/}}
{{- define "streampipes.ui.service" -}}
{{ printf "%s.%s.svc.cluster.local"  (include "streampipes.ui.fullname" .) .Release.Namespace | trimSuffix "-" }}
{{- end }}
