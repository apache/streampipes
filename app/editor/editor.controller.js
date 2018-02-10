//import jQueryUi from 'npm/jquery-ui';

import CustomizeController from './customize.controller';
import MatchingErrorController from './matching-error.controller';
import SavePipelineController from './save-pipeline.controller';
import HelpDialogController from './dialog/help/help-dialog.controller';
import TopicSelectionController from './components/topic/topic-selection-modal.controller';
import {InitTooltips} from "../services/init-tooltips.service";

export class EditorCtrl {

    constructor($scope,
                $rootScope,
                $state,
                $timeout,
                $http,
                RestApi,
                $stateParams,
                objectProvider,
                apiConstants,
                $q,
                $mdDialog,
                $window,
                $compile,
                InitTooltips,
                $mdToast,
                JsplumbService,
                jsplumbConfigService,
                PipelinePositioningService,
                PipelineEditorService,
                JsplumbBridge) {

        this.$scope = $scope;
        this.$rootScope = $rootScope;
        this.$state = $state;
        this.$timeout = $timeout;
        this.$http = $http;
        this.RestApi = RestApi;
        this.$stateParams = $stateParams;
        this.objectProvider = objectProvider;
        this.apiConstants = apiConstants;
        this.$q = $q;
        this.$mdDialog = $mdDialog;
        this.$window = $window;
        this.$compile = $compile;
        this.InitTooltips = InitTooltips;
        this.$mdToast = $mdToast;
        this.jsplumbService = JsplumbService;
        this.jsplumbConfigService = jsplumbConfigService;
        this.pipelinePositioningService = PipelinePositioningService;
        this.pipelineEditorService = PipelineEditorService;
        this.JsplumbBridge = JsplumbBridge;

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

        this.selectMode = true;

        this.currentZoomLevel = 1;

        this.currentPipelineElement;
        this.currentPipelineElementDom;

        var jsplumbConfig = jsplumbConfigService.getEditorConfig();

        if (this.$rootScope.email != undefined) {
            this.RestApi
                .getUserDetails()
                .success(function (user) {
                    if (!user.hideTutorial || user.hideTutorial == undefined) {
                        var confirm = $mdDialog.confirm()
                            .title('Welcome to StreamPipes!')
                            .textContent('If you are new to StreamPipes, check out our user guide')
                            .ok('Show tutorial')
                            .cancel('Cancel');

                        $mdDialog.show(confirm).then(() => {
                            user.hideTutorial = true;
                            this.RestApi.updateUserDetails(user).success(function (data) {

                                this.$window.open('https://docs.streampipes.org', '_blank');
                            });
                        }, function () {

                        });
                    }
                })
        }

        // T1
        $("#assembly").panzoom({
            disablePan: true,
            increment: 0.25,
            minScale: 0.5,
            maxScale: 1.5,
            contain: 'invert'
        });

        $("#assembly").on('panzoomzoom', (e, panzoom, scale) => {
            this.currentZoomLevel = scale;
            JsplumbBridge.setZoom(scale);
            JsplumbBridge.repaintEverything();
        });

        // T1
        angular.element($window).on('scroll', () => {
            JsplumbBridge.repaintEverything();
        });


        // T1
        $scope.$on('$destroy', () => {
            JsplumbBridge.deleteEveryEndpoint();
        });

        // T1
        $scope.$on('$viewContentLoaded', event => {
            JsplumbBridge.setContainer("assembly");

            this.initAssembly();
            this.initPlumb();
        });

        // T1
        $rootScope.$on("elements.loaded", () => {
            this.makeDraggable();
        });

        // T1
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

        // T1
        this.loadSources();
        this.loadSepas();
        this.loadActions();
    }

    isValidPipeline() {
        return this.isStreamInAssembly && this.isActionInAssembly;
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

    showElementInfo(element) {
        var dialogTemplate = this.getDialogTemplate(HelpDialogController, 'app/editor/components/pipeline-element-options/help-dialog.tmpl.html');
        dialogTemplate.locals = {
            pipelineElement: element
        }
        $mdDialog.show(dialogTemplate);
    };

    autoLayout() {
        this.pipelinePositioningService.layoutGraph("#assembly", "span.connectable-editor", 110, false);
        this.JsplumbBridge.repaintEverything();
    }

    toggleSelectMode() {
        if (this.selectMode) {
            $("#assembly").panzoom("option", "disablePan", false);
            $("#assembly").selectable("disable");
            thisArg.selectMode = false;
        }
        else {
            $("#assembly").panzoom("option", "disablePan", true);
            $("#assembly").selectable("enable");
            this.selectMode = true;
        }
    }

    zoomOut() {
        this.doZoom(true);
    }

    zoomIn() {
        this.doZoom(false);
    }

    doZoom(zoomOut) {
        $("#assembly").panzoom("zoom", zoomOut);
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

    showSavePipelineDialog(elementData, sepaName) {
        this.$rootScope.state.currentElement = elementData;
        var dialogContent = this.getDialogTemplate(SavePipelineController, 'app/editor/components/submitPipelineModal.tmpl.html');
        this.$mdDialog.show(dialogContent);
    }

    showMatchingErrorDialog(elementData) {
        var dialogContent = this.getDialogTemplate(MatchingErrorController, 'app/editor/components/matchingErrorDialog.tmpl.html');
        dialogContent.locals = {
            elementData: elementData
        }
        this.$mdDialog.show(dialogContent);
    }

    showCustomizeDialog(elementData, sepaName, sourceEndpoint) {
        this.$rootScope.state.currentElement = elementData;
        var dialogContent = this.getDialogTemplate(CustomizeController, 'app/editor/components/customizeElementDialog.tmpl.html');
        dialogContent.locals = {
            elementData: elementData,
            sepaName: sepaName,
            sourceEndpoint: sourceEndpoint
        }
        this.$mdDialog.show(dialogContent);
    };

    getDialogTemplate(controller, templateUrl) {
        return {
            controller: controller,
            templateUrl: templateUrl,
            parent: angular.element(document.body),
            clickOutsideToClose: true,
            scope: this.$scope,
            rootScope: this.$rootScope,
            preserveScope: true
        }
    }

    showClearAssemblyConfirmDialog(ev) {
        var confirm = this.$mdDialog.confirm()
            .title('Clear assembly area?')
            .textContent('All pipeline elements in the assembly area will be removed.')
            .targetEvent(ev)
            .ok('Clear assembly')
            .cancel('Cancel');
        this.$mdDialog.show(confirm).then(() => {
            this.clearAssembly();
        }, function () {

        });
    };

    openContextMenu($mdOpenMenu, event) {
        $mdOpenMenu(event.$event);
        alert("open context menu");
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
        console.log(result);
        this.options = result.data;
        angular.forEach(this.options, o => {
            this.selectedOptions.push(o.type);
        });
    }

    handleCategoriesError(error) {
        this.options = [];
        console.log(error);
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
            }, function (msg) {
                console.log(msg);
            });
    };


    loadSepas() {
        this.RestApi.getOwnSepas()
            .success(sepas => {
                $.each(sepas, (i, sepa) => {
                    sepa.type = 'sepa';
                });
                this.allElements["sepa"] = sepas;
                this.$timeout(() => {
                    //makeDraggable();
                    this.$rootScope.state.sepas = $.extend(true, [], this.allElements["sepa"]);
                })

            })
    };

    loadActions() {
        this.RestApi.getOwnActions()
            .success((actions) => {
                $.each(actions, (i, action) => {
                    action.type = 'action';
                });
                this.allElements["action"] = actions;
                this.$timeout(() => {
                    this.$rootScope.state.actions = $.extend(true, [], this.allElements["action"]);
                })

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

    //TODO ANGULARIZE
    //Initiate assembly and jsPlumb functionality-------
    initPlumb() {
        this.$rootScope.state.plumbReady = true;

        this.jsplumbService.prepareJsplumb();

        this.JsplumbBridge.unbind("connection");

        this.JsplumbBridge.bind("connectionDetached", (info, originalEvent) => {
            var el = ($("#" + info.targetEndpoint.elementId));
            el.data("JSON", $.extend(true, {}, getPipelineElementContents(el.data("JSON").belongsTo)));
            el.removeClass('a');
            el.addClass('disabled');
            info.targetEndpoint.setType("empty");
        });

        this.JsplumbBridge.bind("connectionDrag", connection => {
            this.JsplumbBridge.selectEndpoints().each(function (endpoint) {
                if (endpoint.isTarget && endpoint.connections.length == 0) {
                    endpoint.setType("highlight");
                }
            });

        });
        this.JsplumbBridge.bind("connectionAborted", connection => {
            this.JsplumbBridge.selectEndpoints().each(endpoint => {
                if (endpoint.isTarget && endpoint.connections.length == 0) {
                    endpoint.setType("empty");
                }
            });
        })

        this.JsplumbBridge.bind("connection", (info, originalEvent) => {
            var $target = $(info.target);

            if (!$target.hasClass('a')) { //class 'a' = do not show customize modal //TODO class a zuweisen
                this.$rootScope.state.currentPipeline = this.createPartialPipeline(info.target, false);
                this.$rootScope.state.currentPipeline.update()
                    .success(data => {
                        if (data.success) {
                            info.targetEndpoint.setType("token");
                            this.modifyPipeline(data.pipelineModifications);
                            for (var i = 0, sepa; sepa = this.$rootScope.state.currentPipeline.sepas[i]; i++) {
                                var id = "#" + sepa.DOM;
                                if ($(id).length > 0) {
                                    if ($(id).data("JSON").configured != true) {
                                        if (!this.pipelineEditorService.isFullyConnected(id)) {
                                            return;
                                        }
                                        var sourceEndpoint = this.JsplumbBridge.selectEndpoints({element: info.targetEndpoint.elementId});
                                        this.showCustomizeDialog($(id), sepa.name, sourceEndpoint);
                                    }
                                }
                            }
                            for (var i = 0, action; action = this.$rootScope.state.currentPipeline.actions[i]; i++) {
                                var id = "#" + action.DOM;
                                if ($(id).length > 0) {
                                    if ($(id).data("JSON").configured != true) {
                                        if (!this.pipelineEditorService.isFullyConnected(id)) {
                                            return;
                                        }
                                        var actionEndpoint = this.JsplumbBridge.selectEndpoints({element: info.targetEndpoint.elementId});
                                        this.showCustomizeDialog($(id), action.name, actionEndpoint);
                                    }
                                }
                            }
                        } else {
                            this.JsplumbBridge.detach(info.connection);
                            this.showMatchingErrorDialog(data);
                        }
                    })
            }
        });

        window.onresize = function (event) {
            this.JsplumbBridge.repaintEverything(true);
        };
    }

    getPipelineElementContents(belongsTo) {
        var pipelineElement = undefined;
        angular.forEach(this.allElements, category => {
            angular.forEach(category, function (sepa) {
                if (sepa.belongsTo == belongsTo) {
                    pipelineElement = sepa;
                }
            });
        });
        return pipelineElement;
    }

    initAssembly() {
        $('#assembly').droppable({
            tolerance: "fit",
            drop: (element, ui) => {

                if (ui.draggable.hasClass('draggable-icon')) {
                    if (ui.draggable.data("JSON") == null) {
                        alert("No JSON - Data for Dropped element");
                        return false;
                    }
                    var $newState = this.jsplumbService.createNewAssemblyElement(ui.draggable.data("JSON"), this.pipelineEditorService.getCoordinates(ui, this.currentZoomLevel), false, "#assembly");

                    //Droppable Streams
                    if (ui.draggable.hasClass('stream')) {
                        this.checkTopicModel($newState);

                        //Droppable Sepas
                    } else if (ui.draggable.hasClass('sepa')) {
                        this.jsplumbService.sepaDropped(this.$scope, $newState, true);

                        //Droppable Actions
                    } else if (ui.draggable.hasClass('action')) {
                        this.jsplumbService.actionDropped(this.$scope, $newState, true);
                    }
                    this.InitTooltips.initTooltips();
                }
                this.JsplumbBridge.repaintEverything(true);
            }

        }); //End #assembly.droppable()
        $("#assembly")
            .selectable({
                selected: function (event, ui) {
                },
                filter: ".connectable.stream,.connectable.sepa:not('.disabled')",
                delay: 150

            })
            .on('click', ".recommended-item", function (e) {
                e.stopPropagation();
                this.createAndConnect(this);
            });


        $(document).click(function () {
            $('#staticContextMenu').hide();
            $('.circleMenu-open').circleMenu('close');
        });

        if (typeof this.currentModifiedPipeline != 'undefined') {
            this.$rootScope.state.adjustingPipelineState = true;
            this.displayPipelineById();
        }

    };

    checkTopicModel(state) {
        this.jsplumbService.streamDropped(this.$scope, state, true);
        var streamDescription = state.data("JSON");
        if (streamDescription
                .eventGrounding
                .transportProtocols[0]
                .properties.topicDefinition
                .type == "org.streampipes.model.grounding.WildcardTopicDefinition") {
            this.showCustomizeStreamDialog(state);
        } else {
            console.log("Wrong format");
            console.log(streamDescription);
        }
    }

    showCustomizeStreamDialog(state) {
        var dialogContent = this.getDialogTemplate(TopicSelectionController, 'app/editor/components/topic/topic-selection-modal.tmpl.html');
        dialogContent.locals = {
            state: state
        }
        this.$mdDialog.show(dialogContent);
    }

    /**
     * clears the Assembly of all elements
     */
    clearAssembly() {
        $('#assembly').children().not('#clear, #submit').remove();
        this.JsplumbBridge.deleteEveryEndpoint();
        this.$rootScope.state.adjustingPipelineState = false;
        $("#assembly").panzoom("reset", {
            disablePan: true,
            increment: 0.25,
            minScale: 0.5,
            maxScale: 1.5,
            contain: 'invert'
        });
        this.currentZoomLevel = 1;
        this.JsplumbBridge.setZoom(this.currentZoomLevel);
        this.JsplumbBridge.repaintEverything();
    };

    createPartialPipeline(currentElement, recommendationConfig) {
        var pipelinePart = new this.objectProvider.Pipeline();
        this.addElementToPartialPipeline(currentElement, pipelinePart, recommendationConfig);
        return pipelinePart;
    }

    addElementToPartialPipeline(element, pipelinePart, recommendationConfig) {
        pipelinePart.addElement(element);
        // add all children of pipeline element that are not already present in the pipeline
        if (!recommendationConfig) {
            var outgoingConnections = this.JsplumbBridge.getConnections({source: element});
            if (outgoingConnections.length > 0) {
                for (var j = 0, ocon; ocon = outgoingConnections[j]; j++) {
                    if (!pipelinePart.hasElement(ocon.target.id)) {
                        this.addElementToPartialPipeline(ocon.target, pipelinePart, recommendationConfig);
                    }
                }
            }
        }

        // add all parents of pipeline element
        var connections = this.JsplumbBridge.getConnections({target: element});
        if (connections.length > 0) {
            for (var i = 0, con; con = connections[i]; i++) {
                this.addElementToPartialPipeline(con.source, pipelinePart);
            }
        }
    }

    /**
     * Sends the pipeline to the server
     */
    submit() {
        var error = false;
        var pipelineNew = new this.objectProvider.Pipeline();
        var streamPresent = false;
        var sepaPresent = false;
        var actionPresent = false;


        $('#assembly').find('.connectable, .connectable-block').each((i, element) => {
            var $element = $(element);

            if (!this.pipelineEditorService.isConnected(element)) {
                error = true;
                this.showToast("error", "All elements must be connected", "Submit Error");
            }

            if ($element.hasClass('sepa')) {
                sepaPresent = true;
                if ($element.data("options")) {
                    pipelineNew.addElement(element);

                } else if ($element.data("JSON").staticProperties != null) {
                    this.showToast("error", "Please enter parameters for transparent elements (Right click -> Customize)", "Submit Error");
                    error = true;
                }
            } else if ($element.hasClass('stream')) {
                streamPresent = true;
                pipelineNew.addElement(element);

            } else if ($element.hasClass('action')) {
                actionPresent = true;
                if ($element.data("JSON").staticProperties == null || $element.data("options")) {
                    pipelineNew.addElement(element);
                } else {
                    this.showToast("error", "Please enter parameters for transparent elements (Right click -> Customize)", "Submit Error");
                    ;
                    error = true;
                }
            }
        });
        if (!streamPresent) {
            this.showToast("error", "No stream element present in pipeline", "Submit Error");
            error = true;
        }

        if (!actionPresent) {
            this.showToast("error", "No action element present in pipeline", "Submit Error");
            error = true;
        }
        if (!error) {
            this.$rootScope.state.currentPipeline = pipelineNew;
            if (this.$rootScope.state.adjustingPipelineState) {
                this.$rootScope.state.currentPipeline.name = this.currentPipelineName;
                this.$rootScope.state.currentPipeline.description = this.currentPipelineDescription;
            }

            this.openPipelineNameModal();
        }
    }

    openPipelineNameModal() {
        if (this.$rootScope.state.adjustingPipelineState) {
            this.modifyPipelineMode = true;
        }
        this.showSavePipelineDialog();
    }

    createAssemblyElement(json, $parentElement) {
        var x = $parentElement.position().left;
        var y = $parentElement.position().top;
        var coord = {'x': x + 200, 'y': y};
        var $target;
        var $createdElement = this.jsplumbService.createNewAssemblyElement(json, coord, false, "#assembly");
        if (json.belongsTo.indexOf("sepa") > 0) { //Sepa Element
            $target = this.jsplumbService.sepaDropped(this.$scope, $createdElement, true);
        } else {
            $target = this.jsplumbService.actionDropped(this.$scope, $createdElement, true);
        }

        var options;
        if ($parentElement.hasClass("stream")) {
            options = this.jsplumbConfig.streamEndpointOptions;
        } else {
            options = this.jsplumbConfig.sepaEndpointOptions;
        }
        var sourceEndPoint;
        if (this.JsplumbBridge.selectEndpoints({source: $parentElement}).length > 0) {

            if (!(this.JsplumbBridge.selectEndpoints({source: $parentElement}).get(0).isFull())) {
                sourceEndPoint = this.JsplumbBridge.selectEndpoints({source: $parentElement}).get(0)
            } else {
                sourceEndPoint = this.JsplumbBridge.addEndpoint($parentElement, options);
            }
        } else {
            sourceEndPoint = this.JsplumbBridge.addEndpoint($parentElement, options);
        }

        var targetEndPoint = this.JsplumbBridge.selectEndpoints({target: $target}).get(0);

        this.JsplumbBridge.connect({source: sourceEndPoint, target: targetEndPoint, detachable: true});
        this.JsplumbBridge.repaintEverything();
    }

    createAndConnect(target) {
        var json = $("a", $(target)).data("recObject").json;
        var $parentElement = $(target).parents(".connectable");
        this.createAssemblyElement(json, $parentElement);
    }

    clearCurrentElement() {
        this.$rootScope.state.currentElement = null;
    };

    handleDeleteOption($element) {
        this.JsplumbBridge.removeAllEndpoints($element);
        $element.remove();
    }

    modifyPipeline(pipelineModifications) {
        var id;
        for (var i = 0, modification; modification = pipelineModifications[i]; i++) {
            id = "#" + modification.domId;
            if ($(id) !== "undefined") {
                $(id).data("JSON").staticProperties = modification.staticProperties;
                $(id).data("JSON").outputStrategies = modification.outputStrategies;
                $(id).data("JSON").inputStreams = modification.inputStreams;
            }
        }
    }

    showToast(type, title, description) {
        this.$mdToast.show(
            this.$mdToast.simple()
                .textContent(title)
                .position("top right")
                .hideDelay(3000)
        );
    }

}

EditorCtrl.$inject = ['$scope',
    '$rootScope',
    '$state',
    '$timeout',
    '$http',
    'RestApi',
    '$stateParams',
    'objectProvider',
    'apiConstants',
    '$q',
    '$mdDialog',
    '$window',
    '$compile',
    'InitTooltips',
    '$mdToast',
    'JsplumbService',
    'jsplumbConfigService',
    'PipelinePositioningService',
    'PipelineEditorService',
    'JsplumbBridge'];
