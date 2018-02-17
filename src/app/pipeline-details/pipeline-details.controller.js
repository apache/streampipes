
export class PipelineDetailsCtrl {

    constructor($scope, RestApi, $rootScope, $stateParams, PipelinePositioningService) {
        this.RestApi = RestApi;
        this.$rootScope = $rootScope;
        this.$scope = $scope;
        this.$stateParams = $stateParams;
        this.pipelinePositioningService = PipelinePositioningService;

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
        this.RestApi.getPipelineById(this.currentPipeline)
            .success(pipeline => {
                this.pipeline = pipeline;
            })
            .error(msg => {
                console.log(msg);
            });
    }

}

PipelineDetailsCtrl.$inject = ['$scope', 'RestApi', '$rootScope', '$stateParams', 'PipelinePositioningService'];
