package config

import (
	"streampipes-client-go/streampipes/internal/credential"
	Path "streampipes-client-go/streampipes/internal/streamPipesApiPath"
)

type ClientConnectionConfigResolver interface {
	GetStreamPipesHost() string
	GetStreamPipesPort() string
	IsHttpsDisabled() bool
	GetBaseUrl() string
	GetCredentials() credential.StreamPipesApiKeyCredentials
}

type StreamPipesClientConnectionConfig struct {
	Credential      credential.StreamPipesApiKeyCredentials //credential.StreamPipesApiKeyCredentials
	StreamPipesHost string
	StreamPipesPort string
	HttpsDisabled   bool
}

func (s *StreamPipesClientConnectionConfig) GetCredentials() credential.StreamPipesApiKeyCredentials {
	return s.Credential
}

func (s *StreamPipesClientConnectionConfig) GetStreamPipesHost() string {
	return s.StreamPipesHost
}

func (s *StreamPipesClientConnectionConfig) GetStreamPipesPort() string {
	return s.StreamPipesPort
}

func (s *StreamPipesClientConnectionConfig) IsHttpsDisabled() bool {
	return s.HttpsDisabled
}

func (s *StreamPipesClientConnectionConfig) GetBaseUrl() *Path.StreamPipesApiPath {
	protocol := "https://"
	if s.IsHttpsDisabled() {
		protocol = "http://"
	}

	protocol = protocol + s.StreamPipesHost + ":" + s.StreamPipesPort
	ApiPath := Path.NewStreamPipesApiPath([]string{protocol}).FromStreamPipesBasePath()
	ApiPath.ToString()

	return ApiPath
}
