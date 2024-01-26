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
