import _ from 'npm/lodash';
import * as dagre from "dagre";

pipelinePositioningService.$inject = ['$rootScope', 'jsplumbService', 'apiConstants', 'jsplumbConfigService'];

export default function pipelinePositioningService($rootScope, jsplumbService, apiConstants, jsplumbConfigService) {

    var pipelinePositioningService = {};

    pipelinePositioningService.displayPipeline = function (scope, jsplumb, pipeline, targetCanvas, isPreview) {
        var tempPos = {x : 0, y: 0};
        var jsplumbConfig = isPreview ? jsplumbConfigService.getPreviewConfig() : jsplumbConfigService.getEditorConfig();

        for (var i = 0, stream; stream = pipeline.streams[i]; i++) {
            jsplumbService
                .streamDropped(scope, jsplumb, jsplumbService
                    .createNewAssemblyElement(jsplumb, stream, tempPos, false, targetCanvas, isPreview), true, isPreview);
        }
        for (var i = 0, sepa; sepa = pipeline.sepas[i]; i++) {
            var $sepa = jsplumbService.sepaDropped(scope, jsplumb, jsplumbService.createNewAssemblyElement(jsplumb, sepa, tempPos, false, targetCanvas, isPreview)
                .data("options", true), false, isPreview);
            if (jsplumb.getConnections({source: sepa.DOM}).length == 0) { //Output Element
                jsplumb.addEndpoint($sepa, jsplumbConfig.sepaEndpointOptions);
            }
        }
        for (var i = 0, action; action = pipeline.actions[i]; i++) {
            var $action = jsplumbService.actionDropped(scope, jsplumb, jsplumbService.createNewAssemblyElement(jsplumb, action, tempPos, false, targetCanvas, isPreview)
                .data("options", true), true, isPreview);
            jsplumb.addEndpoint($action, jsplumbConfig.leftTargetPointOptions);
        }

        connectPipelineElements(jsplumb, pipeline, !isPreview, jsplumbConfig);
        pipelinePositioningService.layoutGraph(targetCanvas, "span.a", jsplumb, isPreview ? 75 : 110, isPreview);
        jsplumb.repaintEverything();

    };

    pipelinePositioningService.layoutGraph = function (canvas, nodeIdentifier, jsplumb, dimension, isPreview) {
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

    function connectPipelineElements(jsplumb, json, detachable, jsplumbConfig) {
        var source, target;
        var sourceEndpoint;
        var targetEndpoint

        jsplumb.setSuspendDrawing(true);

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

                sourceEndpoint = jsplumb.addEndpoint(source, options);
                targetEndpoint = jsplumb.addEndpoint(target, jsplumbConfig.leftTargetPointOptions);
                jsplumb.connect({source: sourceEndpoint, target: targetEndpoint, detachable: detachable});
            }
        }
        for (var i = 0, action; action = json.actions[i]; i++) {
            //Action --> Sepas----------------------//
            target = action.DOM;

            for (var j = 0, connection; connection = action.connectedTo[j]; j++) {
                source = connection;

                sourceEndpoint = jsplumb.addEndpoint(source, jsplumbConfig.sepaEndpointOptions);
                targetEndpoint = jsplumb.addEndpoint(target, jsplumbConfig.leftTargetPointOptions);
                jsplumb.connect({source: sourceEndpoint, target: targetEndpoint, detachable: detachable});
            }
        }
        jsplumb.setSuspendDrawing(false, true);
    }

    return pipelinePositioningService;
}