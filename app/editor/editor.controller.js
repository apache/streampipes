//import jQueryUi from 'npm/jquery-ui';

import CustomizeController from './customize.controller';
import MatchingErrorController from './matching-error.controller';
import SavePipelineController from './save-pipeline.controller';
import HelpDialogController from './directives/pipeline-element-options/help-dialog.controller';

EditorCtrl.$inject = ['$scope', '$rootScope', '$state', '$timeout', '$http', 'restApi', '$stateParams', 'objectProvider', 'apiConstants', '$q', '$mdDialog', '$window', '$compile', 'imageChecker', 'getElementIconText', 'initTooltips', '$mdToast'];


export default function EditorCtrl($scope, $rootScope, $state, $timeout, $http, restApi, $stateParams, objectProvider, apiConstants, $q, $mdDialog, $window, $compile, imageChecker, getElementIconText, initTooltips, $mdToast) {

    $scope.standardUrl = "http://localhost:8080/semantic-epa-backend/api/";
    $scope.isStreamInAssembly = false;
    $scope.isSepaInAssembly = false;
    $scope.isActionInAssembly = false;
    $scope.currentElements = [];
    $scope.allElements = {};
    $scope.currentModifiedPipeline = $stateParams.pipeline;
    $scope.possibleElements = [];
    $scope.activePossibleElementFilter = {};
    $scope.selectedTab = 1;
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

    if ($rootScope.email != undefined) {
        restApi
            .getUserDetails()
            .success(function (user) {
                if (!user.hideTutorial || user.hideTutorial == undefined) {
                    var confirm = $mdDialog.confirm()
                        .title('Welcome to StreamPipes!')
                        .textContent('We have a short tutorial that guides you through your first steps with StreamPipes.')
                        .ok('Show tutorial')
                        .cancel('Cancel');

                    $mdDialog.show(confirm).then(function () {
                        user.hideTutorial = true;
                        restApi.updateUserDetails(user).success(function (data) {
                            $state.go("streampipes.tutorial");
                        });
                    }, function () {

                    });
                }
            })
            .error(function (msg) {
                console.log(msg);
            });
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
        console.log("show");
        $mdDialog.show({
            controller: HelpDialogController,
            templateUrl: 'app/editor/directives/pipeline-element-options/help-dialog.tmpl.html',
            parent: angular.element(document.body),
            clickOutsideToClose: true,
            scope: $scope,
            preserveScope: true,
            locals: {
                pipelineElement: element,
            }
        })
    };

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
        $mdDialog.show({
            controller: SavePipelineController,
            templateUrl: 'app/editor/directives/submitPipelineModal.tmpl.html',
            parent: angular.element(document.body),
            clickOutsideToClose: true,
            scope: $scope,
            rootScope: $rootScope,
            preserveScope: true,

        })
    }

    $scope.showMatchingErrorDialog = function (elementData) {
        $mdDialog.show({
            controller: MatchingErrorController,
            templateUrl: 'app/editor/directives/matchingErrorDialog.tmpl.html',
            parent: angular.element(document.body),
            clickOutsideToClose: true,
            scope: $scope,
            rootScope: $rootScope,
            preserveScope: true,
            locals: {
                elementData: elementData,
            }
        })
    }

    $scope.showCustomizeDialog = function (elementData, sepaName, sourceEndpoint) {
        $rootScope.state.currentElement = elementData;
        $mdDialog.show({
            controller: CustomizeController,
            templateUrl: 'app/editor/directives/customizeElementDialog.tmpl.html',
            parent: angular.element(document.body),
            clickOutsideToClose: true,
            scope: $scope,
            rootScope: $rootScope,
            preserveScope: true,
            locals: {
                elementData: elementData,
                sepaName: sepaName,
                sourceEndpoint: sourceEndpoint
            }
        })
    };

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
        bindContextMenu();
        //initTooltips();
    });
    $scope.openContextMenu = function ($mdOpenMenu, event) {
        $mdOpenMenu(event.$event);
        alert("open context menu");
    };

    $scope.getOwnBlocks = function () {
        return restApi.getBlocks();           //TODO anpassen
    };


    $scope.loadCurrentElements = function (type) {

        $scope.currentElements = [];
        if (type == 'block') {
            $scope.loadOptions("block");
            $scope.currentElements = $scope.allElements["block"];
        } else if (type == 'stream') {
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
                $scope.displayPipeline(pipeline);

            })
            .error(function (msg) {
                console.log(msg);
            });

    };

    $scope.displayPipeline = function (pipeline) {
        var currentx = 50;
        var currenty = 50;
        for (var i = 0, stream; stream = pipeline.streams[i]; i++) {
            $scope.streamDropped(createNewAssemblyElement(stream, {'x': currentx, 'y': currenty}));
            currenty += 200;
        }
        currenty = 50;
        for (var i = 0, sepa; sepa = pipeline.sepas[i]; i++) {
            currentx += 200;
            var $sepa = $scope.sepaDropped(createNewAssemblyElement(sepa, {'x': currentx, 'y': currenty})
                .data("options", true));
            if (jsPlumb.getConnections({source: sepa.DOM}).length == 0) { //Output Element
                jsPlumb.addEndpoint($sepa, apiConstants.sepaEndpointOptions);
            }
        }
        currentx += 200;
        for (var i = 0, action; action = pipeline.actions[i]; i++) {
            var $action = $scope.actionDropped(createNewAssemblyElement(action, {'x': currentx, 'y': currenty})
                .data("options", true));
            currenty += 200;
            jsPlumb.addEndpoint($action, apiConstants.leftTargetPointOptions);
        }

        connectPipelineElements(pipeline, true);
        jsPlumb.repaintEverything();

        $scope.currentPipelineName = pipeline.name;
        $scope.currentPipelineDescription = pipeline.description;
    };

    function bindContextMenu() {
        $(".draggable-icon").off("contextmenu").on("contextmenu", staticContextMenu);
    }

    function connectPipelineElements(json, detachable) {
        var source, target;
        var sourceEndpoint;
        var targetEndpoint

        jsPlumb.setSuspendDrawing(true);

        //Sepas --> Streams / Sepas --> Sepas---------------------//
        for (var i = 0, sepa; sepa = json.sepas[i]; i++) {
            for (var j = 0, connection; connection = sepa.connectedTo[j]; j++) {

                source = connection;
                target = sepa.DOM;


                var options;
                var id = "#" + source;
                if ($(id).hasClass("sepa")) {
                    options = apiConstants.sepaEndpointOptions;
                } else {
                    options = apiConstants.streamEndpointOptions;
                }

                sourceEndpoint = jsPlumb.addEndpoint(source, options);
                targetEndpoint = jsPlumb.addEndpoint(target, apiConstants.leftTargetPointOptions);
                jsPlumb.connect({source: sourceEndpoint, target: targetEndpoint, detachable: detachable});
            }
        }
        for (var i = 0, action; action = json.actions[i]; i++) {
            //Action --> Sepas----------------------//
            target = action.DOM;

            for (var j = 0, connection; connection = action.connectedTo[j]; j++) {
                source = connection;

                sourceEndpoint = jsPlumb.addEndpoint(source, apiConstants.sepaEndpointOptions);
                targetEndpoint = jsPlumb.addEndpoint(target, apiConstants.leftTargetPointOptions);
                jsPlumb.connect({source: sourceEndpoint, target: targetEndpoint, detachable: detachable});
            }
        }
        jsPlumb.setSuspendDrawing(false, true);
    }

    $scope.tabs = [

        {
            title: 'Blocks',
            type: 'block',
        },
        {
            title: 'Data Streams',
            type: 'stream',
        },
        {
            title: 'Processing Elements',
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
                .then(function (result) {
                    $scope.options = result.data;
                    angular.forEach($scope.options, function (o) {
                        $scope.selectedOptions.push(o.type);
                    });
                }, function (error) {
                    $scope.options = [];
                    console.log(error);
                });
        } else if (type == 'sepa') {
            restApi.getEpaCategories()
                .then(function (result) {
                    $scope.options = result.data;
                    angular.forEach($scope.options, function (o) {
                        $scope.selectedOptions.push(o.type);
                    });
                }, function (error) {
                    $scope.options = [];
                    console.log(error);
                });
        } else if (type == 'action') {
            restApi.getEcCategories()
                .then(function (result) {
                    $scope.options = result.data;
                    angular.forEach($scope.options, function (o) {
                        $scope.selectedOptions.push(o.type);
                    });
                }, function (error) {
                    $scope.options = [];
                    console.log(error);
                });
        }

    };

    $scope.loadBlocks = function () {
        restApi.getBlocks().then(function (data) {
            data.data.forEach(function (block, i, blocks) {
                block.type = "block";
            });
            $scope.allElements["block"] = data.data;
        });
    };

    $scope.loadSources = function () {
        var tempStreams = [];
        restApi.getOwnSources()
            .then(function (sources) {
                sources.data.forEach(function (source, i, sources) {
                    source.eventStreams.forEach(function (stream) {
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
            .error(function (msg) {
                console.log(msg);
            });
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

    $scope.loadBlocks();
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
        $('.block').draggable({
            revert: 'invalid',
            helper: 'clone',
            stack: '.block',
            start: function (el, ui) {
                ui.helper.appendTo('#content');
                $('#assemblyArea').css('border-color', '3px dashed rgb(255,64,129)');
            },
            stop: function (el, ui) {
                $('#assemblyArea').css('border', '1px solid rgb(63,81,181)');
            }
        });
    };

    $scope.blockDropped = function ($newElement, endpoints) {
        $scope.isStreamInAssembly = true;
        $scope.isSepaInAssembly = true;
        var data = $.extend({}, $newElement.data("JSON"));
        $newElement
            .addClass("connectable-block")
            .data("block", data)
            .append($('<div>').addClass("block-name tt").text(data.name)
                .attr({
                    "data-toggle": "tooltip",
                    "data-placement": "top",
                    "data-delay": '{"show": 100, "hide": 100}',
                    title: data.description
                })
            )
            .append($('<div>').addClass("block-img-container")
                .append($('<img>').addClass('block-img').attr("src", data.streams[0].iconUrl)));

        if (endpoints) {
            jsPlumb.addEndpoint($newElement, apiConstants.sepaEndpointOptions);
        }


    };

    var loadOptionsButtons = function ($newElement) {
        $scope.currentPipelineElement = $newElement.data("JSON");
        $scope.currentPipelineElementDom = $newElement[0].id;
        var elementId = $scope.currentPipelineElement.type == 'stream' ? $scope.currentPipelineElement.elementId : $scope.currentPipelineElement.belongsTo;
        $newElement.append($compile('<pipeline-element-options show-customize-dialog-function=showCustomizeDialog delete-function=handleDeleteOption create-partial-pipeline-function=createPartialPipeline create-function=createAssemblyElement all-elements=allElements pipeline-element-id=' + elementId + ' internal-id=' + $scope.currentPipelineElementDom + '></pipeline-element-options>')($scope));
    }

    $scope.streamDropped = function ($newElement, endpoints) {
        $scope.isStreamInAssembly = true;
        $newElement.addClass("connectable stream");
        $newElement.id = "sp_stream_" + ($rootScope.state.currentPipeline.streams.length + 1);
        var pipelinePart = new objectProvider.Pipeline();
        pipelinePart.addElement($newElement);
        $rootScope.state.currentPipeline = pipelinePart;
        if (endpoints) {
            jsPlumb.addEndpoint($newElement, apiConstants.streamEndpointOptions);
        }
        loadOptionsButtons($newElement);
        return $newElement;
    };

    $scope.sepaDropped = function ($newElement, endpoints) {
        $scope.isSepaInAssembly = true;
        $newElement.addClass("connectable sepa");
        loadOptionsButtons($newElement);
        if ($newElement.data("JSON").staticProperties != null && !$rootScope.state.adjustingPipelineState && !$newElement.data("options")) {
            $newElement
                .addClass('disabled');
        }


        if (endpoints) {
            if ($newElement.data("JSON").inputStreams.length < 2) { //1 InputNode
                //jsPlumb.addEndpoint($newElement, apiConstants.leftTargetPointOptions);
                jsPlumb.addEndpoint($newElement, apiConstants.leftTargetPointOptions);
            } else {
                jsPlumb.addEndpoint($newElement, getNewTargetPoint(0, 0.25));

                jsPlumb.addEndpoint($newElement, getNewTargetPoint(0, 0.75));
            }
            jsPlumb.addEndpoint($newElement, apiConstants.sepaEndpointOptions);
        }
        return $newElement;
    };
    $scope.actionDropped = function ($newElement, endpoints) {
        $scope.isActionInAssembly = true;
        $newElement
            .addClass("connectable action");
        loadOptionsButtons($newElement);
        if ($newElement.data("JSON").staticProperties != null && !$rootScope.state.adjustingPipelineState) {
            $newElement
                .addClass('disabled');
        }
        if (endpoints) {
            jsPlumb.addEndpoint($newElement, apiConstants.leftTargetPointOptions);
        }
        return $newElement;
    };

    var makeInternalId = function () {
        return "a" + $rootScope.state.currentPipeline.streams.length
            + $rootScope.state.currentPipeline.sepas.length
        $rootScope.state.currentPipeline.actions.length;
    }

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

        jsPlumb.registerEndpointTypes({
            "empty": {
                paintStyle: {
                    fillStyle: "white",
                    strokeStyle: "#9E9E9E",
                    lineWidth: 2
                }
            },
            "token": {
                paintStyle: {
                    fillStyle: "#BDBDBD",
                    strokeStyle: "#9E9E9E",
                    lineWidth: 2
                },
                hoverPaintStyle: {
                    fillStyle: "#BDBDBD",
                    strokeStyle: "#4CAF50",
                    lineWidth: 4
                }
            },
            "highlight": {
                paintStyle: {
                    fillStyle: "white",
                    strokeStyle: "#4CAF50",
                    lineWidth: 4
                }
            }
        });

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
                                        if (!isFullyConnected(id)) {
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
                                        if (!isFullyConnected(id)) {
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
                    .error(function (data) {
                        console.log(data);
                    });
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

                if (ui.draggable.hasClass('draggable-icon') || ui.draggable.hasClass('block')) {
                    if (ui.draggable.data("JSON") == null) {
                        alert("No JSON - Data for Dropped element");
                        return false;
                    }
                    var $newState;
                    //Neues Container Element für Icon / identicon erstellen
                    if (ui.draggable.hasClass("block")) {
                        $newState = createNewAssemblyElement(ui.draggable.data("JSON"), getCoordinates(ui), true);
                    } else {
                        $newState = createNewAssemblyElement(ui.draggable.data("JSON"), getCoordinates(ui), false);
                    }

                    //Droppable Streams
                    if (ui.draggable.hasClass('stream')) {

                        $scope.streamDropped($newState, true);

                        //initRecs(tempPipeline, $newState);
                        //$rootScope.$broadcast("StreamDropped", $newState);
                        //$newState.hover(showRecButton, hideRecButton);

                        //Droppable Sepas
                    } else if (ui.draggable.hasClass('sepa')) {
                        $scope.sepaDropped($newState, true);

                        //Droppable Actions
                    } else if (ui.draggable.hasClass('action')) {
                        $scope.actionDropped($newState, true);
                    } else if (ui.draggable.hasClass('block')) {
                        $scope.blockDropped($newState, true)
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

            if (!isConnected(element)) {
                error = true;

                showToast("error", "All elements must be connected", "Submit Error");
            }

            if ($element.hasClass('sepa')) {
                sepaPresent = true;
                if ($element.data("options")) {
                    pipelineNew.addElement(element);

                } else if ($element.data("JSON").staticProperties != null) {
                    showToast("error", "Please enter parameters for transparent elements (Right click -> Customize)", "Submit Error");
                    ;
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
            } else if ($element.hasClass('connectable-block')) {
                streamPresent = true;
                sepaPresent = true;
                pipelineNew.addElement(element);
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
        if (json.belongsTo.indexOf("sepa") > 0) { //Sepa Element
            $target = $scope.sepaDropped(createNewAssemblyElement(json, coord), true);
        } else {
            $target = $scope.actionDropped(createNewAssemblyElement(json, coord), true);
        }

        var options;
        if ($parentElement.hasClass("stream")) {
            options = apiConstants.streamEndpointOptions;
        } else {
            options = apiConstants.sepaEndpointOptions;
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

    function ContextMenuClickHandler(type) {

        if (type === "static") {
            $('#staticContextMenu').off('click').on('click', function (e) {
                $(this).hide();
                var $invokedOn = $(this).data("invokedOn");
                while ($invokedOn.parent().get(0) != $("#editor-icon-stand").get(0)) {
                    $invokedOn = $invokedOn.parent();
                }
                var json = $invokedOn.data("JSON");
                $('#description-title').text(json.name);
                $('#modal-description').text(json.description);
                $('#descrModal').modal('show');
            });
        }
    }

    $scope.handleDeleteOption = function ($element) {
        jsPlumb.removeAllEndpoints($element);
        $element.remove();
    }

    function getCoordinates(ui) {

        var newLeft = getDropPositionX(ui.helper);
        var newTop = getDropPositionY(ui.helper);
        return {
            'x': newLeft,
            'y': newTop
        };
    }

    function createNewAssemblyElement(json, coordinates, block) {

        var $newState = $('<span>')
            .data("JSON", $.extend(true, {}, json))
            .appendTo('#assembly');
        if (typeof json.DOM !== "undefined") { //TODO TESTTEST
            $newState.attr("id", json.DOM);
            $newState.addClass('a'); //Flag so customize modal won't get triggered
        }

        jsPlumb.draggable($newState, {containment: 'parent'});

        $newState
            .css({'position': 'absolute', 'top': coordinates.y, 'left': coordinates.x});

        if (!block) {
            $scope.addImageOrTextIcon($newState, json, false, 'connectable');
        }

        return $newState;
    }

    function getNewTargetPoint(x, y) {
        return {
            endpoint: ["Dot", {radius: 12}],
            type: "empty",
            anchor: [x, y, -1, 0],
            isTarget: true
        };
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

    function isConnected(element) {

        if (jsPlumb.getConnections({source: element}).length < 1 && jsPlumb.getConnections({target: element}).length < 1) {
            return false;
        }
        return true;
    }

    function isFullyConnected(element) {
        return $(element).data("JSON").inputStreams == null || jsPlumb.getConnections({target: $(element)}).length == $(element).data("JSON").inputStreams.length;
    }

    //----------------------------------------------------
    // Block Methods
    //----------------------------------------------------

    $scope.blockElements = function () {
        var blockData = createBlock();
        var block = blockData[0];
        if (block == false) {
            showToast("error", "Please enter parameters for transparent elements (Right click -> Customize)", "Block Creation Error");
            return;
        }

        if (blockData.length == 2 && blockData[1] === "on") {
            restApi.saveBlock(block)
                .then(function (successData) {
                    console.log(successData);
                }, function (errorData) {
                    console.log(errorData);
                });
        }

        //sendToServer();
        var $selectedElements = $('.ui-selected');
        var blockCoords = getMidpointOfAssemblyElements($selectedElements);
        var $block = block.getjQueryElement()
            .appendTo("#assembly")
            .css({"top": blockCoords.y, "left": blockCoords.x, "position": "absolute"});
        initTooltips();
        $('.block-name').flowtype({
            minFont: 12,
            maxFont: 25,
            fontRatio: 10
        });
        jsPlumb.draggable($block, {containment: 'parent'});

        $block.on("contextmenu", function (e) {
            $('#customize, #division ').hide();
            $('#blockButton, #division1 ').show();
            $('#blockButton').text("Revert to Pipeline");
            $('#assemblyContextMenu')
                .data("invokedOn", $(e.target))
                .show()
                .css({
                    position: "absolute",
                    left: getLeftLocation(e, "assembly"),
                    top: getTopLocation(e, "assembly")
                });
            ContextMenuClickHandler("assembly");
            return false;
        });

        //CLEANUP
        $selectedElements.each(function (i, element) {
            jsPlumb.remove(element);
        });
        //jsPlumb.remove($selectedElements);
        //$selectedElements.remove();
        //jsPlumb.deleteEveryEndpoint();
        jsPlumb.addEndpoint($block, apiConstants.sepaEndpointOptions);


    }


    function createBlock() {
        var blockData = $('#blockNameForm').serializeArray(); //TODO SAVE
        var blockPipeline = new objectProvider.Pipeline();
        $('.ui-selected').each(function () {
            var $el = $(this)
            if ($el.hasClass("sepa") && $el.data("JSON").staticProperties != null && $el.data("options")) {
                blockPipeline.addElement(this);
            } else if ($el.hasClass("stream")) {
                blockPipeline.addElement(this);
            } else {
                return false;
            }
        });
        var block = new objectProvider.Block(blockData[0].value, blockData[1].value, blockPipeline);
        var data;
        if (blockData.length = 3) {
            data = [block, blockData[2].value];
        } else {
            data = [block];
        }

        return data;

    }

    function getMidpointOfAssemblyElements($elements) {
        var maxLeft, minLeft, maxTop, minTop;

        $elements.each(function (i, element) {
            var offsetObject = $(element).position();
            if (i == 0) {
                maxLeft = offsetObject.left;
                minLeft = offsetObject.left;
                maxTop = offsetObject.top;
                minTop = offsetObject.top;
            }
            else {
                minLeft = Math.min(minLeft, offsetObject.left);
                maxLeft = Math.max(maxLeft, offsetObject.left);
                minTop = Math.min(minTop, offsetObject.top);
                maxTop = Math.max(maxTop, offsetObject.top);
            }
        });
        var midLeft = (minLeft + maxLeft) / 2;
        var midTop = (minTop + maxTop) / 2;
        return {x: midLeft, y: midTop};
    }

    /**
     * Shows the contextmenu for given element
     * @param {Object} e
     */
    function staticContextMenu(e) {
        $('#staticContextMenu').data("invokedOn", $(e.target)).show().css({
            position: "fixed",
            left: getLeftLocation(e, "static"),
            top: getTopLocation(e, "static")
        });
        ContextMenuClickHandler("static");
        return false;

    }

    function showToast(type, title, description) {
        $mdToast.show(
            $mdToast.simple()
                .textContent(title)
                .position("top right")
                .hideDelay(3000)
        );
    }


    /**
     * Gets the position of the dropped element insidy the assembly
     * @param {Object} helper
     */
    function getDropPositionY(helper) {
        var newTop;
        var helperPos = helper.offset();
        var divPos = $('#assembly').offset();
        newTop = (helperPos.top - divPos.top) + (1 - $scope.currentZoomLevel) * ((helperPos.top - divPos.top) * 2);
        return newTop;
    }

    function getDropPositionX(helper) {
        var newLeft;
        var helperPos = helper.offset();
        var divPos = $('#assembly').offset();
        newLeft = (helperPos.left - divPos.left) + (1 - $scope.currentZoomLevel) * ((helperPos.left - divPos.left) * 2);
        return newLeft;
    }

    /**
     *
     * @param {Object} e
     * @param {Object} type
     */
    function getLeftLocation(e, type) {
        if (type === "static") {
            var menuWidth = $('#staticContextMenu').width();
        } else {
            var menuWidth = $('#assemblyContextMenu').width();
        }
        var mainCoords = $('#main').position();
        var mouseWidth = e.pageX;
        var pageWidth = $(window).width();

        // opening menu would pass the side of the page
        if (mouseWidth + menuWidth > pageWidth && menuWidth < mouseWidth) {
            return mouseWidth - menuWidth;
        }
        return mouseWidth;
    }

    function getTopLocation(e, type) {

        if (type === "static") {
            var menuHeight = $('#staticContextMenu').height();
        } else {
            var menuHeight = $('#assemblyContextMenu').height();
        }

        var mouseHeight = e.pageY - $(window).scrollTop();
        var pageHeight = $(window).height();

        if (mouseHeight + menuHeight > pageHeight && menuHeight < mouseHeight) {
            return mouseHeight - menuHeight;
        }

        return mouseHeight;

    }

    $scope.addImageOrTextIcon = function ($element, json, small, type) {
        var iconUrl = "";
        if (type == 'block' && json.streams != null && typeof json.streams !== 'undefined') {
            iconUrl = json.streams[0].iconUrl;
        } else {
            iconUrl = json.iconUrl;
        }
        imageChecker.imageExists(iconUrl, function (exists) {
            if (exists) {
                var $img = $('<img>')
                    .attr("src", iconUrl)
                    .data("JSON", $.extend(true, {}, json));
                if (type == 'draggable') {
                    $img.addClass("draggable-img tt");
                } else if (type == 'connectable') {
                    $img.addClass('connectable-img tt');
                } else if (type == 'block') {
                    $img.addClass('block-img tt');
                } else if (type == 'recommended') {
                    $img.addClass('recommended-item-img tt');
                }
                $element.append($img);
            } else {
                var name = "";
                if (type == 'block' && json.streams != null && typeof json.streams !== 'undefined') {
                    name = json.streams[0].name;
                } else {
                    name = json.name;
                }
                var $span = $("<span>")
                    .text(getElementIconText(name) || "N/A")
                    .attr(
                        {
                            "data-toggle": "tooltip",
                            "data-placement": "top",
                            "data-delay": '{"show": 1000, "hide": 100}',
                            title: name
                        })
                    .data("JSON", $.extend(true, {}, json));
                if (small) {
                    $span.addClass("element-text-icon-small")
                } else {
                    $span.addClass("element-text-icon")
                }
                $element.append($span);
            }
        });
    }
};
