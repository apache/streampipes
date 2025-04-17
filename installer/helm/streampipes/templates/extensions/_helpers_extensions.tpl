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

{{- define "streampipes.extensions.name" -}}
{{ include "streampipes.name" . | trunc 55 }}-extensions
{{- end }}

{{- define "streampipes.extensions.fullname" -}}
{{ include "streampipes.fullname" . | trunc 55 }}-extensions
{{- end }}

{{- define "streampipes.extensions.labels" -}}
{{ include "streampipes.labels" . }}
app.kubernetes.io/component: extensions
{{- end }}

{{- define "streampipes.extensions.selectorLabels" -}}
{{ include "streampipes.selectorLabels" . }}
app.kubernetes.io/component: extensions
{{- end }}

{{- define "streampipes.extensions.service" -}}
{{ printf "%s.%s.svc.cluster.local" (include "streampipes.extensions.fullname" .) .Release.Namespace | trimSuffix "-" }}
{{- end }}