export class JsplumbService {

    constructor($http, $rootScope, pipelineElementIconService, ObjectProvider, apiConstants, $compile, jsplumbConfigService, JsplumbBridge) {
        this.$http = $http;
        this.$rootScope = $rootScope;
        this.pipelineElementIconService = pipelineElementIconService;
        this.objectProvider = ObjectProvider;
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

        var displaySettings = isPreview ? 'connectable-preview' : 'connectable-editor';
        var connectable = block ? "" : "connectable";

        return {
            type: json.type, settings: {
                openCustomize: typeof json.DOM !== "undefined",
                preview: isPreview,
                displaySettings: displaySettings,
                connectable: connectable,
                position: {
                    x: coordinates.x,
                    y: coordinates.y
                }
            }, payload: angular.copy(json)
        };
    }

    // loadOptionsButtons(scope, $newElement) {
    //     scope.currentPipelineElement = $newElement.data("JSON");
    //     scope.currentPipelineElementDom = $newElement[0].id;
    //     var elementId = scope.currentPipelineElement.type == 'stream' ? scope.currentPipelineElement.elementId : scope.currentPipelineElement.belongsTo;
    //     $newElement.append(this.$compile('<pipeline-element-options show-customize-stream-dialog-function=showCustomizeStreamDialog show-customize-dialog-function=showCustomizeDialog delete-function=handleDeleteOption create-partial-pipeline-function=createPartialPipeline create-function=createAssemblyElement all-elements=allElements pipeline-element-id=' + elementId + ' internal-id=' + scope.currentPipelineElementDom + '></pipeline-element-options>')(scope));
    // }

    streamDropped($newElement, json, endpoints, preview) {
        var jsplumbConfig = this.getJsplumbConfig(preview);
        if (endpoints) {
            this.JsplumbBridge.draggable($newElement, {containment: 'parent'});
            this.JsplumbBridge.addEndpoint($newElement, jsplumbConfig.streamEndpointOptions);
        }
    };

    sepaDropped($newElement, json, endpoints, preview) {
        var jsplumbConfig = this.getJsplumbConfig(preview);
        this.JsplumbBridge.draggable($newElement, {containment: 'parent'});
        if (endpoints) {
            if (json.inputStreams.length < 2) { //1 InputNode
                this.JsplumbBridge.addEndpoint($newElement, jsplumbConfig.leftTargetPointOptions);
            } else {
                this.JsplumbBridge.addEndpoint($newElement, this.getNewTargetPoint(0, 0.25));

                this.JsplumbBridge.addEndpoint($newElement, this.getNewTargetPoint(0, 0.75));
            }
            this.JsplumbBridge.addEndpoint($newElement, jsplumbConfig.sepaEndpointOptions);
        }
    };

    actionDropped($newElement, json, endpoints, preview) {
        var jsplumbConfig = this.getJsplumbConfig(preview);
        this.JsplumbBridge.draggable($newElement, {containment: 'parent'});
        if (endpoints) {
            this.JsplumbBridge.addEndpoint($newElement, jsplumbConfig.leftTargetPointOptions);
        }
    };

    getJsplumbConfig(preview) {
        return preview ? this.jsplumbConfigService.getPreviewConfig() : this.jsplumbConfigService.getEditorConfig();
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

JsplumbService.$inject = ['$http', '$rootScope', 'pipelineElementIconService', 'ObjectProvider', 'apiConstants', '$compile', 'jsplumbConfigService', 'JsplumbBridge'];
