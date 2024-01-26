package internal

import "streampipes-client-go/streampipes/internal/credential"

type StreamPipesApiKeyCredentials struct {
	Username string
	ApiKey   string
}

func (s *StreamPipesApiKeyCredentials) NewStreamPipesApiKeyCredentials(username, apiKey string) credential.StreamPipesApiKeyCredentials {
	Credentials := credential.StreamPipesApiKeyCredentials{}
	Credentials.ApiKeyCredential(username, apiKey)
	return Credentials
}
