import * as angular from 'angular';

export class JsplumbService {

    objectProvider: any;
    apiConstants: any;
    $compile: any;
    JsplumbConfigService: any;
    JsplumbBridge: any;
    $timeout: any;
    idCounter: any;
    RestApi: any;

    constructor(ObjectProvider, JsplumbConfigService, JsplumbBridge, $timeout, RestApi) {
        this.objectProvider = ObjectProvider;
        this.JsplumbConfigService = JsplumbConfigService;
        this.JsplumbBridge = JsplumbBridge;
        this.$timeout = $timeout;
        this.RestApi = RestApi;

        this.idCounter = 0;
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

    makeRawPipeline(pipelineModel, isPreview) {
        return pipelineModel
            .streams
            .map(s => this.toConfig(s, "stream", isPreview))
            .concat(pipelineModel.sepas.map(s => this.toConfig(s, "sepa", isPreview)))
            .concat(pipelineModel.actions.map(s => this.toConfig(s, "action", isPreview)));
    }

    toConfig(pe, type, isPreview) {
        pe.type = type;
        pe.configured = true;
        return this.createNewPipelineElementConfig(pe, {x: 100, y: 100}, isPreview);
    }


    createElement(pipelineModel, pipelineElement, pipelineElementDomId) {
        var pipelineElementDom = $("#" + pipelineElementDomId);
        var pipelineElementConfig = this.createNewPipelineElementConfigWithFixedCoordinates(pipelineElementDom, pipelineElement, false);
        pipelineModel.push(pipelineElementConfig);
        this.$timeout(() => {
            this.createAssemblyElement(pipelineElementConfig.payload.DOM, pipelineElementConfig.payload, pipelineElementDom);
        });
    }

    createAssemblyElement($newElementId, json, $parentElement) {
        var $target;
        if (json.belongsTo.indexOf("sepa") > 0) { //Sepa Element
            $target = this.sepaDropped($newElementId, json, true, false);
            this.connectNodes($parentElement, $target);
        } else {
            $target = this.actionDropped($newElementId, json, true, false);
            this.connectNodes($parentElement, $target);
        }
    }

    connectNodes($parentElement, $target) {
        var options;
        if ($parentElement.hasClass("stream")) {
            // TODO: getJsplumbConfig depends on isPreview. Not implemented yet
            options = this.getJsplumbConfig(true).streamEndpointOptions;
        } else {
            // TODO: getJsplumbConfig depends on isPreview. Not implemented yet
            options = this.getJsplumbConfig(true).sepaEndpointOptions;
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

    createNewPipelineElementConfigWithFixedCoordinates($parentElement, json, isPreview) {
        var x = $parentElement.position().left;
        var y = $parentElement.position().top;
        var coord = {'x': x + 200, 'y': y};
        return this.createNewPipelineElementConfig(json, coord, isPreview);
    }

    createNewPipelineElementConfig(json, coordinates, isPreview) {
        var displaySettings = isPreview ? 'connectable-preview' : 'connectable-editor';
        var connectable = "connectable";
        var pipelineElementConfig = {
            type: json.type, settings: {
                openCustomize: !json.configured,
                preview: isPreview,
                displaySettings: displaySettings,
                connectable: connectable,
                position: {
                    x: coordinates.x,
                    y: coordinates.y
                }
            }, payload: angular.copy(json)
        };
        if (!pipelineElementConfig.payload.DOM) {
            pipelineElementConfig.payload.DOM = "jsplumb_" + this.idCounter;
            this.idCounter++;
        }

        return pipelineElementConfig;
    }

    streamDropped($newElement, json, endpoints, preview) {
        var jsplumbConfig = this.getJsplumbConfig(preview);
        if (endpoints) {
            if (!preview) {
                this.JsplumbBridge.draggable($newElement, {containment: 'parent'});
            }
            this.JsplumbBridge.addEndpoint($newElement, jsplumbConfig.streamEndpointOptions);
        }
        return $newElement;
    };

    setDropped($newElement, json, endpoints, preview) {
        this.RestApi.updateDataSet(json).success(data => {
            json.eventGrounding = data.eventGrounding;
            json.datasetInvocationId = data.invocationId;
            this.streamDropped($newElement, json, endpoints, preview);
        });
    }

    sepaDropped($newElement, json, endpoints, preview) {
        var jsplumbConfig = this.getJsplumbConfig(preview);
        if (!preview) {
            this.JsplumbBridge.draggable($newElement, {containment: 'parent'});
        }
        if (endpoints) {
            if (json.inputStreams.length < 2) { //1 InputNode
                this.JsplumbBridge.addEndpoint($newElement, jsplumbConfig.leftTargetPointOptions);
            } else {
                this.JsplumbBridge.addEndpoint($newElement, this.getNewTargetPoint(0, 0.25));

                this.JsplumbBridge.addEndpoint($newElement, this.getNewTargetPoint(0, 0.75));
            }
            this.JsplumbBridge.addEndpoint($newElement, jsplumbConfig.sepaEndpointOptions);
        }
        return $newElement;
    };

    actionDropped($newElement, json, endpoints, preview) {
        var jsplumbConfig = this.getJsplumbConfig(preview);
        if (!preview) {
            this.JsplumbBridge.draggable($newElement, {containment: 'parent'});
        }

        if (endpoints) {
            this.JsplumbBridge.addEndpoint($newElement, jsplumbConfig.leftTargetPointOptions);
        }
        return $newElement;
    };

    getJsplumbConfig(preview) {
        return preview ? this.JsplumbConfigService.getPreviewConfig() : this.JsplumbConfigService.getEditorConfig();
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

//JsplumbService.$inject = ['ObjectProvider', 'JsplumbConfigService', 'JsplumbBridge', '$timeout'];
