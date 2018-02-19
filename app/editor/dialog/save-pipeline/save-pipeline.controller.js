export class SavePipelineController {

    constructor($mdDialog, $state, RestApi, $mdToast, ObjectProvider, pipeline) {
        this.RestApi = RestApi;
        this.$mdToast = $mdToast;
        this.$state = $state;
        this.$mdDialog = $mdDialog;
        this.pipelineCategories = [];
        this.pipeline = pipeline;
        this.ObjectProvider = ObjectProvider;

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
                    this.displaySuccess(data);
                    this.hide();
                    if (switchTab) this.$state.go("streampipes.pipelines");
                    if (this.startPipelineAfterStorage) this.$state.go("streampipes.pipelines", {pipeline: data.notifications[1].description});
                    // TODO update pipelines properly
                    if (this.overwrite) {
                        //var pipelineId = $rootScope.state.adjustingPipeline._id;

                        this.RestApi.deleteOwnPipeline(pipelineId)
                            .success(data => {
                                if (data.success) {
                                    $("#overwriteCheckbox").css("display", "none");
                                } else {
                                    this.displayErrors(data);
                                }
                            })
                            .error(data => {
                                this.showToast("error", "Could not delete Pipeline");
                                console.log(data);
                            })

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

    showToast(type, title, description) {
        this.$mdToast.show(
            this.$mdToast.simple()
                .textContent(title)
                .position("top right")
                .hideDelay(3000)
        );
    }
}

SavePipelineController.$inject = ['$mdDialog', '$state', 'RestApi', '$mdToast', 'ObjectProvider', 'pipeline'];
