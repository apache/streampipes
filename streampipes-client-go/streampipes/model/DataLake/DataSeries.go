package DataLake

import "streampipes-client-go/streampipes/model"

type DataSeries struct {
	Total         int `json:"total"`
	Headers       []string
	AllDataSeries []model.DataSerie `json:"allDataSeries"`
	SPQueryStatus string            `alias:"spQueryStatus" default:"OK"`
	ForId         string            `json:"forId"`
}
