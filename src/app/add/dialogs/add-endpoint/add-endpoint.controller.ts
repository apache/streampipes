export class AddEndpointController {

    $mdDialog: any;
    RestApi: any;
    rdfEndpoints: any;
    addSelected: any;
    newEndpoint: any;
    getEndpointItems: any;

    constructor($mdDialog, RestApi, getEndpointItems) {
        this.$mdDialog = $mdDialog;
        this.RestApi = RestApi;
        this.rdfEndpoints = [];
        this.addSelected = false;
        this.newEndpoint = {};
        this.getEndpointItems = getEndpointItems;
    }

    $onInit() {
        this.loadRdfEndpoints();
    }

    showAddInput() {
        this.addSelected = true;
    }

    loadRdfEndpoints() {
        this.RestApi.getRdfEndpoints()
            .then(rdfEndpoints => {
                this.rdfEndpoints = rdfEndpoints.data;
            });
    }

    addRdfEndpoint(rdfEndpoint) {
        this.RestApi.addRdfEndpoint(rdfEndpoint)
            .then(message => {
                this.loadRdfEndpoints();
                this.getEndpointItems();
            });
    }

    removeRdfEndpoint(rdfEndpointId) {
        this.RestApi.removeRdfEndpoint(rdfEndpointId)
            .then(message => {
                this.loadRdfEndpoints();
                this.getEndpointItems();
            });
    }

    hide() {
        this.$mdDialog.hide();
    }

    cancel() {
        this.$mdDialog.cancel();
    };

}

AddEndpointController.$inject = ['$mdDialog', 'RestApi', 'getEndpointItems'];