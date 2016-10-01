PipelineCtrl.$inject = ['$scope', 'restApi', '$rootScope', '$mdDialog', '$state', '$timeout', '$stateParams', 'imageChecker', 'getElementIconText'];

export default function PipelineCtrl($scope, restApi, $rootScope, $mdDialog, $state, $timeout, $stateParams, imageChecker, getElementIconText) {
    $scope.pipeline = {};
    $scope.pipelines = [];
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
    $scope.getPipelines();

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

    function PipelineCategoriesDialogController($scope, $mdDialog) {

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

    function PipelineStatusDialogController($scope, $mdDialog, data) {

        $scope.data = data;

        $scope.hide = function () {
            $mdDialog.hide();
        };
        $scope.cancel = function () {
            $mdDialog.cancel();
        };
    }

    function getLeftLocation(e) {

        var menuWidth = $('#contextMenu').width();
        var mouseWidth = e.pageX;
        var pageWidth = $(window).width();

        // opening menu would pass the side of the page
        if (mouseWidth + menuWidth > pageWidth && menuWidth < mouseWidth) {
            return mouseWidth - menuWidth;
        }
        return mouseWidth;
    }

    function getTopLocation(e) {

        var menuHeight = $('#contextMenu').height();

        var mouseHeight = e.pageY - $(window).scrollTop();
        var pageHeight = $(window).height();

        if (mouseHeight + menuHeight > pageHeight && menuHeight < mouseHeight) {
            return mouseHeight - menuHeight;
        }
        return mouseHeight;

    }
};
