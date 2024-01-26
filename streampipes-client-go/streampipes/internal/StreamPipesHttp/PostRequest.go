package StreamPipesHttp

import "streampipes-client-go/streampipes/internal/config"

type PostRequest struct {
	HttpRequest *HttpRequest
}

func NewPostRequest(clientConfig config.StreamPipesClientConnectionConfig) *PostRequest {
	httpRequest := NewHttpRequest(clientConfig)
	return &PostRequest{
		HttpRequest: httpRequest,
	}
}

func (p *PostRequest) ExecutePostRequest() {
	//p.HttpRequest.ExecuteRequest("POST")
}
