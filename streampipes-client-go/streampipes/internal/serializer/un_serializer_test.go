//
// Licensed to the Apache Software Foundation (ASF) under one or more
// contributor license agreements.  See the NOTICE file distributed with
// this work for additional information regarding copyright ownership.
// The ASF licenses this file to You under the Apache License, Version 2.0
// (the "License"); you may not use this file except in compliance with
// the License.  You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package serializer

import (
	"reflect"
	"streampipes-client-go/streampipes/model"
	"streampipes-client-go/streampipes/model/data_lake"
	"testing"
)

func TestGetUnmarshal(t *testing.T) {

	Testdata1 := []byte(`[  
    {  
        "className": "classname1",  
        "measureName": "measurename1",  
        "timestampField": "timestampField1",  
        "eventSchema": {},  
        "pipelineId": "pipelineId1",  
        "pipelineName": "pipelineName1",  
        "pipelineIsRunning": false,  
        "schemaVersion": "schemaVersion1",  
        "elementId": "elementId1",  
        "_rev": "rev1"  
    }
]`)
	Testdata2 := []byte(`
	{
		"total" : 0,
		"headers" : [],
		"allDataSeries" : [],
		"spQueryStatus" :"OK",
		"forId" : "forId"
	}
`)
	Testdata := [][]byte{Testdata1, Testdata2}

	expectedMeasure := []data_lake.DataLakeMeasure{
		{
			ClassName:         "classname1",
			MeasureName:       "measurename1",
			TimestampField:    "timestampField1",
			EventSchema:       model.EventSchema{},
			PipelineId:        "pipelineId1",
			PipelineName:      "pipelineName1",
			PipelineIsRunning: false,
			SchemaVersion:     "schemaVersion1",
			ElementId:         "elementId1",
			Rev:               "rev1",
		},
	}
	expectedSeries := data_lake.DataSeries{
		Total:         0,
		Headers:       []string{},
		AllDataSeries: []model.DataSerie{},
		SPQueryStatus: "OK",
		ForId:         "forId",
	}

	serializerA := &UnBaseSerializer{
		UnSerializerDataLakeMeasures: new([]data_lake.DataLakeMeasure),
		UnSerializerDataLakeSeries:   nil,
	}
	serializerB := &UnBaseSerializer{
		UnSerializerDataLakeMeasures: nil,
		UnSerializerDataLakeSeries:   new(data_lake.DataSeries),
	}

	for k, v := range Testdata {
		if k == 0 {
			err := serializerA.GetUnmarshal(v)
			if err != nil {
				t.Errorf("Unexpected error: %v", err)
			}
			measureSlice := *serializerA.UnSerializerDataLakeMeasures
			if !reflect.DeepEqual(measureSlice, expectedMeasure) {
				t.Errorf("UnSerializerDataLakeMeasures does not match the expected value")
			}
		} else if k == 1 {
			err := serializerB.GetUnmarshal(v)
			if err != nil {
				t.Errorf("Unexpected error: %v", err)
			}
			series := *serializerB.UnSerializerDataLakeSeries
			if !reflect.DeepEqual(series, expectedSeries) {
				t.Errorf("UnSerializerDataLakeSeries does not match the expected value")
			}
		}
	}
}
