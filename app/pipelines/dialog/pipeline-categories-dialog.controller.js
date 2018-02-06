export class PipelineCategoriesDialogController {

    constructor($mdDialog, restApi)
    {
        this.$mdDialog = $mdDialog;
        this.restApi = restApi;
        this.newCategory = {};
        this.newCategory.categoryName = "";
        this.newCategory.categoryDescription = "";
        this.addSelected = false;
        this.addPipelineToCategorySelected = [];
        this.categoryDetailsVisible = [];
        this.selectedPipelineId = "";

        this.getPipelineCategories();

    }

    toggleCategoryDetailsVisibility(categoryId) {
        this.categoryDetailsVisible[categoryId] = !this.categoryDetailsVisible[categoryId];
    }


    addPipelineToCategory(pipelineCategory) {
        var pipeline = this.findPipeline(pipelineCategory.selectedPipelineId);
        if (pipeline.pipelineCategories == undefined) pipeline.pipelineCategories = [];
        pipeline.pipelineCategories.push(pipelineCategory._id);
        this.storeUpdatedPipeline(pipeline);
    }

    removePipelineFromCategory(pipeline, categoryId) {
        var index = pipeline.pipelineCategories.indexOf(categoryId);
        pipeline.pipelineCategories.splice(index, 1);
        this.storeUpdatedPipeline(pipeline);
    }

    storeUpdatedPipeline(pipeline) {
        this.restApi.updatePipeline(pipeline)
            .success(msg => {
                console.log(msg);
                this.refreshPipelines();
            })
            .error(msg => {
                console.log(msg);
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
        this.restApi.storePipelineCategory(this.newCategory)
            .success(data => {
                console.log(data);
                this.getPipelineCategories();
                this.addSelected = false;
            })
            .error(msg => {
                console.log(msg);
            });
    }

    getPipelineCategories() {
        this.restApi.getPipelineCategories()
            .success(pipelineCategories => {
                this.pipelineCategories = pipelineCategories;
            })
            .error(msg => {
                console.log(msg);
            });
    };

    showAddToCategoryInput(categoryId, show) {
        this.addPipelineToCategorySelected[categoryId] = show;
        this.categoryDetailsVisible[categoryId] = true;
    }

    deletePipelineCategory(pipelineId) {
        this.restApi.deletePipelineCategory(pipelineId)
            .success(data => {
                console.log(data);
                this.getPipelineCategories();
            })
            .error(msg => {
                console.log(msg);
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

PipelineCategoriesDialogController.$inject = ['$mdDialog', 'restApi'];