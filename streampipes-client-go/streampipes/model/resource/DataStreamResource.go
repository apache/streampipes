package resource

import "streampipes-client-go/streampipes/model"

type DataStream struct {
	ClassName              string                        `json:"@class"`
	ElementId              string                        `json:"elementId"`
	Name                   string                        `json:"name"`
	Description            string                        `json:"description,omitempty"`
	IconUrl                string                        `json:"iconUrl,omitempty"`
	AppId                  string                        `json:"appId,omitempty"`
	IncludesAssets         bool                          `json:"includesAssets"`
	IncludesLocales        bool                          `json:"includesLocales"`
	IncludedAssets         []string                      `json:"includedAssets,omitempty"`
	IncludedLocales        []string                      `json:"includedLocales,omitempty"`
	ApplicationLinks       []model.ApplicationLink       `json:"applicationLinks,omitempty"`
	InternallyManaged      bool                          `json:"internallyManaged"`
	ConnectedTo            []string                      `json:"connectedTo,omitempty"`
	EventGrounding         model.EventGrounding          `json:"eventGrounding"`
	EventSchema            model.EventSchema             `json:"eventSchema,omitempty"`
	MeasurementCapability  []model.MeasurementCapability `json:"measurementCapability,omitempty"`
	MeasurementObject      []model.MeasurementObject     `json:"measurementObject,omitempty"`
	Index                  int                           `json:"index"`
	CorrespondingAdapterId string                        `json:"correspondingAdapterId,omitempty"`
	Category               []string                      `json:"category,omitempty"`
	URI                    string                        `json:"uri,omitempty"`
	Dom                    string                        `json:"dom,omitempty"`
	Rev                    string                        `json:"_rev,omitempty"`
}
