
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
