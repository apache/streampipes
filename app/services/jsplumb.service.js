export class JsplumbService {

    constructor($http, $rootScope, pipelineElementIconService, objectProvider, apiConstants, $compile, jsplumbConfigService, JsplumbBridge) {
        this.$http = $http;
        this.$rootScope = $rootScope;
        this.pipelineElementIconService = pipelineElementIconService;
        this.objectProvider = objectProvider;
        this.apiConstants = apiConstants;
        this.$compile = $compile;
        this.jsplumbConfigService = jsplumbConfigService;
        this.JsplumbBridge = JsplumbBridge;
    }

    prepareJsplumb() {
        this.JsplumbBridge.registerEndpointTypes({
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
    }

    createNewAssemblyElement(json, coordinates, block, target, isPreview) {
        var $newState = $('<span>')
            .data("JSON", $.extend(true, {}, json))
            .appendTo(target);
        if (typeof json.DOM !== "undefined") { //TODO TESTTEST
            $newState.attr("id", json.DOM);
            $newState.addClass('a'); //Flag so customize modal won't get triggered
        }

        $newState
            .css({'position': 'absolute', 'top': coordinates.y, 'left': coordinates.x});

        if (isPreview) {
            $newState.addClass('connectable-preview');
        } else {
            $newState.addClass('connectable-editor');
            this.JsplumbBridge.draggable($newState, {containment: 'parent'});
        }

        if (!block) {
            this.pipelineElementIconService.addImageOrTextIcon($newState, json, false, 'connectable');
        }

        return $newState;
    }

    loadOptionsButtons(scope, $newElement) {
        scope.currentPipelineElement = $newElement.data("JSON");
        scope.currentPipelineElementDom = $newElement[0].id;
        var elementId = scope.currentPipelineElement.type == 'stream' ? scope.currentPipelineElement.elementId : scope.currentPipelineElement.belongsTo;
        $newElement.append(this.$compile('<pipeline-element-options show-customize-stream-dialog-function=showCustomizeStreamDialog show-customize-dialog-function=showCustomizeDialog delete-function=handleDeleteOption create-partial-pipeline-function=createPartialPipeline create-function=createAssemblyElement all-elements=allElements pipeline-element-id=' + elementId + ' internal-id=' + scope.currentPipelineElementDom + '></pipeline-element-options>')(scope));
    }

    streamDropped(scope, $newElement, endpoints, preview) {
        var jsplumbConfig = this.getJsplumbConfig(preview);
        scope.isStreamInAssembly = true;
        $newElement.addClass("connectable stream");
        $newElement.id = "sp_stream_" + (this.$rootScope.state.currentPipeline.streams.length + 1);
        var pipelinePart = new this.objectProvider.Pipeline();
        pipelinePart.addElement($newElement);
        this.$rootScope.state.currentPipeline = pipelinePart;
        if (endpoints) {
            this.JsplumbBridge.addEndpoint($newElement, jsplumbConfig.streamEndpointOptions);
        }
        if (!preview) {
            this.loadOptionsButtons(scope, $newElement);
        }
        return $newElement;
    };

    sepaDropped(scope,  $newElement, endpoints, preview) {
        var jsplumbConfig = this.getJsplumbConfig(preview);
        scope.isSepaInAssembly = true;
        $newElement.addClass("connectable sepa");
        if ($newElement.data("JSON").staticProperties != null && !this.$rootScope.state.adjustingPipelineState && !$newElement.data("options")) {
            $newElement
                .addClass('disabled');
        }


        if (endpoints) {
            if ($newElement.data("JSON").inputStreams.length < 2) { //1 InputNode
                //jsPlumb.addEndpoint($newElement, apiConstants.leftTargetPointOptions);
                this.JsplumbBridge.addEndpoint($newElement, jsplumbConfig.leftTargetPointOptions);
            } else {
                this.JsplumbBridge.addEndpoint($newElement, this.getNewTargetPoint(0, 0.25));

                this.JsplumbBridge.addEndpoint($newElement, this.getNewTargetPoint(0, 0.75));
            }
            this.JsplumbBridge.addEndpoint($newElement, jsplumbConfig.sepaEndpointOptions);
        }
        if (!preview) this.loadOptionsButtons(scope, $newElement);
        return $newElement;
    };

    actionDropped (scope, $newElement, endpoints, preview) {
        var jsplumbConfig = this.getJsplumbConfig(preview);
        scope.isActionInAssembly = true;
        $newElement
            .addClass("connectable action");
        if ($newElement.data("JSON").staticProperties != null && !$rootScope.state.adjustingPipelineState) {
            $newElement
                .addClass('disabled');
        }
        if (endpoints) {
            this.JsplumbBridge.addEndpoint($newElement, jsplumbConfig.leftTargetPointOptions);
        }
        if (!preview) this.loadOptionsButtons(scope, $newElement);
        return $newElement;
    };

    getJsplumbConfig(preview) {
        return  preview ? this.jsplumbConfigService.getPreviewConfig() : this.jsplumbConfigService.getEditorConfig();
    }

    getNewTargetPoint(x, y) {
        return {
            endpoint: ["Dot", {radius: 12}],
            type: "empty",
            anchor: [x, y, -1, 0],
            isTarget: true
        };
    }
}

JsplumbService.$inject = ['$http', '$rootScope', 'pipelineElementIconService', 'objectProvider', 'apiConstants', '$compile', 'jsplumbConfigService', 'JsplumbBridge'];
