export class SavePipelineController {

    constructor($scope, $rootScope, $mdDialog, $state, RestApi, $mdToast, ObjectProvider, pipeline) {
        this.RestApi = RestApi;
        this.$mdToast = $mdToast;
        this.$state = $state;
        this.$mdDialog = $mdDialog;
        this.pipelineCategories = [];
        this.pipeline = pipeline;
        this.ObjectProvider = ObjectProvider;

        console.log("save pipeline");
        console.log(this.pipeline);

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
                    if (this.state.adjustingPipelineState && $scope.overwrite) {
                        var pipelineId = $rootScope.state.adjustingPipeline._id;

                        this.RestApi.deleteOwnPipeline(pipelineId)
                            .success(data => {
                                if (data.success) {
                                    $rootScope.state.adjustingPipelineState = false;
                                    $("#overwriteCheckbox").css("display", "none");
                                    refresh("Proa");
                                } else {
                                    this.displayErrors(data);
                                }
                            })
                            .error(data => {
                                this.showToast("error", "Could not delete Pipeline");
                                console.log(data);
                            })

                    }
                    this.clearAssembly();

                } else {
                    this.displayErrors(data);
                }
            })
            .error(function (data) {
                this.showToast("error", "Could not fulfill request", "Connection Error");
                console.log(data);
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

SavePipelineController.$inject = ['$scope', '$rootScope', '$mdDialog', '$state', 'RestApi', '$mdToast', 'ObjectProvider', 'pipeline'];
