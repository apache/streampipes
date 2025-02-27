
{{- define  "snippet.streampipes.env" -}}
- name: SP_INITIAL_ADMIN_EMAIL
  value: {{ .Values.streampipes.admin.email | quote }}
- name: SP_INITIAL_SERVICE_USER
  value: {{ .Values.streampipes.serviceUser.user | quote }}
{{ if .Values.streampipes.secrets.existingSecret }}
- name: SP_INITIAL_ADMIN_PASSWORD
  valueFrom:
    secretKeyRef:
      name: {{ .Values.streampipes.secrets.existingSecret }}
      key: {{ required "streampipes.secrets.adminPasswordSecretKey is required if streampipes.secrets.existingSecret is not empty" .Values.streampipes.secrets.adminPasswordSecretKey | quote }}
- name: SP_INITIAL_SERVICE_USER_SECRET
  valueFrom:
    secretKeyRef:
      name: {{ .Values.streampipes.secrets.existingSecret }}
      key: {{ required "streampipes.secrets.clientSecretKey is required if streampipes.secrets.existingSecret is not empty" .Values.streampipes.secrets.clientSecretKey | quote }}
- name: SP_ENCRYPTION_PASSCODE
  valueFrom:
    secretKeyRef:
      name: {{ .Values.streampipes.secrets.existingSecret }}
      key: {{ required "streampipes.secrets.encryptionPasscodeSecretKey is required if streampipes.secrets.existingSecret is not empty" .Values.streampipes.secrets.encryptionPasscodeSecretKey | quote }}
{{- else -}}
- name: SP_INITIAL_ADMIN_PASSWORD
  value: {{ .Values.streampipes.admin.password | quote }}
- name: SP_INITIAL_SERVICE_USER_SECRET
  valueFrom:
    secretKeyRef:
      name: {{ include "snippet.streampipes.secret.name" . }}
      key: sp-initial-client-secret
- name: SP_ENCRYPTION_PASSCODE
  valueFrom:
    secretKeyRef:
      name: {{ include "snippet.streampipes.secret.name" . }}
      key: sp-encryption-passcode
{{- end }}
{{- end }}

{{- define "snippet.streampipes.proxy.env" -}}
- name: HTTP_PROXY
  value: {{ .Values.proxy | quote }}
- name: HTTPS_PROXY
  value: {{ .Values.proxy | quote }}
- name: NO_PROXY
  value: {{ .Values.noProxy | quote }}
{{- end }}

{{- define "snippet.streampipes.core.env"}}
- name: SP_HTTP_SERVER_ADAPTER_ENDPOINT
  value: {{ include "streampipes.extensions.iiot.service" . }}
- name: SP_CORE_HOST
  value: {{ include "streampipes.backend.service" . }}
- name: SP_CORE_PORT
  value: {{ .Values.backend.service.port | quote }}
{{- end }}

{{- define "snippet.streampipes.secret.client" -}}
- name: SP_CLIENT_USER
  value: {{ .Values.streampipes.serviceUser.user | quote }}
{{ if .Values.streampipes.secrets.existingSecret }}
- name: SP_CLIENT_SECRET
  valueFrom:
    secretKeyRef:
      name: {{ include "snippet.streampipes.secret.name" . | quote }}
      key: {{ .Values.streampipes.secrets.clientSecretKey }}
{{- else -}}
- name: SP_CLIENT_SECRET
  valueFrom:
    secretKeyRef:
      name: {{ include "snippet.streampipes.secret.name" . | quote }}
      key: sp-initial-client-secret
{{- end }}
{{- end }}

{{- define "snippet.streampipes.secret.name" -}}
{{ if .Values.streampipes.secrets.existingSecret -}}
  {{ .Values.streampipes.secrets.existingSecret }}
{{- else -}}
  {{ include "streampipes.fullname" . }}-secret
{{- end }}
{{- end }}


{{- define "snippet.couchdb.env" -}}
- name: SP_COUCHDB_USER
{{- if and (not .Values.couchdb.enabled) .Values.externalCouchdb.existingSecret .Values.externalCouchdb.adminUsernameKey (not .Values.externalCouchdb.adminUsername) }}
  valueFrom:
    secretKeyRef:
      name: {{ .Values.externalCouchdb.existingSecret }}
      key: {{ .Values.externalCouchdb.adminUsernameKey }}
{{- else }}
  value: {{ include "snippet.couchdb.adminUsername" . | quote }}
{{- end }}
- name: SP_COUCHDB_PASSWORD
{{- if and (not .Values.couchdb.enabled) .Values.externalCouchdb.existingSecret .Values.externalCouchdb.adminPasswordKey (not .Values.externalCouchdb.adminPassword) }}
  valueFrom:
    secretKeyRef:
      name: {{ .Values.externalCouchdb.existingSecret }}
      key: {{ .Values.externalCouchdb.adminPasswordKey }}
{{- else }}
  value: {{ include "snippet.couchdb.adminPassword" . | quote }}
{{- end }}
- name: SP_COUCHDB_HOST
  value: {{ include "snippet.couchdb.host" .}}
- name: SP_COUCHDB_PORT
  value: "5984"
{{- end }}


{{- define "snippet.couchdb.host" -}}
{{ if and (not .Values.couchdb.enabled) .Values.externalCouchdb.host -}}
  {{ .Values.externalCouchdb.host }}
{{- else -}}
  {{ include "streampipes.couchdb.fullname" . }}
{{- end }}
{{- end }}

{{- define "snippet.couchdb.port" -}}
{{- if and (not .Values.couchdb.enabled) .Values.externalCouchdb.port }}
  {{- .Values.externalCouchdb.port }}
{{- else -}}
  5984
{{- end }}
{{- end }}

{{- define "snippet.couchdb.connection_string" -}}
{{- include "snippet.couchdb.host" . }}:{{- include "snippet.couchdb.port" . }}
{{- end }}

{{- define "snippet.couchdb.secret.name" -}}
{{ if .Values.couchdb.enabled }}
    {{ include "couchdb.defaultsecret" . }}
{{- else -}}
  {{ if .Values.externalCouchdb.existingSecret }}
    {{ .Values.externalCouchdb.existingSecret }}
  {{- else -}}
    {{ include "streampipes.fullname" . }}-couchdb-secret
  {{- end }}
{{- end }}
{{- end }}

{{- define "snippet.couchdb.adminUsername" -}}
{{- if (not .Values.couchdb.enabled) }}
    {{- .Values.externalCouchdb.adminUsername }}
  {{- else -}}
    {{- .Values.couchdb.adminUsername | default "admin" }}
{{- end }}
{{- end }}

{{- define "snippet.couchdb.adminPassword" -}}
{{ if .Values.couchdb.enabled -}}
    {{ .Values.couchdb.adminPassword }}
  {{- else -}}
    {{ .Values.externalCouchdb.adminPassword }}
{{- end }}
{{- end }}

{{- define "snippet.influxdb.env" -}}
- name: SP_TS_STORAGE_BUCKET
  value: sp
- name: SP_TS_STORAGE_ORG
  value: sp
{{ if .Values.influxdb.enabled }}
- name: SP_TS_STORAGE_HOST
  value: {{ include "influxdb.fullname" . | quote }}
- name: SP_TS_STORAGE_PORT
  value: {{ .Values.influxdb.service.port | quote }}
{{- else -}}
- name: SP_TS_STORAGE_HOST
  value: {{ .Values.externalInfluxdb.host | quote }}
- name: SP_TS_STORAGE_PORT
  value: {{ .Values.externalInfluxdb.port | quote }}
{{- end }}
- name: SP_TS_STORAGE_TOKEN
{{ if and (not .Values.influxdb.enabled) .Values.externalInfluxdb.existingSecret }}
  valueFrom:
    secretKeyRef:
      name: {{ required "externalInfluxdb.existingSecret is required if not influxdb.enabled and not influxdb.admin" .Values.externalInfluxdb.existingSecret | quote }}
      key: {{ required "externalInfluxdb.adminTokenSecretKey is required if not influxdb.enabled not influxdb.admin" .Values.externalInfluxdb.adminTokenSecretKey }}
{{- else }}
  valueFrom:
    secretKeyRef:
      name: {{ include "snippet.streampipes.influxdb.secret.name" . }}
      key: {{ include "snippet.streampipes.influxdb.secret.key" . }}
{{- end }}
{{- end }}

{{- define "snippet.influxdb.secret.name" -}}
{{if .Values.influxdb.enabled}}
  {{ include "influxdb.secretName" . | quote }}
{{ else }}
  {{ include "streampipes.fullname" . }}-influxdb-secret
{{- end}}
{{- end }}

{{- define "snippet.influxdb.secret.key" -}}
{{if .Values.influxdb.enabled}}
  influxdb-admin-token
{{ else }}
  sp-influxdb-token
{{- end}}
{{- end }}

{{- define "snippet.kafka.env" -}}
- name: SP_KAFKA_HOST
{{- if not .Values.kafka.enabled }}
  value: {{ required "externalKafka.host is required if not kafka.enabled" .Values.externalKafka.host | quote }} 
{{- else -}}
  value: {{ .Values.kafka.service.clusterIP | quote }}
{{- end }}
- name: SP_KAFKA_PORT
{{- if not .Values.kafka.enabled }}
  value: {{ required "externalKafka.port is required if not kafka.enabled" .Values.externalKafka.port | quote }}
{{- else -}}
  value: {{ .Values.kafka.service.ports.client | quote }}
{{- end }}
{{- end }}

{{- define "snippet.streampipes.broker.env"}}
- name: SP_PRIORITIZED_PROTOCOL
{{- if eq .Values.streampipes.broker.type "kafka"}}
  value: "kafka"
{{ include "snippet.kafka.env" . }}
{{- end }}
{{- end }}

{{- define "snippet.monitoring.env" -}}
- name: SP_SETUP_PROMETHEUS_ENDPOINT
  value: "false"
{{- end }}

{{- define "snippet.streampipes.backend.env" -}}
{{ include "snippet.streampipes.env" . }}
{{ include "snippet.couchdb.env" . }}
{{ include "snippet.streampipes.broker.env" . }}
{{ include "snippet.monitoring.env" . }}
{{ include "snippet.influxdb.env" . }}
{{ include "snippet.streampipes.proxy.env" . }}
{{- end }}

{{- define "snippet.streampipes.ui.env" -}}
{{ include "snippet.streampipes.backend.env" . }}
{{ include "snippet.streampipes.core.env" . }}
{{ include "snippet.streampipes.proxy.env" . }}
{{- end }}

{{- define "snippet.streampipes.extensions.env" -}}
{{ include "snippet.streampipes.core.env" . }}
{{ include "snippet.couchdb.env" . }}
{{ include "snippet.streampipes.secret.client" . }}
{{ include "snippet.monitoring.env" . }}
{{ include "snippet.influxdb.env" . }}
{{ include "snippet.streampipes.proxy.env" . }}
{{- end }}

