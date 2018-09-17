export class SavePipelineController {

    RestApi: any;
    $mdToast: any;
    $state: any;
    $mdDialog: any;
    pipelineCategories: any;
    pipeline: any;
    ObjectProvider: any;
    startPipelineAfterStorage: any;
    modificationMode: any;
    updateMode: any;
    submitPipelineForm: any;
    TransitionService: any;
    ShepherdService: any;

    constructor($mdDialog,
                $state,
                RestApi,
                $mdToast,
                ObjectProvider,
                pipeline,
                modificationMode,
                TransitionService,
                ShepherdService) {
        this.RestApi = RestApi;
        this.$mdToast = $mdToast;
        this.$state = $state;
        this.$mdDialog = $mdDialog;
        this.pipelineCategories = [];
        this.pipeline = pipeline;
        this.ObjectProvider = ObjectProvider;
        this.modificationMode = modificationMode;
        this.updateMode = "update";
        this.TransitionService = TransitionService;
        this.ShepherdService = ShepherdService;

        this.getPipelineCategories();
    }


    displayErrors(data) {
        for (var i = 0, notification; notification = data.notifications[i]; i++) {
            this.showToast("error", notification.description, notification.title);
        }
    }

    displaySuccess(data) {
        for (var i = 0, notification; notification = data.notifications[i]; i++) {
            this.showToast("success", notification.description, notification.title);
        }
    }

    getPipelineCategories() {
        this.RestApi.getPipelineCategories()
            .success(pipelineCategories => {
                this.pipelineCategories = pipelineCategories;
            })
            .error(msg => {
                console.log(msg);
            });

    };


    savePipelineName(switchTab) {
        if (this.pipeline.name == "") {
            this.showToast("error", "Please enter a name for your pipeline");
            return false;
        }
        
        this.ObjectProvider.storePipeline(this.pipeline)
            .success(data => {
                if (data.success) {
                    if (this.modificationMode && this.updateMode === 'update') {
                        this.RestApi.deleteOwnPipeline(this.pipeline._id)
                            .success(data => {
                            })
                            .error(data => {
                                this.showToast("error", "Could not delete Pipeline");
                            })
                    }
                    this.displaySuccess(data);
                    this.hide();
                    this.TransitionService.makePipelineAssemblyEmpty(true);
                    this.ShepherdService.hideCurrentStep();
                    if (switchTab && !this.startPipelineAfterStorage) {
                        this.$state.go("streampipes.pipelines");
                    }
                    if (this.startPipelineAfterStorage) {
                        this.$state.go("streampipes.pipelines", {pipeline: data.notifications[1].description});
                    }
                    // TODO clear assembly
                    //this.clearAssembly();

                } else {
                    this.displayErrors(data);
                }
            })
            .error(function (data) {
                this.showToast("error", "Could not fulfill request", "Connection Error");
            });

    };

    hide() {
        this.$mdDialog.hide();
    };

    showToast(type, title, description?) {
        this.$mdToast.show(
            this.$mdToast.simple()
                .textContent(title)
                .position("top right")
                .hideDelay(3000)
        );
    }
}

SavePipelineController.$inject = ['$mdDialog', '$state', 'RestApi', '$mdToast', 'ObjectProvider', 'pipeline', 'modificationMode', 'TransitionService', 'ShepherdService'];
