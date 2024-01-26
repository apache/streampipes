package main

import (
	"fmt"
	"streampipes-client-go/streampipes/internal"
	"streampipes-client-go/streampipes/internal/config"
	"streampipes-client-go/streampipes/internal/credential"
)

func main() {
	Config := config.StreamPipesClientConnectionConfig{
		Credential: credential.StreamPipesApiKeyCredentials{
			Username: "admin@streampipes.apache.org",
			ApiKey:   "LNrsh8YrgEyQTzSKSGmaAXb1",
		},
		StreamPipesPort: "8088",
		StreamPipesHost: "localhost",
		HttpsDisabled:   true,
	}
	StreamPipesClient, err := internal.NewStreamPipesClient(Config)
	if err != nil {
		fmt.Println(err)
	}
	measure := StreamPipesClient.DataLakeMeasureApi().All()
	fmt.Println(measure)
	//for _, v := range measure {
	//	fmt.Printf("MeasureName:%s,TimestampDiled:%s,EventSchema:%s,EventProperties:%s,pipelineId:%s,pipelineName:%s,pipelineIsRunning:%s,"+
	//		"schemaVersion:%s,schemaUpdateStrategy:%s,elementId:%s,_rev:%s", v.MeasureName, v.TimestampField, v.EventSchema,
	//		v.PipelineId, v.PipelineName, v.PipelineIsRunning, v.SchemaVersion, v.SchemaUpdateStrategy, v.ElementId, v.Rev)
	//
	//	//fmt.Println(v.MeasureName, v.TimestampField, v.EventSchema.EventProperties,
	//	//	v.PipelineId, v.PipelineName, v.PipelineIsRunning, v.SchemaVersion, v.SchemaUpdateStrategy, v.ElementId, v.Rev)
	//}

}
