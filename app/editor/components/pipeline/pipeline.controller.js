export class PipelineController {

    constructor($timeout, $rootScope, JsplumbService, PipelineEditorService, JsplumbBridge, ImageChecker, InitTooltips, jsplumbConfigService, ObjectProvider, DialogBuilder, EditorDialogManager) {
        this.JsplumbBridge = JsplumbBridge;
        this.jsplumbService = JsplumbService;
        this.pipelineEditorService = PipelineEditorService;
        this.ImageChecker = ImageChecker;
        this.InitTooltips = InitTooltips;
        this.$rootScope = $rootScope;
        this.$timeout = $timeout;
        this.jsplumbConfigService = jsplumbConfigService;
        this.objectProvider = ObjectProvider;
        this.DialogBuilder = DialogBuilder;
        this.EditorDialogManager = EditorDialogManager;
        this.currentMouseOverElement = "";

        this.currentPipelineModel = {};
        this.idCounter = 0;

        this.currentZoomLevel = 1;

        this.$timeout(() => {
            JsplumbBridge.setContainer("assembly");
            this.initAssembly();
            this.initPlumb();
        });

    }

    updateMouseover(elementId) {
        this.currentMouseOverElement = elementId;
    }

    getElementCss(currentPipelineElementSettings) {
        return "position:absolute;"
        + "left: " +currentPipelineElementSettings.position.x +"px; "
        + "top: " +currentPipelineElementSettings.position.y +"px; "
    }

    getElementCssClasses(currentPipelineElementSettings) {
        return currentPipelineElementSettings.openCustomize ? "a " : ""
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
                    var pipelineElementConfig = this.jsplumbService.createNewPipelineElementConfig(ui.draggable.data("JSON"), this.pipelineEditorService.getCoordinates(ui, this.currentZoomLevel), false);
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

        var streamDescription = pipelineElementConfig.payload
        if (streamDescription
                .eventGrounding
                .transportProtocols[0]
                .properties.topicDefinition
                .type === "org.streampipes.model.grounding.WildcardTopicDefinition") {
            this.EditorDialogManager.showCustomizeStreamDialog(streamDescription);
        }
    }

    handleDeleteOption(internalId) {
        angular.forEach(this.pipelineModel, (pe, index) => {
           if (pe.payload.DOM == internalId) {
               this.pipelineModel.splice(index, 1);
           }
        });
        this.JsplumbBridge.removeAllEndpoints(internalId);
    }

    //TODO ANGULARIZE
    //Initiate assembly and jsPlumb functionality-------
    initPlumb() {
        this.$rootScope.state.plumbReady = true;

        this.jsplumbService.prepareJsplumb();

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
                this.currentPipelineModel = this.objectProvider.makePipeline(info.target, this.pipelineModel, info.target.id );
                console.log(this.currentPipelineModel);
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
                                        this.EditorDialogManager.showCustomizeDialog($(id), sourceEndpoint, sepa.payload);
                                    }
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

PipelineController.$inject = ['$timeout', '$rootScope', 'JsplumbService', 'PipelineEditorService', 'JsplumbBridge', 'ImageChecker', 'InitTooltips', 'jsplumbConfigService', 'ObjectProvider', 'DialogBuilder', 'EditorDialogManager']