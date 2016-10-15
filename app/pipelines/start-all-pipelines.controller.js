StartAllPipelinesController.$inject = ['$scope', '$mdDialog', 'restApi', 'pipelines', 'action', 'activeCategory'];

export default function StartAllPipelinesController($scope, $mdDialog, restApi, pipelines, action, activeCategory) {

    $scope.pipelinesToModify = [];
    $scope.installationStatus = [];
    $scope.installationFinished = false;
    $scope.page = "preview";
    $scope.nextButton = "Next";
    $scope.installationRunning = false;

    $scope.hide = function () {
        $mdDialog.hide();
    };

    $scope.cancel = function () {
        $mdDialog.cancel();
    };

    $scope.next = function () {
        if ($scope.page == "installation") {
            $scope.cancel();
        } else {
            $scope.page = "installation";
            initiateInstallation($scope.pipelinesToModify[0], 0);
        }
    }

    var getPipelinesToModify = function () {
        angular.forEach(pipelines, function (pipeline) {
            if (pipeline.running != action && hasCategory(pipeline)) {
                $scope.pipelinesToModify.push(pipeline);
            }
        });
    }

    var hasCategory = function (pipeline) {
        var categoryPresent = false;
        if (activeCategory == "") return true;
        else {
            angular.forEach(pipeline.pipelineCategories, function (category) {
                if (category == activeCategory) {
                    categoryPresent = true;
                }
            });
            return categoryPresent;
        }
    }

    var initiateInstallation = function (pipeline, index) {
        $scope.installationRunning = true;
        $scope.installationStatus.push({"name": pipeline.name, "id": index, "status": "waiting"});
        if (action) {
            startPipeline(pipeline, index);
        } else {
            stopPipeline(pipeline, index);
        }
    }

    var startPipeline = function (pipeline, index) {
        restApi.startPipeline(pipeline._id)
            .success(function (data) {
                if (data.success) {
                    $scope.installationStatus[index].status = "success";
                } else {
                    $scope.installationStatus[index].status = "error";
                }
            })
            .error(function (data) {
                $scope.installationStatus[index].status = "error";
            })
            .then(function () {
                if (index < $scope.pipelinesToModify.length - 1) {
                    index++;
                    initiateInstallation($scope.pipelinesToModify[index], index);
                } else {
                    $scope.getPipelines();
                    $scope.nextButton = "Close";
                    $scope.installationRunning = false;
                }
            });
    }

    var stopPipeline = function (pipeline, index) {
        restApi.stopPipeline(pipeline._id)
            .success(function (data) {
                if (data.success) {
                    $scope.installationStatus[index].status = "success";
                } else {
                    $scope.installationStatus[index].status = "error";
                }
            })
            .error(function (data) {
                $scope.installationStatus[index].status = "error";
            })
            .then(function () {
                if (index < $scope.pipelinesToModify.length - 1) {
                    index++;
                    initiateInstallation($scope.pipelinesToModify[index], index);
                } else {
                    $scope.getPipelines();
                    $scope.nextButton = "Close";
                    $scope.installationRunning = false;
                }
            });
    }

    getPipelinesToModify();

    if ($scope.pipelinesToModify.length == 0) {
        $scope.nextButton = "Close";
        $scope.page = "installation";
    }
}