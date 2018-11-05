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
        this.loadRdfEndpoints();
    }

    showAddInput() {
        this.addSelected = true;
    }

    loadRdfEndpoints() {
        this.RestApi.getRdfEndpoints()
            .success(rdfEndpoints => {
                this.rdfEndpoints = rdfEndpoints;
            })
            .error(error => {
                console.log(error);
            });
    }

    addRdfEndpoint(rdfEndpoint) {
        this.RestApi.addRdfEndpoint(rdfEndpoint)
            .success(message => {
                this.loadRdfEndpoints();
                this.getEndpointItems();
            })
            .error(error => {
                console.log(error);
            });
    }

    removeRdfEndpoint(rdfEndpointId) {
        this.RestApi.removeRdfEndpoint(rdfEndpointId)
            .success(message => {
                this.loadRdfEndpoints();
                this.getEndpointItems();
            })
            .error(error => {
                console.log(error);
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