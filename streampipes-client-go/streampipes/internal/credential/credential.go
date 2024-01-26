package credential

import (
	"net/http"
	"streampipes-client-go/streampipes/internal/StreamPipesHttp/headers"
)

//type CredentialsProvider interface {
//	MakeHeaders() []StreamPipesHttp.Header
//}

type StreamPipesApiKeyCredentials struct {
	Username string
	ApiKey   string
}

func (s *StreamPipesApiKeyCredentials) ApiKeyCredential(username, apikey string) {
	s.Username = username
	s.ApiKey = apikey
}

func (s *StreamPipesApiKeyCredentials) GetUsername() string {
	return s.Username
}
func (s *StreamPipesApiKeyCredentials) GetApiKey() string {
	return s.ApiKey
}

func (s *StreamPipesApiKeyCredentials) MakeHeaders(header *headers.Headers) (Rheader []http.Header) {
	XApiUser := header.XApiUser(s.Username)
	XApiKey := header.XApiKey(s.ApiKey)
	Rheader = append(append(Rheader, XApiUser), XApiKey)
	return Rheader
}
