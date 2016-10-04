PipelineCategoriesDialogController.$inject = ['$scope', '$mdDialog', 'restApi'];

export default function PipelineCategoriesDialogController($scope, $mdDialog, restApi) {

    $scope.newCategory = {};
    $scope.newCategory.categoryName = "";
    $scope.newCategory.categoryDescription = "";
    $scope.addSelected = false;
    $scope.addPipelineToCategorySelected = [];
    $scope.categoryDetailsVisible = [];
    $scope.selectedPipelineId = "";

    $scope.toggleCategoryDetailsVisibility = function (categoryId) {
        $scope.categoryDetailsVisible[categoryId] = !$scope.categoryDetailsVisible[categoryId];
    }


    $scope.addPipelineToCategory = function (pipelineCategory) {

        var pipeline = findPipeline(pipelineCategory.selectedPipelineId);
        if (pipeline.pipelineCategories == undefined) pipeline.pipelineCategories = [];
        pipeline.pipelineCategories.push(pipelineCategory._id);
        $scope.storeUpdatedPipeline(pipeline);
    }

    $scope.removePipelineFromCategory = function (pipeline, categoryId) {
        var index = pipeline.pipelineCategories.indexOf(categoryId);
        pipeline.pipelineCategories.splice(index, 1);
        $scope.storeUpdatedPipeline(pipeline);
    }

    $scope.storeUpdatedPipeline = function (pipeline) {
        restApi.updatePipeline(pipeline)
            .success(function (msg) {
                console.log(msg);
                $scope.getPipelines();
            })
            .error(function (msg) {
                console.log(msg);
            });
    }

    var findPipeline = function (pipelineId) {
        var matchedPipeline = {};
        angular.forEach($scope.pipelines, function (pipeline) {
            console.log(pipeline._id);
            if (pipeline._id == pipelineId) matchedPipeline = pipeline;
        });
        return matchedPipeline;
    }

    $scope.addPipelineCategory = function () {
        restApi.storePipelineCategory($scope.newCategory)
            .success(function (data) {
                console.log(data);
                $scope.getPipelineCategories();
                $scope.addSelected = false;
            })
            .error(function (msg) {
                console.log(msg);
            });
    }

    $scope.showAddToCategoryInput = function (categoryId, show) {
        $scope.addPipelineToCategorySelected[categoryId] = show;
        $scope.categoryDetailsVisible[categoryId] = true;
    }

    $scope.deletePipelineCategory = function (pipelineId) {
        restApi.deletePipelineCategory(pipelineId)
            .success(function (data) {
                console.log(data);
                $scope.getPipelineCategories();
            })
            .error(function (msg) {
                console.log(msg);
            });
    }

    $scope.showAddInput = function () {
        $scope.addSelected = true;
        $scope.newCategory.categoryName = "";
        $scope.newCategory.categoryDescription = "";
    }

    $scope.hide = function () {
        $mdDialog.hide();
    };

    $scope.cancel = function () {
        $mdDialog.cancel();
    };
}