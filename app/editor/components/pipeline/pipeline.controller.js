export class PipelineController {

    constructor($scope, $timeout, JsplumbService, PipelineEditorService, JsplumbBridge, ObjectProvider, DialogBuilder, EditorDialogManager, RestApi, PipelinePositioningService) {
        this.$scope = $scope;
        this.plumbReady = false;
        this.JsplumbBridge = JsplumbBridge;
        this.jsplumbService = JsplumbService;
        this.pipelineEditorService = PipelineEditorService;
        this.$timeout = $timeout;
        this.objectProvider = ObjectProvider;
        this.DialogBuilder = DialogBuilder;
        this.EditorDialogManager = EditorDialogManager;
        this.RestApi = RestApi;
        this.currentMouseOverElement = "";
        this.PipelinePositioningService = PipelinePositioningService;

        this.currentPipelineModel = {};
        this.idCounter = 0;

        this.currentZoomLevel = 1;

        JsplumbBridge.setContainer("assembly");
        this.initAssembly();
        this.initPlumb();

        $scope.$on('$destroy', () => {
            JsplumbBridge.deleteEveryEndpoint();
            JsplumbBridge.reset();
            this.plumbReady = false;
        });
    }

    updateMouseover(elementId) {
        this.currentMouseOverElement = elementId;
    }

    getElementCss(currentPipelineElementSettings) {
        return "position:absolute;width:110px;height:110px;"
            + "left: " + currentPipelineElementSettings.position.x + "px; "
            + "top: " + currentPipelineElementSettings.position.y + "px; "
    }

    getElementCssClasses(currentPipelineElement) {
        return currentPipelineElement.type + " " + (currentPipelineElement.settings.openCustomize ? "" : "")
            + currentPipelineElement.settings.connectable + " "
            + currentPipelineElement.settings.displaySettings;
        return test;
    }

    initAssembly() {
        $('#assembly').droppable({
            tolerance: "fit",
            drop: (element, ui) => {

                if (ui.draggable.hasClass('draggable-icon')) {
                    var pipelineElementConfig = this.jsplumbService.createNewPipelineElementConfig(ui.draggable.data("JSON"), this.pipelineEditorService.getCoordinates(ui, this.currentZoomLevel), false);
                    this.rawPipelineModel.push(pipelineElementConfig);
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
    }
    ;

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
        angular.forEach(this.rawPipelineModel, (pe, index) => {
            if (pe.payload.DOM == internalId) {
                this.rawPipelineModel.splice(index, 1);
            }
        });
        this.JsplumbBridge.removeAllEndpoints(internalId);
    }

    initPlumb() {

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
                this.currentPipelineModel = this.objectProvider.makePipeline(this.rawPipelineModel, info.target.id);
                this.objectProvider.updatePipeline(this.currentPipelineModel)
                    .success(data => {
                        if (data.success) {
                            info.targetEndpoint.setType("token");
                            this.modifyPipeline(data.pipelineModifications);
                            for (var i = 0, sepa; sepa = this.rawPipelineModel[i]; i++) {
                                var id = "#" + sepa.payload.DOM;
                                if ($(id).length > 0) {
                                    if (sepa.payload.configured !== true) {
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

PipelineController.$inject = ['$scope', '$timeout', 'JsplumbService', 'PipelineEditorService', 'JsplumbBridge', 'ObjectProvider', 'DialogBuilder', 'EditorDialogManager', 'RestApi', 'PipelinePositioningService']