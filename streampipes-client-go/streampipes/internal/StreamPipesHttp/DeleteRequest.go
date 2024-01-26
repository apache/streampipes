package StreamPipesHttp

import "streampipes-client-go/streampipes/internal/config"

type DeleteRequest struct {
	HttpRequest *HttpRequest
}

func NewDeleteRequest(clientConfig config.StreamPipesClientConnectionConfig) *DeleteRequest {
	httpRequest := NewHttpRequest(clientConfig)
	return &DeleteRequest{
		HttpRequest: httpRequest,
	}
}

func (d *DeleteRequest) ExecuteDeleteRequest() {
	//d.HttpRequest.ExecuteRequest("Delete")
}
