package StreamPipesHttp

import (
	"io/ioutil"
	"log"
	"streampipes-client-go/streampipes/internal/config"
	"streampipes-client-go/streampipes/internal/serializer"
	"streampipes-client-go/streampipes/model/resource"
)

type GetRequest struct {
	HttpRequest *HttpRequest
	Serializer  *serializer.UnBaseSerializer
}

func NewGetRequest(clientConfig config.StreamPipesClientConnectionConfig, Serializer *serializer.UnBaseSerializer) *GetRequest {
	return &GetRequest{
		HttpRequest: NewHttpRequest(clientConfig),
		Serializer:  Serializer,
	}
}

func (g *GetRequest) ExecuteGetRequest(dataLakeMeasure *[]resource.DataLakeMeasure) {
	//Process complete GET requests
	g.HttpRequest.ExecuteRequest("GET", dataLakeMeasure)
	g.HttpRequest.AfterRequest = func(dataLakeMeasure *[]resource.DataLakeMeasure) {
		g.afterRequest(dataLakeMeasure)
	}
	g.HttpRequest.AfterRequest(dataLakeMeasure)
}

func (g *GetRequest) afterRequest(dataLakeMeasure *[]resource.DataLakeMeasure) {
	////Process complete GET requests
	defer g.HttpRequest.Response.Body.Close()
	body, err := ioutil.ReadAll(g.HttpRequest.Response.Body)
	if err != nil {
		log.Fatal(err)
	}
	err = g.Serializer.GetUnmarshal(body, dataLakeMeasure)
	if err != nil {
		log.Fatal("Serialization failed")
	}
}
