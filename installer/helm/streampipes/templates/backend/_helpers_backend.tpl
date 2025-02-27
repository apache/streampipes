{{/*
Maximum of 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "streampipes.backend.name" -}}
{{ include "streampipes.name" . | trunc 55 }}-backend
{{- end }}

{{- define "streampipes.backend.fullname" -}}
{{ include "streampipes.fullname" . | trunc 55 }}-backend
{{- end }}


{{/*
Backend common labels
*/}}
{{- define "streampipes.backend.labels" -}}
{{ include "streampipes.labels" . }}
app.kubernetes.io/component: backend
{{- end }}

{{/*
Backend selector labels
*/}}
{{- define "streampipes.backend.selectorLabels" -}}
{{ include "streampipes.selectorLabels" . }}
app.kubernetes.io/component: backend
{{- end }}


{{/*
Streampipes backend K8s Service
*/}}
{{- define "streampipes.backend.service" -}}
{{ printf "%s.%s.svc.cluster.local"  (include "streampipes.backend.fullname" .) .Release.Namespace | trimSuffix "-" }}
{{- end }}
