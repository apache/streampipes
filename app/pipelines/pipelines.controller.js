import {StartAllPipelinesController} from './dialog/start-all-pipelines-dialog.controller';
import {PipelineCategoriesDialogController} from './dialog/pipeline-categories-dialog.controller';

export class PipelineCtrl {

    constructor($scope, restApi, $rootScope, $mdDialog, $state, $timeout, $stateParams, imageChecker, getElementIconText) {
        this.restApi = restApi;
        this.$rootScope = $rootScope;
        this.$mdDialog = $mdDialog;
        this.$state = $state;
        this.$timeout = $timeout;
        this.$stateParams = $stateParams;
        this.imageChecker = imageChecker;
        this.getElementIconText = getElementIconText;

        this.pipeline = {};
        this.pipelines = [];
        this.systemPipelines = [];
        this.pipelinShowing = false;
        this.pipelinePlumb = jsPlumb.getInstance({Container: "pipelineDisplay"});
        this.starting = false;
        this.stopping = false;

        this.pipelineCategories = [];
        this.activeCategory = "";

        this.startPipelineDirectly = $stateParams.pipeline;

        $scope.$on('$destroy', () => {
            this.pipelinePlumb.deleteEveryEndpoint();
        });

        this.getPipelineCategories();
        this.getPipelines();
        this.getSystemPipelines();
    }

    setSelectedTab(categoryId) {
        this.activeCategory = categoryId;
    }

    getPipelines() {
        this.restApi.getOwnPipelines()
            .success(pipelines => {
                this.pipelines = pipelines;
                if (this.startPipelineDirectly != "") {
                    angular.forEach(this.pipelines, pipeline => {
                        if (pipeline._id == this.startPipelineDirectly) {
                            pipeline.immediateStart = true;
                        }
                    });
                    this.startPipelineDirectly = "";
                }
            })
            .error(msg => {
                console.log(msg);
            });

    };

    getSystemPipelines() {
        this.restApi.getSystemPipelines()
            .success(pipelines => {
                this.systemPipelines = pipelines;
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

    isTextIconShown(element) {
        return element.iconUrl == null || element.iconUrl == 'http://localhost:8080/img' || typeof element.iconUrl === 'undefined';

    };

    activeClass(pipeline) {
        return 'active-pipeline';
    }

    checkCurrentSelectionStatus(status) {
        var active = true;
        angular.forEach(this.pipelines, pipeline => {
            if (this.activeCategory == "" || pipeline.pipelineCategories == this.activeCategory) {
                if (pipeline.running == status) {
                    active = false;
                }
            }
        });

        return active;
    }

    startAllPipelines(action) {
        this.$mdDialog.show({
            controller: StartAllPipelinesController,
            controllerAs : 'ctrl',
            templateUrl: 'app/pipelines/dialog/start-all-pipelines-dialog.tmpl.html',
            parent: angular.element(document.body),
            clickOutsideToClose: false,
            locals: {
                pipelines: this.pipelines,
                action: action,
                activeCategory: this.activeCategory,
                refreshPipelines: this.refreshPipelines,
                getPipelines: this.getPipelines,
                getSystemPipelines: this.getSystemPipelines
            },
            bindToController: true
        })
    }
    
    showPipelineCategoriesDialog() {
        this.$mdDialog.show({
            controller: PipelineCategoriesDialogController,
            controllerAs : 'ctrl',
            templateUrl: 'app/pipelines/dialog/pipeline-categories-dialog.tmpl.html',
            parent: angular.element(document.body),
            clickOutsideToClose: true,
            locals: {
                pipelines: this.pipelines,
                refreshPipelines: this.refreshPipelines,
                getPipelines: this.getPipelines,
                getSystemPipelines: this.getSystemPipelines
            },
            bindToController: true
        })
    };

    refreshPipelines() {
        console.log("refreshing pipelines");
        this.getPipelines();
        this.getSystemPipelines();
    }
    

    showPipeline(pipeline) {
        pipeline.display = !pipeline.display;
    }
    

    addImageOrTextIcon($element, json) {
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

    elementTextIcon(string) {
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
    

}

PipelineCtrl.$inject = ['$scope', 'restApi', '$rootScope', '$mdDialog', '$state', '$timeout', '$stateParams', 'imageChecker', 'getElementIconText'];