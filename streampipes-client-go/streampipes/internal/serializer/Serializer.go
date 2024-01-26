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
	"encoding/json"
	"reflect"
	"streampipes-client-go/streampipes/model/resource"
)

type UnBaseSerializer struct {
	UnSerializerDataLakeMeasure *[]resource.DataLakeMeasure
}
type BaseSerializer struct {
	SerializerDataLakeMeasure resource.DataLakeMeasure
}

func NewBaseSerializer() *UnBaseSerializer {
	return &UnBaseSerializer{
		UnSerializerDataLakeMeasure: new([]resource.DataLakeMeasure),
	}
}

func (u *UnBaseSerializer) GetUnmarshal(body []byte, Interface interface{}) error { //(resource.DataLakeMeasure,error) {
	//Obtain interface type through reflect
	V := reflect.ValueOf(u).Elem()
	for j := 0; j < V.NumField(); j++ {
		field := V.Field(j)
		if field.Type() == reflect.ValueOf(Interface).Type() {
			err := json.Unmarshal(body, field.Addr().Interface())
			return err
		}
	}
	return nil

	//Type Asserts
	//if _, ok := i.(*[]resource.DataLakeMeasure); ok {
	//	err := json.Unmarshal(body, &b.UnSerializerDataLakeMeasure)
	//	if err != nil {
	//		log.Print(err)
	//	}
	//} else if _, ok = i.(resource.DataLakeMeasure); ok {
	//	err := json.Unmarshal(body, &b.SerializerDataLakeMeasure)
	//	if err != nil {
	//		log.Print(err)
	//	}
	//}
	//if v.Kind() != reflect.Struct {
	//	fmt.Println("Unknown type")
	//	return nil
	//}

}

func (b *BaseSerializer) GetMarshal(Interface interface{}) {

}
