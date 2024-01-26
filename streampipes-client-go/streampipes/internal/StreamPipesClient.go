package internal

import (
	"errors"
	"streampipes-client-go/streampipes/internal/api"
	"streampipes-client-go/streampipes/internal/config"
)

// Client is a base client that is used to make StreamPipesHttp httpRequest to the ServiceURL

type StreamPipesClient struct {
	Config config.StreamPipesClientConnectionConfig
}

// 暂时不支持https
func NewStreamPipesClient(config config.StreamPipesClientConnectionConfig) (*StreamPipesClient, error) {
	if !config.HttpsDisabled || config.StreamPipesPort == "443" {
		return &StreamPipesClient{}, errors.New(
			"Invalid configuration passed! The given client configuration has " +
				"`https_disabled` set to `True` and `port` set to `443`.\n " +
				"If you want to connect to port 443, use `https_disabled=False` or " +
				"alternatively connect to port .")
	}

	return &StreamPipesClient{
		config,
	}, nil
}

func (s *StreamPipesClient) DataLakeMeasureApi() *api.DataLakeMeasureApi {
	return api.NewDataLakeMeasureApi(s.Config)
}
