import * as angular from "angular";

export class PipelineController {

    $timeout: any;
    JsplumbService: any;
    PipelineEditorService: any;
    JsplumbBridge: any;
    ObjectProvider: any;
    DialogBuilder: any;
    plumbReady: any;
    objectProvider: any;
    EditorDialogManager: any;
    currentMouseOverElement: any;
    currentPipelineModel: any;
    idCounter: any;
    currentZoomLevel: any;
    canvasId: any;
    preview: any;
    rawPipelineModel: any;
    TransitionService: any;
    ShepherdService: any;

    constructor($timeout, JsplumbService, PipelineEditorService, JsplumbBridge, ObjectProvider, DialogBuilder, EditorDialogManager, TransitionService, ShepherdService) {
        this.plumbReady = false;
        this.JsplumbBridge = JsplumbBridge;
        this.JsplumbService = JsplumbService;
        this.PipelineEditorService = PipelineEditorService;
        this.$timeout = $timeout;
        this.objectProvider = ObjectProvider;
        this.DialogBuilder = DialogBuilder;
        this.EditorDialogManager = EditorDialogManager;
        this.currentMouseOverElement = "";
        this.TransitionService = TransitionService;
        this.ShepherdService = ShepherdService;

        this.currentPipelineModel = {};
        this.idCounter = 0;

        this.currentZoomLevel = 1;
    }

    $onInit() {
        this.JsplumbBridge.setContainer(this.canvasId);
        this.initAssembly();
        this.initPlumb();
    }

    $onDestroy() {
        this.JsplumbBridge.deleteEveryEndpoint();
        //this.JsplumbBridge.reset();
        this.plumbReady = false;
    }

    updateMouseover(elementId) {
        this.currentMouseOverElement = elementId;
    }

    updateOptionsClick(elementId) {
        if (this.currentMouseOverElement == elementId) {
            this.currentMouseOverElement = "";
        } else {
            this.currentMouseOverElement = elementId;
        }
    }

    getElementCss(currentPipelineElementSettings) {
        return "position:absolute;"
            + (this.preview ? "width:75px;" : "width:110px;")
            + (this.preview ? "height:75px;" : "height:110px;")
            + "left: " + currentPipelineElementSettings.position.x + "px; "
            + "top: " + currentPipelineElementSettings.position.y + "px; "
    }

    getElementCssClasses(currentPipelineElement) {
        return currentPipelineElement.type + " " + (currentPipelineElement.settings.openCustomize ? "" : "")
            + currentPipelineElement.settings.connectable + " "
            + currentPipelineElement.settings.displaySettings;
    }

    isStreamInPipeline() {
        return this.isInPipeline('stream');
    }

    isSetInPipeline() {
        return this.isInPipeline('set');
    }

    isInPipeline(type) {
        return this.rawPipelineModel.some(x => (x.type == type && !(x.settings.disabled)));
    }

    showMixedStreamAlert() {
        this.EditorDialogManager.showMixedStreamAlert();
    }

    initAssembly() {
        ($('#assembly') as any).droppable({
            tolerance: "fit",
            drop: (element, ui) => {
                if (ui.draggable.hasClass('draggable-icon')) {
                    this.TransitionService.makePipelineAssemblyEmpty(false);
                    var pipelineElementConfig = this.JsplumbService.createNewPipelineElementConfig(ui.draggable.data("JSON"), this.PipelineEditorService.getCoordinates(ui, this.currentZoomLevel), false);
                    if ((this.isStreamInPipeline() && pipelineElementConfig.type == 'set') ||
                        this.isSetInPipeline() && pipelineElementConfig.type == 'stream') {
                        this.showMixedStreamAlert();
                    } else {
                        this.rawPipelineModel.push(pipelineElementConfig);
                        if (ui.draggable.hasClass('set')) {
                            this.$timeout(() => {
                                this.$timeout(() => {
                                    this.JsplumbService.setDropped(pipelineElementConfig.payload.DOM, pipelineElementConfig.payload, true, false);
                                });
                            });
                        }
                        else if (ui.draggable.hasClass('stream')) {
                            this.checkTopicModel(pipelineElementConfig);
                        } else if (ui.draggable.hasClass('sepa')) {
                            this.$timeout(() => {
                                this.$timeout(() => {
                                    this.JsplumbService.sepaDropped(pipelineElementConfig.payload.DOM, pipelineElementConfig.payload, true, false);
                                });
                            });
                            //Droppable Actions
                        } else if (ui.draggable.hasClass('action')) {
                            this.$timeout(() => {
                                this.$timeout(() => {
                                    this.JsplumbService.actionDropped(pipelineElementConfig.payload.DOM, pipelineElementConfig.payload, true, false);
                                });
                            });
                        }
                        if (this.ShepherdService.isTourActive()) {
                            this.ShepherdService.trigger("drop-" +pipelineElementConfig.type);
                        }
                    }
                }
                this.JsplumbBridge.repaintEverything();
            }

        }); //End #assembly.droppable()
        ($("#assembly") as any)
            .selectable({
                selected: function (event, ui) {
                },
                filter: ".connectable.stream,.connectable.sepa:not('.disabled')",
                delay: 150

            })
    }
    ;

    checkTopicModel(pipelineElementConfig) {
        this.$timeout(() => {
            this.$timeout(() => {
                this.JsplumbService.streamDropped(pipelineElementConfig.payload.DOM, pipelineElementConfig.payload, true, false);
            });
        });

        var streamDescription = pipelineElementConfig.payload;
        if (streamDescription
                .eventGrounding
                .transportProtocols[0]
                .properties.topicDefinition
                .type === "org.streampipes.model.grounding.WildcardTopicDefinition") {
            this.EditorDialogManager.showCustomizeStreamDialog(streamDescription);
        }
    }

    handleDeleteOption(pipelineElement) {
        this.JsplumbBridge.removeAllEndpoints(pipelineElement.payload.DOM);
        //this.rawPipelineModel = this.rawPipelineModel.filter(item => !(item.payload.DOM == internalId));
        angular.forEach(this.rawPipelineModel, pe => {
           if (pe.payload.DOM == pipelineElement.payload.DOM) {
               pe.settings.disabled = true;
           }
        });
        if (this.rawPipelineModel.every(pe => pe.settings.disabled)) {
            this.TransitionService.makePipelineAssemblyEmpty(true);
        }
        this.JsplumbBridge.repaintEverything();
    }

    initPlumb() {

        this.JsplumbService.prepareJsplumb();

        this.JsplumbBridge.unbind("connection");

        this.JsplumbBridge.bind("connectionDetached", (info, originalEvent) => {
            var el = ($("#" + info.targetEndpoint.elementId));
            //el.data("JSON", $.extend(true, {}, getPipelineElementContents(el.data("JSON").belongsTo)));
            el.removeClass('a');
            el.addClass('disabled');
            info.targetEndpoint.setType("empty");
        });

        this.JsplumbBridge.bind("connectionDrag", connection => {
            this.JsplumbBridge.selectEndpoints().each(function (endpoint) {
                if (endpoint.isTarget && endpoint.connections.length === 0) {
                    endpoint.setType("highlight");
                }
            });

        });
        this.JsplumbBridge.bind("connectionAborted", connection => {
            this.JsplumbBridge.selectEndpoints().each(endpoint => {
                if (endpoint.isTarget && endpoint.connections.length === 0) {
                    endpoint.setType("empty");
                }
            });
        })

        this.JsplumbBridge.bind("connection", (info, originalEvent) => {
            var pe = this.objectProvider.findElement(info.target.id, this.rawPipelineModel);
            if (pe.settings.openCustomize) {
                this.currentPipelineModel = this.objectProvider.makePipeline(this.rawPipelineModel);
                this.objectProvider.updatePipeline(this.currentPipelineModel)
                    .success(data => {
                        if (data.success) {
                            info.targetEndpoint.setType("token");
                            this.modifyPipeline(data.pipelineModifications);
                            var sourceEndpoint = this.JsplumbBridge.selectEndpoints({element: info.targetEndpoint.elementId});
                            if (this.PipelineEditorService.isFullyConnected(pe)) {
                                this.EditorDialogManager.showCustomizeDialog($("#" +pe.payload.DOM), sourceEndpoint, pe.payload);
                                if (this.ShepherdService.isTourActive()) {
                                    this.ShepherdService.trigger("customize-" +pe.type);
                                }
                            }
                        } else {
                            this.JsplumbBridge.detach(info.connection);
                            this.EditorDialogManager.showMatchingErrorDialog(data);
                        }
                    })
            }
        });

        window.onresize = (event) => {
            this.JsplumbBridge.repaintEverything();
        };

        this.$timeout(() => {
            this.plumbReady = true;
        }, 100);
    }

    modifyPipeline(pipelineModifications) {
        for (var i = 0, modification; modification = pipelineModifications[i]; i++) {
            var id = modification.domId;
            if (id !== "undefined") {
                var pe = this.objectProvider.findElement(id, this.rawPipelineModel);
                pe.payload.staticProperties = modification.staticProperties;
                pe.payload.outputStrategies = modification.outputStrategies;
                pe.payload.inputStreams = modification.inputStreams;
            }
        }
    }


}

PipelineController.$inject = ['$timeout', 'JsplumbService', 'PipelineEditorService', 'JsplumbBridge', 'ObjectProvider', 'DialogBuilder', 'EditorDialogManager', 'TransitionService', 'ShepherdService']