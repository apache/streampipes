

{{/*
Maximum of 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "streampipes.extensions.iiot.name" -}}
{{ include "streampipes.name" . | trunc 55 }}-extensions
{{- end }}

{{- define "streampipes.extensions.iiot.fullname" -}}
{{ include "streampipes.fullname" . | trunc 55 }}-extensions
{{- end }}


{{/*
Backend common labels
*/}}
{{- define "streampipes.extensions.iiot.labels" -}}
{{ include "streampipes.labels" . }}
app.kubernetes.io/component: extensions
{{- end }}

{{/*
Backend selector labels
*/}}
{{- define "streampipes.extensions.iiot.selectorLabels" -}}
{{ include "streampipes.selectorLabels" . }}
app.kubernetes.io/component: extensions
{{- end }}


{{/*
Streampipes backend K8s Service
*/}}
{{- define "streampipes.extensions.iiot.service" -}}
{{ printf "%s.%s.svc.cluster.local"  (include "streampipes.extensions.iiot.fullname" .) .Release.Namespace | trimSuffix "-" }}
{{- end }}
