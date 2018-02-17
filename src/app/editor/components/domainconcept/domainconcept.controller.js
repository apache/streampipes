export class DomainConceptController {

    constructor(RestApi) {
        this.RestApi = RestApi;
        
        this.availableDomainProperties = {};

        this.loadDomainConcepts(this.autoCompleteStaticProperty);
    }


    loadDomainConcepts(item) {
        var query = {};
        query.requiredClass = item.properties.requiredClass;
        query.requiredProperties = [];
        angular.forEach(item.properties.supportedProperties, function (p) {
            var propertyObj = {};
            propertyObj.propertyId = p.propertyId;
            query.requiredProperties.push(propertyObj);
        });

        this.RestApi.getDomainKnowledgeItems(query)
            .success(queryResponse => {
                if (!this.availableDomainProperties[item.elementId]) {
                    this.availableDomainProperties[item.elementId] = {};
                }
                angular.forEach(queryResponse.requiredProperties, resp => {
                    angular.forEach(resp.queryResponse, instanceResult => {
                        if (!this.availableDomainProperties[item.elementId][resp.propertyId])
                            this.availableDomainProperties[item.elementId][resp.propertyId] = [];
                        var instanceData = {
                            label: instanceResult.label,
                            description: instanceResult.description,
                            propertyValue: instanceResult.propertyValue
                        };
                        this.availableDomainProperties[item.elementId][resp.propertyId].push(instanceData);
                    });
                });
            })
            .error(function (msg) {
                console.log(msg);
            });
    }

    querySearch(query, staticPropertyId) {
        var result = [];
        var i = 0;
        angular.forEach(this.availableDomainProperties[staticPropertyId], function (values) {
            if (values.length > 0 && i == 0) {
                var position = 0;
                angular.forEach(values, value => {
                    if (query == undefined || value.label.substring(0, query.length) === query) result.push({
                        label: value.label,
                        description: value.description,
                        position: position
                    });
                    position++;
                })
                i++;
            }
        });
        return result;
    }

    searchTextChange(text) {

    }

    selectedItemChange(item, staticPropertyId, supportedProperties) {
        angular.forEach(supportedProperties, supportedProperty => {
            supportedProperty.value = this.availableDomainProperties[staticPropertyId][supportedProperty.propertyId][item.position].propertyValue;
        });
    }

}