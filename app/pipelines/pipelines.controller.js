import StartAllPipelinesController from './start-all-pipelines.controller';
import PipelineCategoriesDialogController from './pipeline-categories-dialog.controller';

PipelineCtrl.$inject = ['$scope', 'restApi', '$rootScope', '$mdDialog', '$state', '$timeout', '$stateParams', 'imageChecker', 'getElementIconText'];

export default function PipelineCtrl($scope, restApi, $rootScope, $mdDialog, $state, $timeout, $stateParams, imageChecker, getElementIconText) {
    $scope.pipeline = {};
    $scope.pipelines = [];
    $scope.systemPipelines = [];
    $scope.pipelinShowing = false;
    var pipelinePlumb = jsPlumb.getInstance({Container: "pipelineDisplay"});
    $scope.starting = false;
    $scope.stopping = false;

    $scope.pipelineStatus = [];
    $scope.pipelineCategories = [];
    $scope.activeCategory = "";

    $scope.startPipelineDirectly = $stateParams.pipeline;

    $scope.$on('$destroy', function () {
        pipelinePlumb.deleteEveryEndpoint();
    });

    $scope.setSelectedTab = function (categoryId) {
        $scope.activeCategory = categoryId;
    }

    $scope.getPipelines = function () {
        restApi.getOwnPipelines()
            .success(function (pipelines) {
                $scope.pipelines = pipelines;
            })
            .error(function (msg) {
                console.log(msg);
            });

    };
    
    $scope.getSystemPipelines = function() {
        restApi.getSystemPipelines()
            .success(function (pipelines) {
                $scope.systemPipelines = pipelines;
            })
            .error(function (msg) {
                console.log(msg);
            });
    }
    $scope.getPipelines();
    $scope.getSystemPipelines();

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

    $scope.isTextIconShown = function (element) {
        return element.iconUrl == null || element.iconUrl == 'http://localhost:8080/img' || typeof element.iconUrl === 'undefined';

    };

    $scope.activeClass = function (pipeline) {
        return 'active-pipeline';
    }

    $scope.startAllPipelines = function(action) {
        $mdDialog.show({
            controller: StartAllPipelinesController,
            templateUrl: 'app/pipelines/start-all-pipelines.tmpl.html',
            parent: angular.element(document.body),
            scope: $scope,
            preserveScope: true,
            clickOutsideToClose: false,
            locals: {
                pipelines: $scope.pipelines,
                action: action,
                activeCategory: $scope.activeCategory
            }
        })
    }

    $scope.showPipelineCategoriesDialog = function () {
        $mdDialog.show({
            controller: PipelineCategoriesDialogController,
            templateUrl: 'app/pipelines/templates/managePipelineCategoriesDialog.tmpl.html',
            parent: angular.element(document.body),
            scope: $scope,
            preserveScope: true,
            clickOutsideToClose: true
        })
    };

    $scope.showDialog = function (data) {
        $mdDialog.show({
            controller: PipelineStatusDialogController,
            templateUrl: 'app/pipelines/templates/pipelineOperationDialog.tmpl.html',
            parent: angular.element(document.body),
            clickOutsideToClose: true,
            locals: {
                data: data
            }
        })
    };

    $scope.startPipeline = function (pipelineId) {
        $scope.starting = true;
        restApi.startPipeline(pipelineId)
            .success(function (data) {
                $scope.showDialog(data);
                $scope.getPipelines();
                $scope.getSystemPipelines();

                $scope.starting = false;

            })
            .error(function (data) {
                console.log(data);

                $scope.starting = false;

                $scope.showDialog({
                    notifications: [{
                        title: "Network Error",
                        description: "Please check your Network."
                    }]
                });

            });
    };

    $scope.stopPipeline = function (pipelineId) {
        $scope.stopping = true;
        restApi.stopPipeline(pipelineId)
            .success(function (data) {
                $scope.stopping = false;
                $scope.showDialog(data);
                $scope.getPipelines();
                $scope.getSystemPipelines();
            })
            .error(function (data) {
                console.log(data);
                $scope.stopping = false;
                $scope.showDialog({
                    notifications: [{
                        title: "Network Error",
                        description: "Please check your Network."
                    }]
                });

            });
    };

    $scope.getPipelineStatus = function (pipeline) {
        restApi.getPipelineStatusById(pipeline._id)
            .success(function (data) {

                $scope.pipelineStatus[pipeline._id] = data;
                pipeline.displayStatus = !pipeline.displayStatus;
            })
    }


    if ($scope.startPipelineDirectly != "") {
        $scope.startPipeline($scope.startPipelineDirectly);
    }

    $scope.showPipeline = function (pipeline) {
        pipeline.display = !pipeline.display;

        //clearPipelineDisplay();
        //displayPipeline(pipeline);
    };
    $scope.modifyPipeline = function (pipeline) {
        showPipelineInEditor(pipeline);

    };


    $scope.deletePipeline = function (ev, pipelineId) {
        var confirm = $mdDialog.confirm()
            .title('Delete pipeline?')
            .textContent('The pipeline will be removed. ')
            .targetEvent(ev)
            .ok('Delete')
            .cancel('Cancel');
        $mdDialog.show(confirm).then(function () {
            restApi.deleteOwnPipeline(pipelineId)
                .success(function (data) {
                    $scope.getPipelines();
                    $scope.getSystemPipelines();
                    console.log(data);
                })
                .error(function (data) {
                    console.log(data);
                })
        }, function () {

        });
    };

    $scope.addImageOrTextIcon = function ($element, json) {
        imageChecker.imageExists(json.iconUrl, function (exists) {
            if (exists) {
                var $img = $('<img>')
                    .attr("src", json.iconUrl)
                    .addClass('pipeline-display-element-img');
                $element.append($img);
            } else {
                var $span = $("<span>")
                    .text(getElementIconText(json.name) || "N/A")
                    .addClass("element-text-icon")
                $element.append($span);
            }
        });
    }

    $scope.elementTextIcon = function (string) {
        var result = "";
        if (string.length <= 4) {
            result = string;
        } else {
            var words = string.split(" ");
            words.forEach(function (word, i) {
                result += word.charAt(0);
            });
        }
        return result.toUpperCase();
    };


    function showPipelineInEditor(id) {
        $state.go("streampipes.edit", {pipeline: id});
    }

    function PipelineStatusDialogController($scope, $mdDialog, data) {

        $scope.data = data;

        $scope.hide = function () {
            $mdDialog.hide();
        };
        $scope.cancel = function () {
            $mdDialog.cancel();
        };
    }

};
