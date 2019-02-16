export class AddDialogController {

    RestApi: any;
    $mdDialog: any;
    loadConcepts: any;
    loadConceptDetails: any;
    loadProperties: any;
    loadPropertyDetails: any;
    loadInstanceDetails: any;
    elementData: any;
    elementType: any;
    conceptId: any;
    namespaces: any;

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
    }

    $onInit() {
        this.getNamespaces();
    }


    getNamespaces() {
        this.RestApi.getOntologyNamespaces()
            .then(namespaces => {
                this.namespaces = namespaces.data;
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
                .then(msg => {
                    this.loadProperties();
                    this.loadPropertyDetails(this.elementData.namespace + this.elementData.elementName);
                });
        }
        else if (this.elementType === 'Concept')
        {
            this.RestApi.addOntologyConcept(this.elementData)
                .then(msg => {
                    this.loadConcepts();
                    this.loadConceptDetails(this.elementData.namespace + this.elementData.elementName);
                });
        }
        else
        {
            // parent: this,
            if (this.conceptId != undefined) this.elementData.instanceOf = this.conceptId;
            this.elementData.id = this.elementData.namespace + this.elementData.elementName
            this.RestApi.addOntologyInstance(this.elementData).then(msg => {
                this.loadConcepts();
                if (this.conceptId != undefined) this.loadConceptDetails(this.conceptId);
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
