import _ from 'npm/lodash';
import * as dagre from "dagre";

export class PipelinePositioningService {

    constructor($rootScope, JsplumbService, apiConstants, JsplumbConfigService, JsplumbBridge) {
        this.$rootScope = $rootScope;
        this.JsplumbService = JsplumbService;
        this.apiConstants = apiConstants;
        this.JsplumbConfigService = JsplumbConfigService;
        this.JsplumbBridge = JsplumbBridge;
    }


    displayPipeline(scope, pipeline, targetCanvas, isPreview) {
        var tempPos = {x : 0, y: 0};
        var jsplumbConfig = isPreview ? this.JsplumbConfigService.getPreviewConfig() : this.JsplumbConfigService.getEditorConfig();

        for (var i = 0, stream; stream = pipeline.streams[i]; i++) {
            this.JsplumbService
                .streamDropped(scope, this.JsplumbService
                    .createNewAssemblyElement(stream, tempPos, false, targetCanvas, isPreview), true, isPreview);
        }
        for (var i = 0, sepa; sepa = pipeline.sepas[i]; i++) {
            var $sepa = this.JsplumbService.sepaDropped(scope, this.JsplumbService.createNewAssemblyElement(sepa, tempPos, false, targetCanvas, isPreview)
                .data("options", true), false, isPreview);
            if (this.JsplumbBridge.getConnections({source: sepa.DOM}).length == 0) { //Output Element
                this.JsplumbBridge.addEndpoint($sepa, jsplumbConfig.sepaEndpointOptions);
            }
        }
        for (var i = 0, action; action = pipeline.actions[i]; i++) {
            var $action = this.JsplumbService.actionDropped(scope, this.JsplumbService.createNewAssemblyElement(action, tempPos, false, targetCanvas, isPreview)
                .data("options", true), true, isPreview);
            jsplumb.addEndpoint($action, jsplumbConfig.leftTargetPointOptions);
        }

        this.connectPipelineElements(pipeline, !isPreview, jsplumbConfig);
        this.layoutGraph(targetCanvas, "span.a", jsplumb, isPreview ? 75 : 110, isPreview);
        this.JsplumbBridge.repaintEverything();

    };

    layoutGraph(canvas, nodeIdentifier, dimension, isPreview) {
        var g = new dagre.graphlib.Graph();
        g.setGraph({rankdir : "LR", ranksep : isPreview ? "50" : "100"});
        g.setDefaultEdgeLabel(function () {
            return {};
        });
        var nodes = $(canvas).find(nodeIdentifier).get();
        for (var i = 0; i < nodes.length; i++) {
            var n = nodes[i];
            g.setNode(n.id, {label: n.id, width: dimension, height: dimension});
        }
        var edges = jsplumb.getAllConnections();
        for (var i = 0; i < edges.length; i++) {
            var c = edges[i];
            g.setEdge(c.source.id, c.target.id);
        }
        dagre.layout(g);
        g.nodes().forEach(function (v) {
            $("#" + v).css("left", g.node(v).x + "px");
            $("#" + v).css("top", g.node(v).y + "px");
        });
    };

    connectPipelineElements(json, detachable, jsplumbConfig) {
        var source, target;
        var sourceEndpoint;
        var targetEndpoint

        this.JsplumbBridge.setSuspendDrawing(true);

        //Sepas --> Streams / Sepas --> Sepas---------------------//
        for (var i = 0, sepa; sepa = json.sepas[i]; i++) {
            for (var j = 0, connection; connection = sepa.connectedTo[j]; j++) {

                source = connection;
                target = sepa.DOM;

                var options;
                var id = "#" + source;
                if ($(id).hasClass("sepa")) {
                    options = jsplumbConfig.sepaEndpointOptions;
                } else {
                    options = jsplumbConfig.streamEndpointOptions;
                }

                sourceEndpoint = this.JsplumbBridge.addEndpoint(source, options);
                targetEndpoint = this.JsplumbBridge.addEndpoint(target, jsplumbConfig.leftTargetPointOptions);
                this.JsplumbBridge.connect({source: sourceEndpoint, target: targetEndpoint, detachable: detachable});
            }
        }
        for (var i = 0, action; action = json.actions[i]; i++) {
            //Action --> Sepas----------------------//
            target = action.DOM;

            for (var j = 0, connection; connection = action.connectedTo[j]; j++) {
                source = connection;

                sourceEndpoint = this.JsplumbBridge.addEndpoint(source, jsplumbConfig.sepaEndpointOptions);
                targetEndpoint = this.JsplumbBridge.addEndpoint(target, jsplumbConfig.leftTargetPointOptions);
                this.JsplumbBridge.connect({source: sourceEndpoint, target: targetEndpoint, detachable: detachable});
            }
        }
        this.JsplumbBridge.setSuspendDrawing(false, true);
    }

}

PipelinePositioningService.$inject = ['$rootScope', 'JsplumbService', 'apiConstants', 'JsplumbConfigService', 'JsplumbBridge'];