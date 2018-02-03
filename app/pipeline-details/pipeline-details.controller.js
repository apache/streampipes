
export class PipelineDetailsCtrl {

    constructor($scope, restApi, $rootScope, $stateParams, pipelinePositioningService) {
        this.restApi = restApi;
        this.$rootScope = $rootScope;
        this.$scope = $scope;
        this.$stateParams = $stateParams;
        this.pipelinePositioningService = pipelinePositioningService;

        this.currentPipeline = $stateParams.pipeline;
        this.pipeline = {};

        this.selectedTab = "overview";
        this.selectedElement = "";

        this.loadPipeline();
    }

    setSelectedTab(tabTitle) {
        this.selectedTab = tabTitle;
    }

    updateSelected(selected) {
        this.selectedElement = selected;
        this.$scope.$apply();
    }

    loadPipeline() {
        this.restApi.getPipelineById(this.currentPipeline)
            .success(pipeline => {
                this.pipeline = pipeline;
            })
            .error(msg => {
                console.log(msg);
            });
    }

}

PipelineDetailsCtrl.$inject = ['$scope', 'restApi', '$rootScope', '$stateParams', 'pipelinePositioningService'];
