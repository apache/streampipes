//import jQueryUi from 'npm/jquery-ui';

import CustomizeController from './customize.controller';
import MatchingErrorController from './matching-error.controller';
import SavePipelineController from './save-pipeline.controller';
import HelpDialogController from './directives/pipeline-element-options/help-dialog.controller';
import TopicSelectionController from './directives/topic/topic-selection-modal.controller';

EditorCtrl.$inject = ['$scope',
    '$rootScope',
    '$state',
    '$timeout',
    '$http',
    'restApi',
    '$stateParams',
    'objectProvider',
    'apiConstants',
    '$q',
    '$mdDialog',
    '$window',
    '$compile',
    'getElementIconText',
    'initTooltips',
    '$mdToast',
    'jsplumbService',
    'jsplumbConfigService',
    'pipelinePositioningService',
    'pipelineEditorService'];


export default function EditorCtrl($scope, $rootScope, $state, $timeout, $http, restApi, $stateParams, objectProvider, apiConstants, $q, $mdDialog, $window, $compile, getElementIconText, initTooltips, $mdToast, jsplumbService, jsplumbConfigService, pipelinePositioningService, pipelineEditorService) {

    $scope.standardUrl = "http://localhost:8080/semantic-epa-backend/api/";
    $scope.isStreamInAssembly = false;
    $scope.isSepaInAssembly = false;
    $scope.isActionInAssembly = false;
    $scope.currentElements = [];
    $scope.allElements = {};
    $scope.currentModifiedPipeline = $stateParams.pipeline;
    $scope.possibleElements = [];
    $scope.activePossibleElementFilter = {};
    $scope.selectedTab = 0;
    $rootScope.title = "StreamPipes";
    $scope.options = [];
    $scope.selectedOptions = [];

    $scope.currentPipelineName = "";
    $scope.currentPipelineDescription = "";

    $scope.minimizedEditorStand = false;

    $scope.selectMode = true;

    $scope.currentZoomLevel = 1;

    $scope.currentPipelineElement;
    $scope.currentPipelineElementDom;

    var jsplumbConfig = jsplumbConfigService.getEditorConfig();

    var logError = function(error) {
        console.log(error);
    }

    if ($rootScope.email != undefined) {
        restApi
            .getUserDetails()
            .success(function (user) {
                if (!user.hideTutorial || user.hideTutorial == undefined) {
                    var confirm = $mdDialog.confirm()
                        .title('Welcome to StreamPipes!')
                        .textContent('If you are new to StreamPipes, check out our user guide')
                        .ok('Show tutorial')
                        .cancel('Cancel');

                    $mdDialog.show(confirm).then(function () {
                        user.hideTutorial = true;
                        restApi.updateUserDetails(user).success(function (data) {

                            $window.open('https://docs.streampipes.org', '_blank');
                        });
                    }, function () {

                    });
                }
            })
            .error(logError);
    }


    $scope.isValidPipeline = function () {
        return $scope.isStreamInAssembly && $scope.isActionInAssembly;
    }

    $scope.toggleEditorStand = function () {
        $scope.minimizedEditorStand = !$scope.minimizedEditorStand;
    }

    $scope.currentFocus = function (element, active) {
        if (active) $scope.currentlyFocusedElement = element;
        else $scope.currentlyFocusedElement = undefined;
    }

    $scope.currentFocusActive = function (element) {
        return $scope.currentlyFocusedElement == element;
    }

    $scope.showElementInfo = function (element) {
        var dialogTemplate = getDialogTemplate(HelpDialogController, 'app/editor/directives/pipeline-element-options/help-dialog.tmpl.html');
        dialogTemplate.locals = {
            pipelineElement: element
        }
        $mdDialog.show(dialogTemplate);
    };

    $scope.autoLayout = function () {
        pipelinePositioningService.layoutGraph("#assembly", "span.connectable-editor", jsPlumb, 110, false);
        jsPlumb.repaintEverything();
    }

    $("#assembly").panzoom({
        disablePan: true,
        increment: 0.25,
        minScale: 0.5,
        maxScale: 1.5,
        contain: 'invert'
    });

    $("#assembly").on('panzoomzoom', function (e, panzoom, scale) {
        $scope.currentZoomLevel = scale;
        jsPlumb.setZoom(scale);
        jsPlumb.repaintEverything();
    });

    $scope.toggleSelectMode = function () {
        if ($scope.selectMode) {
            $("#assembly").panzoom("option", "disablePan", false);
            $("#assembly").selectable("disable");
            $scope.selectMode = false;
        }
        else {
            $("#assembly").panzoom("option", "disablePan", true);
            $("#assembly").selectable("enable");
            $scope.selectMode = true;
        }
    }

    $scope.zoomOut = function () {
        doZoom(true);
    }

    $scope.zoomIn = function () {
        doZoom(false);
    }

    var doZoom = function (zoomOut) {
        $("#assembly").panzoom("zoom", zoomOut);
    }

    $scope.possibleFilter = function (value, index, array) {
        if ($scope.possibleElements.length > 0) {
            for (var i = 0; i < $scope.possibleElements.length; i++) {
                if (value.belongsTo === $scope.possibleElements[i].elementId) {
                    return true;
                }
            }
            return false;
        }
        return true;
    };

    $scope.selectFilter = function (value, index, array) {
        if ($scope.selectedOptions.length > 0) {
            var found = false;
            if (value.category.length == 0) value.category[0] = "UNCATEGORIZED";
            angular.forEach(value.category, function (c) {
                if ($scope.selectedOptions.indexOf(c) > -1) found = true;
            });
            return found;
        } else {
            return false;
        }
    };

    $scope.toggleFilter = function (option) {
        $scope.selectedOptions = [];
        $scope.selectedOptions.push(option.type);
    }

    $scope.optionSelected = function (option) {
        return $scope.selectedOptions.indexOf(option.type) > -1;
    }

    $scope.selectAllOptions = function () {
        $scope.selectedOptions = [];
        angular.forEach($scope.options, function (o) {
            $scope.selectedOptions.push(o.type);
        });
    }

    $scope.deselectAllOptions = function () {
        $scope.selectedOptions = [];
    }

    $scope.showImageIf = function (iconUrl) {
        return !!(iconUrl != null && iconUrl != 'http://localhost:8080/img' && iconUrl !== 'undefined');
    };

    $scope.showSavePipelineDialog = function (elementData, sepaName) {
        $rootScope.state.currentElement = elementData;
        var dialogContent = getDialogTemplate(SavePipelineController, 'app/editor/directives/submitPipelineModal.tmpl.html');
        $mdDialog.show(dialogContent);
    }

    $scope.showMatchingErrorDialog = function (elementData) {
        var dialogContent = getDialogTemplate(MatchingErrorController, 'app/editor/directives/matchingErrorDialog.tmpl.html');
        dialogContent.locals = {
            elementData: elementData
        }
        $mdDialog.show(dialogContent);
    }

    $scope.showCustomizeDialog = function (elementData, sepaName, sourceEndpoint) {
        $rootScope.state.currentElement = elementData;
        var dialogContent = getDialogTemplate(CustomizeController, 'app/editor/directives/customizeElementDialog.tmpl.html');
        dialogContent.locals = {
            elementData: elementData,
            sepaName: sepaName,
            sourceEndpoint: sourceEndpoint
        }
        $mdDialog.show(dialogContent);
    };

    var getDialogTemplate = function(controller, templateUrl) {
        return {
            controller: controller,
            templateUrl: templateUrl,
            parent: angular.element(document.body),
            clickOutsideToClose: true,
            scope: $scope,
            rootScope: $rootScope,
            preserveScope: true
        }
    }

    $scope.showClearAssemblyConfirmDialog = function (ev) {
        var confirm = $mdDialog.confirm()
            .title('Clear assembly area?')
            .textContent('All pipeline elements in the assembly area will be removed.')
            .targetEvent(ev)
            .ok('Clear assembly')
            .cancel('Cancel');
        $mdDialog.show(confirm).then(function () {
            $scope.clearAssembly();
        }, function () {

        });
    };

    angular.element($window).on('scroll', function () {
        jsPlumb.repaintEverything(true);
    });


    $scope.$on('$destroy', function () {
        jsPlumb.deleteEveryEndpoint();
    });

    $scope.$on('$viewContentLoaded', function (event) {
        jsPlumb.setContainer("assembly");

        initAssembly();
        initPlumb();
    });
    $rootScope.$on("elements.loaded", function () {
        makeDraggable();
        //initTooltips();
    });
    $scope.openContextMenu = function ($mdOpenMenu, event) {
        $mdOpenMenu(event.$event);
        alert("open context menu");
    };


    $scope.loadCurrentElements = function (type) {

        $scope.currentElements = [];
        if (type == 'stream') {
            $scope.loadOptions("stream");
            $scope.currentElements = $scope.allElements["stream"];
        } else if (type == 'sepa') {
            $scope.loadOptions("sepa");
            $scope.currentElements = $scope.allElements["sepa"];
        } else if (type == 'action') {
            $scope.loadOptions("action");
            $scope.currentElements = $scope.allElements["action"];
        }
    };

    $scope.displayPipelineById = function () {
        restApi.getPipelineById($scope.currentModifiedPipeline)
            .success(function (pipeline) {
                pipelinePositioningService.displayPipeline($scope, jsPlumb, pipeline, "#assembly", false);
                $scope.currentPipelineName = pipeline.name;
                $scope.currentPipelineDescription = pipeline.description;

            })
            .error(logError);

    };

    $scope.tabs = [
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

    $scope.loadOptions = function (type) {
        $scope.options = [];
        $scope.selectedOptions = [];

        if (type == 'stream') {
            restApi.getEpCategories()
                .then(handleCategoriesSuccess, handleCategoriesError);
        } else if (type == 'sepa') {
            restApi.getEpaCategories()
                .then(handleCategoriesSuccess, handleCategoriesError);
        } else if (type == 'action') {
            restApi.getEcCategories()
                .then(handleCategoriesSuccess, handleCategoriesError);
        }
    };

    var handleCategoriesSuccess = function(result) {
        $scope.options = result.data;
        angular.forEach($scope.options, function (o) {
            $scope.selectedOptions.push(o.type);
        });
    }

    var handleCategoriesError = function(error) {
        $scope.options = [];
        console.log(error);
    }

    $scope.loadSources = function () {
        var tempStreams = [];
        restApi.getOwnSources()
            .then(function (sources) {
                sources.data.forEach(function (source, i, sources) {
                    source.spDataStreams.forEach(function (stream) {
                        stream.type = 'stream';
                        tempStreams = tempStreams.concat(stream);
                    });
                    $scope.allElements["stream"] = tempStreams;
                    $scope.currentElements = $scope.allElements["stream"];
                });
            }, function (msg) {
                console.log(msg);
            });
    };


    $scope.loadSepas = function () {
        restApi.getOwnSepas()
            .success(function (sepas) {
                $.each(sepas, function (i, sepa) {
                    sepa.type = 'sepa';
                });
                $scope.allElements["sepa"] = sepas;
                $timeout(function () {
                    //makeDraggable();
                    $rootScope.state.sepas = $.extend(true, [], $scope.allElements["sepa"]);
                })

            })
            .error(logError);
    };
    $scope.loadActions = function () {
        restApi.getOwnActions()
            .success(function (actions) {
                $.each(actions, function (i, action) {
                    action.type = 'action';
                });
                $scope.allElements["action"] = actions;
                $timeout(function () {
                    //makeDraggable();
                    $rootScope.state.actions = $.extend(true, [], $scope.allElements["action"]);
                })

            });
    };

    $scope.loadSources();
    $scope.loadSepas();
    $scope.loadActions();

    var makeDraggable = function () {
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

    $scope.elementTextIcon = function (string) {
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
        //return result.toUpperCase();
    }

    //TODO ANGULARIZE
    //Initiate assembly and jsPlumb functionality-------
    function initPlumb() {
        $rootScope.state.plumbReady = true;

        jsplumbService.prepareJsplumb(jsPlumb);

        jsPlumb.unbind("connection");

        jsPlumb.bind("connectionDetached", function (info, originalEvent) {
            var el = ($("#" + info.targetEndpoint.elementId));
            el.data("JSON", $.extend(true, {}, getPipelineElementContents(el.data("JSON").belongsTo)));
            el.removeClass('a');
            el.addClass('disabled');
            info.targetEndpoint.setType("empty");
        });

        jsPlumb.bind("connectionDrag", function (connection) {
            jsPlumb.selectEndpoints().each(function (endpoint) {
                if (endpoint.isTarget && endpoint.connections.length == 0) {
                    endpoint.setType("highlight");
                }
            });

        });
        jsPlumb.bind("connectionAborted", function (connection) {
            jsPlumb.selectEndpoints().each(function (endpoint) {
                if (endpoint.isTarget && endpoint.connections.length == 0) {
                    endpoint.setType("empty");
                }
            });
        })

        jsPlumb.bind("connection", function (info, originalEvent) {
            var $target = $(info.target);

            if (!$target.hasClass('a')) { //class 'a' = do not show customize modal //TODO class a zuweisen
                $rootScope.state.currentPipeline = $scope.createPartialPipeline(info.target, false);
                $rootScope.state.currentPipeline.update()
                    .success(function (data) {
                        if (data.success) {
                            info.targetEndpoint.setType("token");
                            modifyPipeline(data.pipelineModifications);
                            for (var i = 0, sepa; sepa = $rootScope.state.currentPipeline.sepas[i]; i++) {
                                var id = "#" + sepa.DOM;
                                if ($(id).length > 0) {
                                    if ($(id).data("JSON").configured != true) {
                                        if (!pipelineEditorService.isFullyConnected(id, jsPlumb)) {
                                            return;
                                        }
                                        var sourceEndpoint = jsPlumb.selectEndpoints({element: info.targetEndpoint.elementId});
                                        $scope.showCustomizeDialog($(id), sepa.name, sourceEndpoint);
                                    }
                                }
                            }
                            for (var i = 0, action; action = $rootScope.state.currentPipeline.actions[i]; i++) {
                                var id = "#" + action.DOM;
                                if ($(id).length > 0) {
                                    if ($(id).data("JSON").configured != true) {
                                        if (!pipelineEditorService.isFullyConnected(id, jsPlumb)) {
                                            return;
                                        }
                                        var actionEndpoint = jsPlumb.selectEndpoints({element: info.targetEndpoint.elementId});
                                        $scope.showCustomizeDialog($(id), action.name, actionEndpoint);
                                    }
                                }
                            }
                        } else {
                            jsPlumb.detach(info.connection);
                            $scope.showMatchingErrorDialog(data);
                        }
                    })
                    .error(logError);
            }
        });

        window.onresize = function (event) {
            jsPlumb.repaintEverything(true);
        };
    }

    var getPipelineElementContents = function (belongsTo) {
        var pipelineElement = undefined;
        angular.forEach($scope.allElements, function (category) {
            angular.forEach(category, function (sepa) {
                if (sepa.belongsTo == belongsTo) {
                    pipelineElement = sepa;
                }
            });
        });
        return pipelineElement;
    }

    function initAssembly() {
        $('#assembly').droppable({
            tolerance: "fit",
            drop: function (element, ui) {

                if (ui.draggable.hasClass('draggable-icon')) {
                    if (ui.draggable.data("JSON") == null) {
                        alert("No JSON - Data for Dropped element");
                        return false;
                    }
                    var $newState = jsplumbService.createNewAssemblyElement(jsPlumb, ui.draggable.data("JSON"), pipelineEditorService.getCoordinates(ui, $scope.currentZoomLevel), false, "#assembly");

                    //Droppable Streams
                    if (ui.draggable.hasClass('stream')) {
                        checkTopicModel($newState);

                    //Droppable Sepas
                    } else if (ui.draggable.hasClass('sepa')) {
                        jsplumbService.sepaDropped($scope, jsPlumb, $newState, true);

                        //Droppable Actions
                    } else if (ui.draggable.hasClass('action')) {
                        jsplumbService.actionDropped($scope, jsPlumb, $newState, true);
                    }
                    initTooltips();
                }
                jsPlumb.repaintEverything(true);
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
                $scope.createAndConnect(this);
            });


        $(document).click(function () {
            $('#staticContextMenu').hide();
            $('.circleMenu-open').circleMenu('close');
        });

        if (typeof $scope.currentModifiedPipeline != 'undefined') {
            $rootScope.state.adjustingPipelineState = true;
            $scope.displayPipelineById();
        }

    };

    var checkTopicModel = function(state) {
        jsplumbService.streamDropped($scope, jsPlumb, state, true);
        var streamDescription = state.data("JSON");
        if (streamDescription
                .eventGrounding
                .transportProtocols[0]
                .properties.
                topicDefinition
                .type == "org.streampipes.model.grounding.WildcardTopicDefinition") {
            $scope.showCustomizeStreamDialog(state);
        } else {
            console.log("Wrong format");
            console.log(streamDescription);
        }
    }

    $scope.showCustomizeStreamDialog = function(state) {
        var dialogContent = getDialogTemplate(TopicSelectionController, 'app/editor/directives/topic/topic-selection-modal.tmpl.html');
        dialogContent.locals = {
            state : state
        }
        $mdDialog.show(dialogContent);
    }

    /**
     * clears the Assembly of all elements
     */
    $scope.clearAssembly = function () {
        $('#assembly').children().not('#clear, #submit').remove();
        jsPlumb.deleteEveryEndpoint();
        $rootScope.state.adjustingPipelineState = false;
        $("#assembly").panzoom("reset", {
            disablePan: true,
            increment: 0.25,
            minScale: 0.5,
            maxScale: 1.5,
            contain: 'invert'
        });
        $scope.currentZoomLevel = 1;
        jsPlumb.setZoom($scope.currentZoomLevel);
        jsPlumb.repaintEverything();
    };

    $scope.createPartialPipeline = function (currentElement, recommendationConfig) {
        var pipelinePart = new objectProvider.Pipeline();
        addElementToPartialPipeline(currentElement, pipelinePart, recommendationConfig);
        return pipelinePart;
    }

    function addElementToPartialPipeline(element, pipelinePart, recommendationConfig) {
        pipelinePart.addElement(element);
        // add all children of pipeline element that are not already present in the pipeline
        if (!recommendationConfig) {
            var outgoingConnections = jsPlumb.getConnections({source: element});
            if (outgoingConnections.length > 0) {
                for (var j = 0, ocon; ocon = outgoingConnections[j]; j++) {
                    if (!pipelinePart.hasElement(ocon.target.id)) {
                        addElementToPartialPipeline(ocon.target, pipelinePart, recommendationConfig);
                    }
                }
            }
        }

        // add all parents of pipeline element
        var connections = jsPlumb.getConnections({target: element});
        if (connections.length > 0) {
            for (var i = 0, con; con = connections[i]; i++) {
                addElementToPartialPipeline(con.source, pipelinePart);
            }
        }
    }

    /**
     * Sends the pipeline to the server
     */
    $scope.submit = function () {
        var error = false;
        var pipelineNew = new objectProvider.Pipeline();
        var streamPresent = false;
        var sepaPresent = false;
        var actionPresent = false;


        $('#assembly').find('.connectable, .connectable-block').each(function (i, element) {
            var $element = $(element);

            if (!pipelineEditorService.isConnected(element, jsPlumb)) {
                error = true;
                showToast("error", "All elements must be connected", "Submit Error");
            }

            if ($element.hasClass('sepa')) {
                sepaPresent = true;
                if ($element.data("options")) {
                    pipelineNew.addElement(element);

                } else if ($element.data("JSON").staticProperties != null) {
                    showToast("error", "Please enter parameters for transparent elements (Right click -> Customize)", "Submit Error");
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
                    showToast("error", "Please enter parameters for transparent elements (Right click -> Customize)", "Submit Error");
                    ;
                    error = true;
                }
            }
        });
        if (!streamPresent) {
            showToast("error", "No stream element present in pipeline", "Submit Error");
            error = true;
        }

        if (!actionPresent) {
            showToast("error", "No action element present in pipeline", "Submit Error");
            error = true;
        }
        if (!error) {
            $rootScope.state.currentPipeline = pipelineNew;
            if ($rootScope.state.adjustingPipelineState) {
                $rootScope.state.currentPipeline.name = $scope.currentPipelineName;
                $rootScope.state.currentPipeline.description = $scope.currentPipelineDescription;
            }

            openPipelineNameModal();
        }
    }

    function openPipelineNameModal() {
        if ($rootScope.state.adjustingPipelineState) {
            $scope.modifyPipelineMode = true;
        }
        $scope.showSavePipelineDialog();
    }

    $scope.createAssemblyElement = function (json, $parentElement) {
        var x = $parentElement.position().left;
        var y = $parentElement.position().top;
        var coord = {'x': x + 200, 'y': y};
        var $target;
        var $createdElement = jsplumbService.createNewAssemblyElement(jsPlumb, json, coord, false, "#assembly");
        if (json.belongsTo.indexOf("sepa") > 0) { //Sepa Element
            $target = jsplumbService.sepaDropped($scope, jsPlumb, $createdElement, true);
        } else {
            $target = jsplumbService.actionDropped($scope, jsPlumb, $createdElement, true);
        }

        var options;
        if ($parentElement.hasClass("stream")) {
            options = jsplumbConfig.streamEndpointOptions;
        } else {
            options = jsplumbConfig.sepaEndpointOptions;
        }
        var sourceEndPoint;
        if (jsPlumb.selectEndpoints({source: $parentElement}).length > 0) {

            if (!(jsPlumb.selectEndpoints({source: $parentElement}).get(0).isFull())) {
                sourceEndPoint = jsPlumb.selectEndpoints({source: $parentElement}).get(0)
            } else {
                sourceEndPoint = jsPlumb.addEndpoint($parentElement, options);
            }
        } else {
            sourceEndPoint = jsPlumb.addEndpoint($parentElement, options);
        }

        var targetEndPoint = jsPlumb.selectEndpoints({target: $target}).get(0);

        jsPlumb.connect({source: sourceEndPoint, target: targetEndPoint, detachable: true});
        jsPlumb.repaintEverything();
    }

    $scope.createAndConnect = function (target) {
        var json = $("a", $(target)).data("recObject").json;
        var $parentElement = $(target).parents(".connectable");
        $scope.createAssemblyElement(json, $parentElement);
    }

    $scope.clearCurrentElement = function () {
        $rootScope.state.currentElement = null;
    };

    $scope.handleDeleteOption = function ($element) {
        jsPlumb.removeAllEndpoints($element);
        $element.remove();
    }

    function modifyPipeline(pipelineModifications) {
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

    function showToast(type, title, description) {
        $mdToast.show(
            $mdToast.simple()
                .textContent(title)
                .position("top right")
                .hideDelay(3000)
        );
    }

};
