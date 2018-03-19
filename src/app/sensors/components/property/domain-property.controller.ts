export class DomainPropertyController {

    domainProperties: any;

    constructor(DomainProperties) {
        this.domainProperties = DomainProperties.getDomainProperties();
    }

}

DomainPropertyController.$inject = ['DomainProperties'];