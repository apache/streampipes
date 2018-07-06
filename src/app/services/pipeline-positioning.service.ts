//import * from 'lodash';
import * as dagre from "dagre";
declare const jsPlumb: any;

export class PipelinePositioningService {

    JsplumbService: any;
    JsplumbConfigService: any;
    JsplumbBridge: any;

    constructor(JsplumbService, JsplumbConfigService, JsplumbBridge) {
        this.JsplumbService = JsplumbService;
        this.JsplumbConfigService = JsplumbConfigService;
        this.JsplumbBridge = JsplumbBridge;
    }


    displayPipeline(rawPipelineModel, targetCanvas, isPreview) {
        var jsplumbConfig = isPreview ? this.JsplumbConfigService.getPreviewConfig() : this.JsplumbConfigService.getEditorConfig();

        for (var i = 0; i < rawPipelineModel.length; i++) {
            var currentPe = rawPipelineModel[i];
            if (currentPe.type === "stream") {
                this.JsplumbService
                    .streamDropped(currentPe.payload.DOM, currentPe.payload, true, isPreview);
            }
            if (currentPe.type === "sepa") {
                var $sepa = this.JsplumbService.sepaDropped(currentPe.payload.DOM, currentPe.payload, true, isPreview);
                if (this.JsplumbBridge.getConnections({source: currentPe.payload.DOM}).length == 0) { //Output Element
                    this.JsplumbBridge.addEndpoint($sepa, jsplumbConfig.sepaEndpointOptions);
                }
            }
            if (currentPe.type === "action") {
                var $action = this.JsplumbService.actionDropped(currentPe.payload.DOM, currentPe.payload, true, isPreview);
                this.JsplumbBridge.addEndpoint($action, jsplumbConfig.leftTargetPointOptions);
            }
        }

        this.connectPipelineElements(rawPipelineModel, !isPreview, jsplumbConfig);
        this.layoutGraph(targetCanvas, "span[id^='jsplumb']", isPreview ? 75 : 110, isPreview);
        this.JsplumbBridge.repaintEverything();

    }

    layoutGraph(canvas, nodeIdentifier, dimension, isPreview) {
        var g = new dagre.graphlib.Graph();
        g.setGraph({rankdir: "LR", ranksep: isPreview ? "50" : "100"});
        g.setDefaultEdgeLabel(function () {
            return {};
        });
        var nodes = $(canvas).find(nodeIdentifier).get();

        for (var i = 0; i < nodes.length; i++) {
            var n = nodes[i];
            g.setNode(n.id, {label: n.id, width: dimension, height: dimension});
        }
        var edges = this.JsplumbBridge.getAllConnections();
        for (var i = 0; i < edges.length; i++) {
            var c = edges[i];
            g.setEdge(c.source.id, c.target.id);
        }
        dagre.layout(g);
        g.nodes().forEach(v => {
            $("#" + v).css("left", g.node(v).x + "px");
            $("#" + v).css("top", g.node(v).y + "px");
        });
    }

    connectPipelineElements(json, detachable, jsplumbConfig) {
        var source, target;
        var sourceEndpoint;
        var targetEndpoint

        this.JsplumbBridge.setSuspendDrawing(true);
        for (var i = 0; i < json.length; i++) {
            var pe = json[i];

            if (pe.type == "sepa") {
                for (var j = 0, connection; connection = pe.payload.connectedTo[j]; j++) {
                    source = connection;
                    target = pe.payload.DOM;

                    var options;
                    var id = "#" + source;
                    if ($(id).hasClass("sepa")) {
                        options = jsplumbConfig.sepaEndpointOptions;
                    } else {
                        options = jsplumbConfig.streamEndpointOptions;
                    }

                    sourceEndpoint = this.JsplumbBridge.addEndpoint(source, options);
                    targetEndpoint = this.JsplumbBridge.addEndpoint(target, jsplumbConfig.leftTargetPointOptions);
                    this.JsplumbBridge.connect({
                        source: sourceEndpoint,
                        target: targetEndpoint,
                        detachable: detachable
                    });
                }
            } else if (pe.type == "action") {
                target = pe.payload.DOM;

                for (var j = 0, connection; connection = pe.payload.connectedTo[j]; j++) {
                    source = connection;
                    sourceEndpoint = this.JsplumbBridge.addEndpoint(source, jsplumbConfig.sepaEndpointOptions);
                    targetEndpoint = this.JsplumbBridge.addEndpoint(target, jsplumbConfig.leftTargetPointOptions);
                    this.JsplumbBridge.connect({
                        source: sourceEndpoint,
                        target: targetEndpoint,
                        detachable: detachable
                    });


                }
            }
        }
        this.JsplumbBridge.setSuspendDrawing(false, true);
    }

}

//PipelinePositioningService.$inject = ['JsplumbService', 'JsplumbConfigService', 'JsplumbBridge'];