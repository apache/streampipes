import * as angular from 'angular';

export class ContextController {

    RestApi: any;
    $mdDialog: any;
    Upload: any;
    contexts: any;
    addSelected: any;
    newContext: any;
    file: any;
    availableFormats: any;

    constructor($mdDialog, RestApi, Upload) {
        this.RestApi = RestApi;
        this.$mdDialog = $mdDialog;
        this.Upload = Upload;

        this.contexts = [];
        this.addSelected = false;
        this.newContext = {};
        this.file = {};

        this.availableFormats = ["RDFXML", "JSONLD", "TURTLE", "RDFA"];
    }

    $onInit() {
        this.getContexts();
    }

    getContexts() {
        this.RestApi.getAvailableContexts()
            .then(contexts => {
                this.contexts = contexts.data;
            });
    }

    deleteContext(contextId) {
        this.RestApi.deleteContext(contextId)
            .then(msg => {
                this.getContexts();
            });
    }

    showAddInput() {
        this.addSelected = true;
    }

    submit(file) {
        if (file) {
            file.upload = this.Upload.upload({
                url: '/semantic-epa-backend/api/v2/contexts',
                data: {file: file, 'context' : angular.toJson(this.newContext)}
            });

            file.upload.then(function (response) {
            }, function (response) {
                if (response.status > 0)
                    this.errorMsg = response.status + ': ' + response.data;
            }, function (evt) {
                file.progress = Math.min(100, parseInt((100.0 *
                    evt.loaded / evt.total)+''));
            });
        }
    }

    hide() {
        this.$mdDialog.hide();
    };

    cancel() {
        this.$mdDialog.cancel();
    };
}

ContextController.$inject = ['$mdDialog', 'RestApi', 'Upload'];
