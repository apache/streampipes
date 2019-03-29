import {RestApi} from "../../services/rest-api.service";

export class ImportPipelineDialogController {

    $mdDialog: any;
    PipelineOperationsService: any;

    refreshPipelines: any;
    RestApi: RestApi;

    isInProgress: any = false;
    currentStatus: any;
    page = "upload-pipelines";

    availablePipelines: any[];
    selectedPipelines: any[];

    importing: boolean = false;
    $q: any;

    pages = [{
        type: "upload-pipelines",
        title: "Upload",
        description: "Upload a json file containing the pipelines to import"
    }, {
        type: "select-pipelines",
        title: "Select pipelines",
        description: "Select the pipelines to import"
    }, {
        type: "import-pipelines",
        title: "Import",
        description: ""
    }];

    constructor($mdDialog, RestApi: RestApi, refreshPipelines, $q) {
        this.$mdDialog = $mdDialog;
        this.RestApi = RestApi;
        this.refreshPipelines = refreshPipelines;
        this.$q = $q;
    }

    $onInit() {


    }

    upload(file) {
        var aReader = new FileReader();
        aReader.readAsText(file, "UTF-8");
        aReader.onload = evt => {
            this.availablePipelines = JSON.parse(aReader.result as string);
            this.page = "select-pipelines";
        }
    };

    hide() {
        this.$mdDialog.hide();
    };

    cancel() {
        this.$mdDialog.cancel();
    };

    back() {
        if (this.page == 'select-pipelines') {
            this.page = 'upload-pipelines';
        } else if (this.page == 'import-pipelines') {
            this.page = 'select-pipelines';
        }
    }

    storePipelines() {
        var promises = [];
        this.selectedPipelines.forEach(pipeline => {
            pipeline._rev = undefined;
            pipeline._id = undefined;
            promises.push(this.RestApi.storePipeline(pipeline));
        });
        this.$q.all(promises).then(result => {
            this.importing = false;
            this.refreshPipelines();
            this.hide();
        });
    }

    startImport() {
        this.page = 'import-pipelines';
        this.selectedPipelines = this.availablePipelines.filter(p => p.selectedForUpload);
        this.importing = true;
        this.storePipelines();
    }

}