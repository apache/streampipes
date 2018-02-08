export class DomainPropertyController {

    constructor(DomainProperties) {
        this.domainProperties = DomainProperties.getDomainProperties();
    }

}

DomainPropertyController.$inject = ['DomainProperties'];