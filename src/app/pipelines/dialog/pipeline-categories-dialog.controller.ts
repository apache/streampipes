import * as angular from 'angular';

export class PipelineCategoriesDialogController {

    $mdDialog: any;
    RestApi: any;
    newCategory: any;
    addSelected: any;
    addPipelineToCategorySelected: any;
    categoryDetailsVisible: any;
    selectedPipelineId: any;
    pipelineCategories: any;
    pipelines: any;
    refreshPipelines: any;
    getPipelineCategories;

    constructor($mdDialog, RestApi, getPipelineCategories, refreshPipelines)
    {
        this.$mdDialog = $mdDialog;
        this.RestApi = RestApi;
        this.newCategory = {};
        this.newCategory.categoryName = "";
        this.newCategory.categoryDescription = "";
        this.addSelected = false;
        this.addPipelineToCategorySelected = [];
        this.categoryDetailsVisible = [];
        this.selectedPipelineId = "";
        this.getPipelineCategories = getPipelineCategories;
        this.refreshPipelines = refreshPipelines;

        this.fetchPipelineCategories();

    }

    toggleCategoryDetailsVisibility(categoryId) {
        this.categoryDetailsVisible[categoryId] = !this.categoryDetailsVisible[categoryId];
    }


    addPipelineToCategory(pipelineCategory) {
        var pipeline = this.findPipeline(pipelineCategory.selectedPipelineId);
        if (pipeline['pipelineCategories'] == undefined) pipeline['pipelineCategories'] = [];
        pipeline['pipelineCategories'].push(pipelineCategory._id);
        this.storeUpdatedPipeline(pipeline);
    }

    removePipelineFromCategory(pipeline, categoryId) {
        var index = pipeline.pipelineCategories.indexOf(categoryId);
        pipeline.pipelineCategories.splice(index, 1);
        this.storeUpdatedPipeline(pipeline);
    }

    storeUpdatedPipeline(pipeline) {
        this.RestApi.updatePipeline(pipeline)
            .then(msg => {
                this.refreshPipelines();
                this.getPipelineCategories();
                this.fetchPipelineCategories();
            });
    }

    findPipeline(pipelineId) {
        var matchedPipeline = {};
        angular.forEach(this.pipelines, function (pipeline) {
            if (pipeline._id == pipelineId) {
                matchedPipeline = pipeline;
            }
        });
        return matchedPipeline;
    }

    addPipelineCategory() {
        this.RestApi.storePipelineCategory(this.newCategory)
            .then(data => {
                this.fetchPipelineCategories();
                this.getPipelineCategories();
                this.addSelected = false;
            });
    }

    fetchPipelineCategories() {
        this.RestApi.getPipelineCategories()
            .then(pipelineCategories => {
                this.pipelineCategories = pipelineCategories.data;
            });
    };

    showAddToCategoryInput(categoryId, show) {
        this.addPipelineToCategorySelected[categoryId] = show;
        this.categoryDetailsVisible[categoryId] = true;
    }

    deletePipelineCategory(pipelineId) {
        this.RestApi.deletePipelineCategory(pipelineId)
            .then(data => {
                this.fetchPipelineCategories();
                this.getPipelineCategories();
            });
    }

    showAddInput() {
        this.addSelected = true;
        this.newCategory.categoryName = "";
        this.newCategory.categoryDescription = "";
    }

    hide() {
        this.$mdDialog.hide();
    }

    cancel() {
        this.$mdDialog.cancel();
    }
}

PipelineCategoriesDialogController.$inject = ['$mdDialog', 'RestApi', 'getPipelineCategories', 'refreshPipelines'];