package StreamPipesHttp

import "streampipes-client-go/streampipes/internal/config"

type PutRequest struct {
	HttpRequest *HttpRequest
}

func NewPutRequest(clientConfig config.StreamPipesClientConnectionConfig) *PutRequest {
	httpRequest := NewHttpRequest(clientConfig)
	return &PutRequest{
		HttpRequest: httpRequest,
	}
}

func (p *PutRequest) ExecuteGetRequest() {
	//p.HttpRequest.ExecuteRequest("PUT")
}
