import {CustomizeController} from '../../customize.controller';
import MatchingErrorController from '../../matching-error.controller';

export class PipelineController {

    constructor($scope, $element, $timeout, $rootScope, JsplumbService, PipelineEditorService, JsplumbBridge, ImageChecker, InitTooltips, jsplumbConfigService, ObjectProvider, $mdDialog) {
        this.$scope = $scope;
        this.JsplumbBridge = JsplumbBridge;
        this.jsplumbService = JsplumbService;
        this.pipelineEditorService = PipelineEditorService;
        this.ImageChecker = ImageChecker;
        this.InitTooltips = InitTooltips;
        this.$rootScope = $rootScope;
        this.$element = $element;
        this.$timeout = $timeout;
        this.jsplumbConfigService = jsplumbConfigService;
        this.objectProvider = ObjectProvider;
        this.$mdDialog = $mdDialog;

        this.currentPipelineModel = {};
        this.idCounter = 0;

        this.currentZoomLevel = 1;

        this.$timeout(() => {
            JsplumbBridge.setContainer("assembly");
            this.initAssembly();
            this.initPlumb();
        });

    }

    getElementCss(currentPipelineElementSettings) {
        return "position:absolute;"
        + "left: " +currentPipelineElementSettings.position.x +"px; "
        + "top: " +currentPipelineElementSettings.position.y +"px; "
    }

    getElementCssClasses(currentPipelineElementSettings) {
        return  currentPipelineElementSettings.openCustomize ? "a " : ""
            +currentPipelineElementSettings.connectable +" "
            +currentPipelineElementSettings.displaySettings;
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
                    var pipelineElementConfig = this.jsplumbService.createNewAssemblyElement(ui.draggable.data("JSON"), this.pipelineEditorService.getCoordinates(ui, this.currentZoomLevel), "", "", false);
                    pipelineElementConfig.payload.DOM = "jsplumb_" +this.idCounter;
                    this.idCounter++;

                    this.pipelineModel.push(pipelineElementConfig);

                    //Droppable Streams
                    if (ui.draggable.hasClass('stream')) {
                        this.checkTopicModel(pipelineElementConfig);
                        //Droppable Sepas
                    } else if (ui.draggable.hasClass('sepa')) {
                        this.$timeout(() => {
                            this.$timeout(() => {
                                this.jsplumbService.sepaDropped(pipelineElementConfig.payload.DOM, pipelineElementConfig.payload, true, false);
                            });
                        });
                        //Droppable Actions
                    } else if (ui.draggable.hasClass('action')) {
                        this.$timeout(() => {
                            this.$timeout(() => {
                                this.jsplumbService.actionDropped(pipelineElementConfig.payload.DOM, pipelineElementConfig.payload, true, false);
                            });
                        });
                    }
                    //this.InitTooltips.initTooltips();
                }
                this.JsplumbBridge.repaintEverything();
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
            $('.circleMenu-open').circleMenu('close');
        });

        if (typeof this.currentModifiedPipeline != 'undefined') {
            this.$rootScope.state.adjustingPipelineState = true;
            this.displayPipelineById();
        }

    };


    checkTopicModel(pipelineElementConfig) {
        this.$timeout(() => {
            this.$timeout(() => {
                this.jsplumbService.streamDropped(pipelineElementConfig.payload.DOM, pipelineElementConfig.payload, true, false);
            });
        });

        //this.loadOptionsButtons(this, state);
        var streamDescription = pipelineElementConfig.payload
        if (streamDescription
                .eventGrounding
                .transportProtocols[0]
                .properties.topicDefinition
                .type === "org.streampipes.model.grounding.WildcardTopicDefinition") {
            //this.showCustomizeStreamDialog(state);
        }
    }

    loadOptionsButtons(scope, $newElement) {
        this.currentPipelineElement = $newElement.data("JSON");
        this.currentPipelineElementDom = $newElement[0].id;
        var elementId = this.currentPipelineElement.type == 'stream' ? this.currentPipelineElement.elementId : this.currentPipelineElement.belongsTo;
        $newElement.append(this.$compile('<pipeline-element-options show-customize-stream-dialog-function=ctrl.showCustomizeStreamDialog show-customize-dialog-function=ctrl.showCustomizeDialog delete-function=ctrl.handleDeleteOption create-partial-pipeline-function=ctrl.createPartialPipeline create-function=ctrl.createAssemblyElement all-elements=ctrl.allElements pipeline-element-id=' + elementId + ' internal-id=' + this.currentPipelineElementDom + '></pipeline-element-options>')(this.$scope));
    }

    showCustomizeStreamDialog(state) {
        var dialogContent = this.getDialogTemplate(TopicSelectionController, 'app/editor/components/topic/topic-selection-modal.tmpl.html');
        dialogContent.locals = {
            state: state
        }
        this.$mdDialog.show(dialogContent);
    }

    handleDeleteOption($element, internalId) {
        angular.forEach(this.pipelineModel, (pe, index) => {
           if (pe.payload.DOM == internalId) {
               this.pipelineModel.splice(index, 1);
           }
        });
        this.JsplumbBridge.removeAllEndpoints($element);
        $element.remove();
    }

    showMatchingErrorDialog(elementData) {
        var dialogContent = this.getDialogTemplate(MatchingErrorController, 'app/editor/components/matchingErrorDialog.tmpl.html');
        dialogContent.locals = {
            elementData: elementData
        }
        this.$mdDialog.show(dialogContent);
    }

    showCustomizeDialog(elementData, sepaName, sourceEndpoint, sepa) {
        var dialogContent = this.getDialogTemplate(CustomizeController, 'app/editor/components/customizeElementDialog.tmpl.html');
        dialogContent.locals = {
            elementData: elementData,
            sepaName: sepaName,
            sourceEndpoint: sourceEndpoint,
            sepa: sepa
        }
        this.$mdDialog.show(dialogContent);
    };

    getDialogTemplate(controller, templateUrl) {
        return {
            controller: controller,
            controllerAs: "ctrl",
            bindToController: true,
            templateUrl: templateUrl,
            parent: angular.element(document.body),
            clickOutsideToClose: true,
            //scope: this.$scope,
            //rootScope: this.$rootScope,
            //preserveScope: true
        }
    }

    getDomElement(internalId) {

    }

    waitForRenderingFinished(internalId, pipelineElementConfig) {
        this.$timeout(() => {
            this.jsplumbService.streamDropped(pipelineElementConfig.DOM, pipelineElementConfig, true, false);
        });
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
                this.currentPipelineModel = this.createPartialPipeline(info.target, false);
                this.objectProvider.updatePipeline(this.currentPipelineModel)
                    .success(data => {
                        if (data.success) {
                            info.targetEndpoint.setType("token");
                            this.modifyPipeline(data.pipelineModifications);
                            for (var i = 0, sepa; sepa = this.pipelineModel[i]; i++) {
                                var id = "#" + sepa.payload.DOM;
                                if ($(id).length > 0) {
                                    if (sepa.payload.configured != true) {
                                        // if (!this.pipelineEditorService.isFullyConnected(id)) {
                                        //     return;
                                        // }
                                        var sourceEndpoint = this.JsplumbBridge.selectEndpoints({element: info.targetEndpoint.elementId});
                                        this.showCustomizeDialog($(id), sepa.name, sourceEndpoint, sepa.payload);
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

    createPartialPipeline(currentElement, recommendationConfig) {
        var pipelinePart = this.objectProvider.makePipeline(currentElement, this.pipelineModel);
        //this.addElementToPartialPipeline(currentElement, pipelinePart, recommendationConfig);
        return pipelinePart;
    }

    addElementToPartialPipeline(element, pipelinePart, recommendationConfig) {
        pipelinePart.addElement(element, this.pipelineModel);
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

    modifyPipeline(pipelineModifications) {
        for (var i = 0, modification; modification = pipelineModifications[i]; i++) {
            var id = modification.domId;
            if (id !== "undefined") {
                var pe = this.objectProvider.findElement(id, this.pipelineModel);
                pe.payload.staticProperties = modification.staticProperties;
                pe.payload.outputStrategies = modification.outputStrategies;
                pe.payload.inputStreams = modification.inputStreams;
            }
        }
    }
}

PipelineController.$inject = ['$scope', '$element', '$timeout', '$rootScope', 'JsplumbService', 'PipelineEditorService', 'JsplumbBridge', 'ImageChecker', 'InitTooltips', 'jsplumbConfigService', 'ObjectProvider', '$mdDialog']