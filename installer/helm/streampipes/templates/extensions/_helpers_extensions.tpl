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