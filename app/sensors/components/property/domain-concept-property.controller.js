export class DomainConceptPropertyController {

    constructor(restApi) {
        this.restApi = restApi;
        this.concepts = [];
        this.properties = [];

        this.loadProperties();
        this.loadConcepts();
    }

    loadProperties() {
        this.restApi.getOntologyProperties()
            .success(propertiesData => {
                this.properties = propertiesData;
            })
            .error(msg => {
                console.log(msg);
            });
    }

    loadConcepts() {
        this.restApi.getOntologyConcepts()
            .success(conceptsData => {
                this.concepts = conceptsData;
            })
            .error(msg => {
                console.log(msg);
            });
    }

    addSupportedProperty(supportedProperties) {
        if (supportedProperties == undefined) supportedProperties = [];
        supportedProperties.push({"propertyId": ""});
    }

    removeSupportedProperty(supportedProperties, index) {
        supportedProperties.splice(index, 1);
    }

    conceptRestricted(domainProperty) {
        return (domainProperty.requiredClass != undefined);
    }

    toggleConceptRestriction(domainProperty) {
        if ($scope.conceptRestricted(domainProperty)) domainProperty.requiredClass = undefined;
        else domainProperty.requiredClass = $scope.concepts[0].id;
    }

    conceptSelected(conceptId, currentConceptId) {
        return (conceptId == currentConceptId);
    }

    isSelectedProperty(availableProperty, selectedProperty) {
        return (availableProperty == selectedProperty);
    }

}

DomainConceptPropertyController.$inject = ['restApi'];