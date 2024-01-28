package serializer

import (
	"encoding/json"
	"fmt"
	"reflect"
	"streampipes-client-go/streampipes/model/DataLake"
)

//type UnBaseSerializer struct {
//	UnSerializerDataLakeMeasures *[]DataLake.DataLakeMeasure
//}

//func NewBaseUnSerializer() *UnBaseSerializer {
//	return &UnBaseSerializer{
//		UnSerializerDataLakeMeasures: new([]DataLake.DataLakeMeasure),
//	}
//}

type UnBaseSerializer struct {
	UnSerializerDataLakeMeasures *[]DataLake.DataLakeMeasure
	UnSerializerDataLakeSeries   *DataLake.DataSeries
}

type Option func(opts *UnBaseSerializer)

func NewBaseUnSerializer(opts ...Option) *UnBaseSerializer {
	unBaseSerializer := UnBaseSerializer{}
	for _, opt := range opts {
		opt(&unBaseSerializer)
	}
	return &unBaseSerializer
}

func WithUnSerializerDataLakeMeasures(DataLakeMeasures *[]DataLake.DataLakeMeasure) Option {
	return func(opts *UnBaseSerializer) {
		opts.UnSerializerDataLakeMeasures = DataLakeMeasures
	}
}

func WithUnSerializerDataSeries(DataSeries *DataLake.DataSeries) Option {
	return func(opts *UnBaseSerializer) {
		opts.UnSerializerDataLakeSeries = DataSeries
	}
}

func (u *UnBaseSerializer) GetUnmarshal(body []byte, Interface interface{}) error { //(DataLake.DataLakeMeasure,error) {
	//Obtain interface type through reflect
	Type := reflect.TypeOf(Interface)
	Value := reflect.ValueOf(u).Elem()
	for i := 0; i < Value.NumField(); i++ {
		field := Value.Field(i)
		if field.Type() == Type {
			err := json.Unmarshal(body, field.Interface())
			if err != nil {
				fmt.Println(err)
				return err
			}
			return nil
		}
	}
	return nil
}
