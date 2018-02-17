export class EditorCtrl {

    constructor($scope,
                $rootScope,
                RestApi,
                $stateParams,
                $window,
                $mdToast,
                PipelinePositioningService,
                JsplumbBridge,
                EditorDialogManager,
                AuthStatusService) {

        this.$scope = $scope;
        this.$rootScope = $rootScope;
        this.RestApi = RestApi;
        this.$stateParams = $stateParams;
        this.$window = $window;
        this.$mdToast = $mdToast;
        this.pipelinePositioningService = PipelinePositioningService;
        this.JsplumbBridge = JsplumbBridge;
        this.EditorDialogManager = EditorDialogManager;
        this.AuthStatusService = AuthStatusService;

        this.isStreamInAssembly = false;
        this.isSepaInAssembly = false;
        this.isActionInAssembly = false;
        this.currentElements = [];
        this.allElements = {};
        this.currentModifiedPipeline = $stateParams.pipeline;
        this.possibleElements = [];
        this.activePossibleElementFilter = {};
        this.selectedTab = 0;
        $rootScope.title = "StreamPipes";
        this.options = [];
        this.selectedOptions = [];

        this.currentPipelineName = "";
        this.currentPipelineDescription = "";

        this.minimizedEditorStand = false;

        this.currentPipelineElement;
        this.currentPipelineElementDom;

        this.pipelineModel = [];

        if (this.AuthStatusService.email != undefined) {
            this.RestApi
                .getUserDetails()
                .success(user => {
                    if (!user.hideTutorial || user.hideTutorial == undefined) {
                        this.EditorDialogManager.showTutorialDialog().then(() => {
                            user.hideTutorial = true;
                            this.RestApi.updateUserDetails(user).success(data => {
                                this.$window.open('https://docs.streampipes.org', '_blank');
                            });
                        }, function () {
                        });
                    }
                })
        }

        angular.element($window).on('scroll', () => {
            JsplumbBridge.repaintEverything();
        });

        $scope.$on('$destroy', () => {
            JsplumbBridge.deleteEveryEndpoint();
        });

        $scope.$on('$viewContentLoaded', event => {
            JsplumbBridge.setContainer("assembly");

            //this.initAssembly();
            //this.initPlumb();
        });

        $rootScope.$on("elements.loaded", () => {
            this.makeDraggable();
        });

        this.tabs = [
            {
                title: 'Data Streams',
                type: 'stream',
            },
            {
                title: 'Data Processors',
                type: 'sepa',
            },
            {
                title: 'Data Sinks',
                type: 'action',
            }
        ];

        this.loadSources();
        this.loadSepas();
        this.loadActions();
    }

    isValidPipeline() {
        return true;
        // TODO change later
        //return this.isStreamInAssembly && this.isActionInAssembly;
    }

    toggleEditorStand() {
        this.minimizedEditorStand = !this.minimizedEditorStand;
    }

    currentFocus(element, active) {
        if (active) this.currentlyFocusedElement = element;
        else this.currentlyFocusedElement = undefined;
    }

    currentFocusActive(element) {
        return this.currentlyFocusedElement == element;
    }

    possibleFilter(value, index, array) {
        if (this.possibleElements.length > 0) {
            for (var i = 0; i < this.possibleElements.length; i++) {
                if (value.belongsTo === this.possibleElements[i].elementId) {
                    return true;
                }
            }
            return false;
        }
        return true;
    };

    selectFilter(value, index, array) {
        if (this.selectedOptions.length > 0) {
            var found = false;
            if (value.category.length == 0) value.category[0] = "UNCATEGORIZED";
            angular.forEach(value.category, c => {
                if (this.selectedOptions.indexOf(c) > -1) found = true;
            });
            return found;
        } else {
            return false;
        }
    };

    toggleFilter(option) {
        this.selectedOptions = [];
        this.selectedOptions.push(option.type);
    }

    optionSelected(option) {
        return this.selectedOptions.indexOf(option.type) > -1;
    }

    selectAllOptions() {
        this.selectedOptions = [];
        angular.forEach(this.options, o => {
            this.selectedOptions.push(o.type);
        });
    }

    deselectAllOptions() {
        this.selectedOptions = [];
    }

    showImageIf(iconUrl) {
        return !!(iconUrl != null && iconUrl != 'http://localhost:8080/img' && iconUrl !== 'undefined');
    };

    loadCurrentElements(type) {

        this.currentElements = [];
        if (type == 'stream') {
            this.loadOptions("stream");
            this.currentElements = this.allElements["stream"];
        } else if (type == 'sepa') {
            this.loadOptions("sepa");
            this.currentElements = this.allElements["sepa"];
        } else if (type == 'action') {
            this.loadOptions("action");
            this.currentElements = this.allElements["action"];
        }
    };

    displayPipelineById() {
        this.RestApi.getPipelineById(this.currentModifiedPipeline)
            .success((pipeline) => {
                this.pipelinePositioningService.displayPipeline(this.$scope, pipeline, "#assembly", false);
                this.currentPipelineName = pipeline.name;
                this.currentPipelineDescription = pipeline.description;

            })
    };

    loadOptions(type) {
        this.options = [];
        this.selectedOptions = [];

        if (type == 'stream') {
            this.RestApi.getEpCategories()
                .then(s => this.handleCategoriesSuccess(s), e => this.handleCategoriesError(e));
        } else if (type == 'sepa') {
            this.RestApi.getEpaCategories()
                .then(s => this.handleCategoriesSuccess(s), e => this.handleCategoriesError(e));
        } else if (type == 'action') {
            this.RestApi.getEcCategories()
                .then(s => this.handleCategoriesSuccess(s), e => this.handleCategoriesError(e));
        }
    };

    handleCategoriesSuccess(result) {
        this.options = result.data;
        angular.forEach(this.options, o => {
            this.selectedOptions.push(o.type);
        });
    }

    handleCategoriesError(error) {
        this.options = [];
    }

    loadSources() {
        var tempStreams = [];
        this.RestApi.getOwnSources()
            .then((sources) => {
                sources.data.forEach((source, i, sources) => {
                    source.spDataStreams.forEach(function (stream) {
                        stream.type = 'stream';
                        tempStreams = tempStreams.concat(stream);
                    });
                    this.allElements["stream"] = tempStreams;
                    this.currentElements = this.allElements["stream"];
                });
            });
    };

    loadSepas() {
        this.RestApi.getOwnSepas()
            .success(sepas => {
                $.each(sepas, (i, sepa) => {
                    sepa.type = 'sepa';
                });
                this.allElements["sepa"] = sepas;
            })
    };

    loadActions() {
        this.RestApi.getOwnActions()
            .success((actions) => {
                $.each(actions, (i, action) => {
                    action.type = 'action';
                });
                this.allElements["action"] = actions;
            });
    };

    makeDraggable() {
        $('.draggable-icon').draggable({
            revert: 'invalid',
            helper: 'clone',
            stack: '.draggable-icon',
            start: function (el, ui) {
                ui.helper.appendTo('#content');
                $('#outerAssemblyArea').css('border', '3px dashed rgb(255,64,129)');
            },
            stop: function (el, ui) {
                $('#outerAssemblyArea').css('border', '1px solid rgb(63,81,181)');
            }
        });
    };

    elementTextIcon(string) {
        var result = "";
        if (string.length <= 4) {
            result = string;
        } else {
            var words = string.split(" ");
            words.forEach(function (word, i) {
                if (word.charAt(0) != '(' && word.charAt(0) != ')') {
                    result += word.charAt(0);
                }
            });
        }
        return string;
    }

    clearCurrentElement() {
        this.$rootScope.state.currentElement = null;
    };

}

EditorCtrl.$inject = ['$scope',
    '$rootScope',
    'RestApi',
    '$stateParams',
    '$window',
    '$mdToast',
    'PipelinePositioningService',
    'JsplumbBridge',
    'EditorDialogManager',
    'AuthStatusService'];
