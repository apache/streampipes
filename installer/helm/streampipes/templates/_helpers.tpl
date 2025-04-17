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
{{/* vim: set filetype=mustache: */}}

{{/*
Expand the name of the chart.
*/}}
{{- define "streampipes.name" -}}
  {{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "streampipes.fullname" -}}
  {{- if .Values.fullnameOverride }}
  {{- .Values.fullnameOverride | trunc 63 | trimSuffix "-"}}
  {{- else}}
  {{- $name := default .Chart.Name .Values.nameOverride -}}
  {{- if contains $name .Release.Name -}}
    {{- .Release.Name | trunc 63 | trimSuffix "-" -}}
  {{- else -}}
    {{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
  {{- end -}}
  {{- end -}}
{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "streampipes.chart" -}}
  {{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}


{{/*
Common labels.
*/}}
{{- define "streampipes.labels" -}}
helm.sh/chart: {{ include "streampipes.chart" . }}
{{ include "streampipes.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "streampipes.selectorLabels" -}}
app.kubernetes.io/name: {{ include "streampipes.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "streampipes.serviceAccountName" -}}
{{- if .Values.serviceAccount.create -}}
{{- default (include "streampipes.fullname" .) .Values.serviceAccount.name | quote }}
{{- else -}}
{{- default "default" .Values.serviceAccount.name | quote }}
{{- end -}}
{{- end -}}

{{/*
Generate the fullname of the couchdb subchart
*/}}
{{- define "streampipes.couchdb.fullname" -}}
{{- printf "%s-%s" .Release.Name "couchdb" | trunc 63 | trimSuffix "-" }}
{{- end -}}

{{/*
Generate the fullname of the influxdb subchart
*/}}
{{- define "streampipes.influxdb.fullname" -}}
{{- printf "%s-%s" .Release.Name "influxdb" | trunc 63 | trimSuffix "-" }}
{{- end -}}


{{/*
Generate the fullname of the kafka subchart
*/}}
{{- define "streampipes.kafka.fullname" -}}
{{- printf "%s-%s" .Release.Name "kafka" | trunc 63 | trimSuffix "-" }}
{{- end -}}

{{/*
Generate the fullname of the nats subchart
*/}}
{{- define "streampipes.nats.fullname" -}}
{{- printf "%s-%s" .Release.Name "nats" | trunc 63 | trimSuffix "-" }}
{{- end -}}

{{/*
Generate the fullname of the pulsar subchart
*/}}
{{- define "streampipes.pulsar.fullname" -}}
{{- printf "%s-%s" .Release.Name "pulsar" | trunc 63 | trimSuffix "-" }}
{{- end -}}


{{/* Generate backend image name */}}
{{- define "streampipes.backend.image" -}}
{{- printf "%s/%s:%s" .Values.image.registry .Values.backend.image (.Values.backend.tag | default .Chart.AppVersion) }}
{{- end }}

{{/* Generate ui image url */}}
{{- define "streampipes.ui.image" -}}
{{- printf "%s/%s:%s" .Values.image.registry .Values.ui.image (.Values.ui.tag | default .Chart.AppVersion) }}
{{- end}}

{{/* Generate extensions image url */}}
{{- define "streampipes.extensions.image" -}}
{{- printf "%s/%s:%s" .Values.image.registry .Values.extensions.image (.Values.extensions.tag | default .Chart.AppVersion) }}
{{- end}}

{{/*
Generate the initContainer check for couchDB 
*/}}
{{- define "streampipes.initContainer" -}}
- name: wait-for-db
  image: alpine
  imagePullPolicy: {{ .Values.image.pullPolicy }}
  command: ["sh", "-c", "for i in $(seq 1 300); do nc -zvw1 {{- include "snippet.couchdb.host" . | nindent 1 }} {{- include "snippet.couchdb.port" . | nindent 1 }} && exit 0; sleep 3; done; exit 1"] 
{{- end -}}

