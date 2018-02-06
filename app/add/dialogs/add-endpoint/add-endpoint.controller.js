export class AddEndpointController {

    constructor($mdDialog, restApi) {
        this.$mdDialog = $mdDialog;
        this.restApi = restApi;
        this.rdfEndpoints = [];
        this.addSelected = false;
        this.newEndpoint = {};
        this.loadRdfEndpoints();
    }

    showAddInput() {
        this.addSelected = true;
    }

    loadRdfEndpoints() {
        this.restApi.getRdfEndpoints()
            .success(rdfEndpoints => {
                this.rdfEndpoints = rdfEndpoints;
            })
            .error(error => {
                console.log(error);
            });
    }

    addRdfEndpoint(rdfEndpoint) {
        this.restApi.addRdfEndpoint(rdfEndpoint)
            .success(message => {
                this.loadRdfEndpoints();
                this.getEndpointItems();
            })
            .error(error => {
                console.log(error);
            });
    }

    removeRdfEndpoint(rdfEndpointId) {
        this.restApi.removeRdfEndpoint(rdfEndpointId)
            .success(message => {
                this.loadRdfEndpoints();
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

AddEndpointController.$inject = ['$mdDialog', 'restApi'];