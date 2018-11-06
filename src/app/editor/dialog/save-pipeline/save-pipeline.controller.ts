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
        if (data.notifications.length > 0) {
            this.showToast("success", data.notifications[0].description, data.notifications[0].title);
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
                            .success(d => {
                                this.afterStorage(data, switchTab);
                            })
                            .error(d => {
                                this.showToast("error", "Could not delete Pipeline");
                            })
                    } else {
                        this.afterStorage(data, switchTab);
                    }
                } else {
                    this.displayErrors(data);
                }
            })
            .error(function (data) {
                this.showToast("error", "Could not fulfill request", "Connection Error");
            });
    };

    afterStorage(data, switchTab) {
        this.displaySuccess(data);
        this.hide();
        this.TransitionService.makePipelineAssemblyEmpty(true);
        if (this.ShepherdService.isTourActive()) {
            this.ShepherdService.hideCurrentStep();
        }
        if (switchTab && !this.startPipelineAfterStorage) {
            this.$state.go("streampipes.pipelines");
        }
        if (this.startPipelineAfterStorage) {
            this.$state.go("streampipes.pipelines", {pipeline: data.notifications[1].description});
        }
    }

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
