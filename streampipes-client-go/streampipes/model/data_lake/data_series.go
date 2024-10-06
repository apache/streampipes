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

package data_lake

import (
	"fmt"
	"log"
	"strings"

	"github.com/apache/streampipes/streampipes-client-go/streampipes/model/common"
)

type DataSeries struct {
	Total         int                 `json:"total"`
	Headers       []string            `json:"http_headers"`
	AllDataSeries []common.DataSeries `json:"allDataSeries"`
	SPQueryStatus string              `alias:"spQueryStatus" default:"OK"`
	ForId         interface{}         `json:"forId"`
}

func (d *DataSeries) Print() {
	if d.Total == 0 {
		log.Println("This DataSeries has no data")
		return
	}
	fmt.Printf("There are %d pieces of DataSerie in the Dataseries\n", d.Total)
	for i := 0; i < d.Total; i++ {
		fmt.Printf("The %d DataSeries\n", i+1)
		headers := strings.Join(d.Headers, "                   ")
		fmt.Printf("%s\n", headers)
		for j := 0; j < d.AllDataSeries[i].Total; j++ {
			singleLine := strings.Join(d.AllDataSeries[i].Rows[j], "   ")
			fmt.Printf("%s\n",
				singleLine,
			)
		}
	}
}
