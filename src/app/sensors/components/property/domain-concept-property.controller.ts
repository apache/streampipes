export class DomainConceptPropertyController {

    RestApi: any;
    concepts: any;
    properties: any;

    constructor(RestApi) {
        this.RestApi = RestApi;
        this.concepts = [];
        this.properties = [];

        this.loadProperties();
        this.loadConcepts();
    }

    loadProperties() {
        this.RestApi.getOntologyProperties()
            .success(propertiesData => {
                this.properties = propertiesData;
            })
            .error(msg => {
                console.log(msg);
            });
    }

    loadConcepts() {
        this.RestApi.getOntologyConcepts()
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
        if (this.conceptRestricted(domainProperty)) domainProperty.requiredClass = undefined;
        else domainProperty.requiredClass = this.concepts[0].id;
    }

    conceptSelected(conceptId, currentConceptId) {
        return (conceptId == currentConceptId);
    }

    isSelectedProperty(availableProperty, selectedProperty) {
        return (availableProperty == selectedProperty);
    }

}

DomainConceptPropertyController.$inject = ['RestApi'];