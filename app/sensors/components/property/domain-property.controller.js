export class DomainPropertyController {

    constructor(domainPropertiesService) {
        this.domainProperties = [];
        this.domainProperties = domainPropertiesService.getDomainProperties();
    }

}

DomainPropertyController.$inject = ['domainPropertiesService'];