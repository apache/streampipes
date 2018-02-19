
export class PipelineDetailsCtrl {

    constructor(RestApi, $stateParams) {
        this.RestApi = RestApi;
        this.$stateParams = $stateParams;

        this.currentPipeline = $stateParams.pipeline;
        this.pipeline = {};

        this.selectedTab = "overview";
        this.selectedElement = "";

    }

    $onInit() {
        this.loadPipeline();
    }

    setSelectedTab(tabTitle) {
        this.selectedTab = tabTitle;
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

PipelineDetailsCtrl.$inject = ['RestApi', '$stateParams'];
