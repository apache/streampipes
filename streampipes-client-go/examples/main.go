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

package main

import (
	"fmt"
	"github.com/apache/streampipes/streampipes-client-go/streampipes"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/config"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/credential"
)

/*
	Here are some examples of using go client, including outputting the data returned by streams.
	Only supports outputting model data
*/

func main() {
	Config := config.StreamPipesClientConnectionConfig{
		Url: "http://localhost:8030",
		Credential: credential.StreamPipesApiKeyCredentials{
			UserName: "<Your-User-Name>",
			ApiKey:   "<Your-API-Key>",
		},
	}
	StreamPipesClient, err := streampipes.NewStreamPipesClient(Config)
	if err != nil {
		fmt.Println(err)
	}
	StreamPipesClient.DataLakeMeasures().GetSingleDataSeries("measureId").Conversion()

	/*
			output format:

			There are 2 pieces of DataSerie in the Dataseries
			The 1 DataSeries
			time                   msg                   test
			2024-02-23T13:37:09.052Z   go-client_test   2f4556
			2024-02-23T13:37:26.044Z   go-client_test   2f4556
			2024-02-23T13:37:29.007Z   go-client_test   2f4556
			The 2 DataSeries
		    time                   msg                   test
			2024-02-23T13:37:09.052Z   go-client_test   2f4556
			2024-02-23T13:37:26.044Z   go-client_test   2f4556
			2024-02-23T13:37:29.007Z   go-client_test   2f4556

	*/

}
