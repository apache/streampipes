export class DomainPropertyController {

    domainProperties: any;
    DomainPropertiesService: any;

    constructor(DomainProperties) {
        this.DomainPropertiesService = DomainProperties;
    }

    $onInit() {
        this.domainProperties = this.DomainPropertiesService.getDomainProperties();
    }

}

DomainPropertyController.$inject = ['DomainProperties'];