export class AddDialogController {

    constructor($mdDialog, RestApi, elementType, conceptId, loadConcepts, loadConceptDetails, loadProperties,
                loadPropertyDetails, loadInstanceDetails) {
        this.RestApi = RestApi;
        this.$mdDialog = $mdDialog;

        this.loadConcepts = loadConcepts;
        this.loadConceptDetails = loadConceptDetails;
        this.loadProperties = loadProperties;
        this.loadPropertyDetails = loadPropertyDetails;
        this.loadInstanceDetails = loadInstanceDetails;

        this.elementData = {};
        this.elementData.namespace = "";
        this.elementData.id = "";
        this.elementData.elementName = "";
        this.elementType = elementType;
        this.conceptId = conceptId;
        this.namespaces = [];

        this.getNamespaces();
    }


    getNamespaces() {
        this.RestApi.getOntologyNamespaces()
            .success(namespaces => {
                this.namespaces = namespaces;
            })
            .error(msg => {
                console.log(msg);
            });
    }

    add() {
        var promise;
        if (this.elementType === 'Property')
        {
            this.RestApi.addOntologyProperty(this.elementData)
                .success(msg => {
                    this.loadProperties();
                    this.loadPropertyDetails(this.elementData.namespace + this.elementData.elementName);
                });
        }
        else if (this.elementType === 'Concept')
        {
            this.RestApi.addOntologyConcept(this.elementData)
                .success(msg => {
                    this.loadConcepts();
                    this.loadConceptDetails(this.elementData.namespace + this.elementData.elementName);
                });
        }
        else
        {
            // parent: this,
            if (this.conceptId != undefined) this.elementData.instanceOf = conceptId;
            this.elementData.id = this.elementData.namespace + this.elementData.elementName
            this.RestApi.addOntologyInstance(this.elementData).success(msg => {
                this.loadConcepts();
                if (this.conceptId != undefined) this.loadConceptDetails(conceptId);
                this.loadInstanceDetails(this.elementData.namespace + this.elementData.elementName);
            });
        }

        this.hide();
    };

    hide() {
        this.$mdDialog.hide();
    };

    cancel() {
        this.$mdDialog.cancel();
    };


}

AddDialogController.$inject = ['$mdDialog', 'RestApi', 'elementType', 'conceptId', 'loadConcepts', 'loadConceptDetails',
'loadProperties', 'loadPropertyDetails', 'loadInstanceDetails'];
