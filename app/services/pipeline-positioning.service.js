import _ from 'npm/lodash';
import * as dagre from "dagre";

pipelinePositioningService.$inject = ['$rootScope', 'jsplumbService', 'apiConstants'];

export default function pipelinePositioningService($rootScope, jsplumbService, apiConstants) {

    var pipelinePositioningService = {};

    pipelinePositioningService.displayPipeline = function (scope, jsplumb, pipeline, targetCanvas, isPreview) {
        var tempPos = {x : 0, y: 0};
        for (var i = 0, stream; stream = pipeline.streams[i]; i++) {
            jsplumbService
                .streamDropped(scope, jsplumb, jsplumbService
                    .createNewAssemblyElement(jsplumb, stream, tempPos, false, targetCanvas), true, isPreview);
        }
        for (var i = 0, sepa; sepa = pipeline.sepas[i]; i++) {
            var $sepa = jsplumbService.sepaDropped(scope, jsplumb, jsplumbService.createNewAssemblyElement(jsplumb, sepa, tempPos, false, targetCanvas)
                .data("options", true), false, isPreview);
            if (jsplumb.getConnections({source: sepa.DOM}).length == 0) { //Output Element
                jsplumb.addEndpoint($sepa, apiConstants.sepaEndpointOptions);
            }
        }
        for (var i = 0, action; action = pipeline.actions[i]; i++) {
            var $action = jsplumbService.actionDropped(scope, jsplumb, jsplumbService.createNewAssemblyElement(jsplumb, action, tempPos, false, targetCanvas)
                .data("options", true), true, isPreview);
            jsplumb.addEndpoint($action, apiConstants.leftTargetPointOptions);
        }

        connectPipelineElements(jsplumb, pipeline, true);
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
        console.log(g);
        g.nodes().forEach(function (v) {
            $("#" + v).css("left", g.node(v).x + "px");
            $("#" + v).css("top", g.node(v).y + "px");
        });
    };

    function connectPipelineElements(jsplumb, json, detachable) {
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
                    options = apiConstants.sepaEndpointOptions;
                } else {
                    options = apiConstants.streamEndpointOptions;
                }

                sourceEndpoint = jsplumb.addEndpoint(source, options);
                targetEndpoint = jsplumb.addEndpoint(target, apiConstants.leftTargetPointOptions);
                jsplumb.connect({source: sourceEndpoint, target: targetEndpoint, detachable: detachable});
            }
        }
        for (var i = 0, action; action = json.actions[i]; i++) {
            //Action --> Sepas----------------------//
            target = action.DOM;

            for (var j = 0, connection; connection = action.connectedTo[j]; j++) {
                source = connection;

                sourceEndpoint = jsplumb.addEndpoint(source, apiConstants.sepaEndpointOptions);
                targetEndpoint = jsplumb.addEndpoint(target, apiConstants.leftTargetPointOptions);
                jsplumb.connect({source: sourceEndpoint, target: targetEndpoint, detachable: detachable});
            }
        }
        jsplumb.setSuspendDrawing(false, true);
    }

    return pipelinePositioningService;
}