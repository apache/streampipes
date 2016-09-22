SavePipelineController.$inject = ['$scope', '$rootScope', '$mdDialog', '$state', 'restApi', '$mdToast'];

export default function SavePipelineController($scope, $rootScope, $mdDialog, $state, restApi, $mdToast) {

    $scope.pipelineCategories = [];

    $scope.displayErrors = function (data) {
        for (var i = 0, notification; notification = data.notifications[i]; i++) {
            showToast("error", notification.description, notification.title);
        }
    }

    $scope.displaySuccess = function (data) {
        for (var i = 0, notification; notification = data.notifications[i]; i++) {
            showToast("success", notification.description, notification.title);
        }
    }

    $scope.getPipelineCategories = function () {
        restApi.getPipelineCategories()
            .success(function (pipelineCategories) {
                $scope.pipelineCategories = pipelineCategories;
            })
            .error(function (msg) {
                console.log(msg);
            });

    };
    $scope.getPipelineCategories();

    $scope.savePipelineName = function (switchTab) {

        if ($rootScope.state.currentPipeline.name == "") {
            showToast("error", "Please enter a name for your pipeline");
            return false;
        }

        var overWrite;

        if (!($("#overwriteCheckbox").css('display') == 'none')) {
            overWrite = $("#overwriteCheckbox").prop("checked");
        } else {
            overWrite = false;
        }
        $rootScope.state.currentPipeline.send()
            .success(function (data) {
                if (data.success) {
                    $scope.displaySuccess(data);
                    $scope.hide();
                    if (switchTab) $state.go("streampipes.pipelines");
                    if ($scope.startPipelineAfterStorage) $state.go("streampipes.pipelines", {pipeline: data.notifications[1].description});
                    if ($rootScope.state.adjustingPipelineState && overWrite) {
                        var pipelineId = $rootScope.state.adjustingPipeline._id;

                        restApi.deleteOwnPipeline(pipelineId)
                            .success(function (data) {
                                if (data.success) {
                                    $rootScope.state.adjustingPipelineState = false;
                                    $("#overwriteCheckbox").css("display", "none");
                                    refresh("Proa");
                                } else {
                                    displayErrors(data);
                                }
                            })
                            .error(function (data) {
                                showToast("error", "Could not delete Pipeline");
                                console.log(data);
                            })

                    }
                    $scope.clearAssembly();

                } else {
                    $scope.displayErrors(data);
                }
            })
            .error(function (data) {
                showToast("error", "Could not fulfill request", "Connection Error");
                console.log(data);
            });

    };

    $scope.hide = function () {
        $mdDialog.hide();
    };

    function showToast(type, title, description) {
        $mdToast.show(
            $mdToast.simple()
                .textContent(title)
                .position("top right")
                .hideDelay(3000)
        );
    }
}