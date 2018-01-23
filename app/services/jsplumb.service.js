jsplumbService.$inject = ['$http', '$rootScope', 'pipelineElementIconService', 'objectProvider', 'apiConstants', '$compile', 'jsplumbConfigService'];

export default function jsplumbService($http, $rootScope, pipelineElementIconService, objectProvider, apiConstants, $compile, jsplumbConfigService) {

    var jsplumbService = {};

    jsplumbService.prepareJsplumb = function(jsplumb) {
        jsplumb.registerEndpointTypes({
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

    jsplumbService.createNewAssemblyElement = function(jsplumb, json, coordinates, block, target, isPreview) {
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
            jsplumb.draggable($newState, {containment: 'parent'});
        }

        if (!block) {
            pipelineElementIconService.addImageOrTextIcon($newState, json, false, 'connectable');
        }

        return $newState;
    }

    var loadOptionsButtons = function (scope, $newElement) {
        scope.currentPipelineElement = $newElement.data("JSON");
        scope.currentPipelineElementDom = $newElement[0].id;
        var elementId = scope.currentPipelineElement.type == 'stream' ? scope.currentPipelineElement.elementId : scope.currentPipelineElement.belongsTo;
        $newElement.append($compile('<pipeline-element-options show-customize-stream-dialog-function=showCustomizeStreamDialog show-customize-dialog-function=showCustomizeDialog delete-function=handleDeleteOption create-partial-pipeline-function=createPartialPipeline create-function=createAssemblyElement all-elements=allElements pipeline-element-id=' + elementId + ' internal-id=' + scope.currentPipelineElementDom + '></pipeline-element-options>')(scope));
    }

    jsplumbService.streamDropped = function (scope, jsplumb, $newElement, endpoints, preview) {
        var jsplumbConfig = getJsplumbConfig(preview);
        scope.isStreamInAssembly = true;
        $newElement.addClass("connectable stream");
        $newElement.id = "sp_stream_" + ($rootScope.state.currentPipeline.streams.length + 1);
        var pipelinePart = new objectProvider.Pipeline();
        pipelinePart.addElement($newElement);
        $rootScope.state.currentPipeline = pipelinePart;
        if (endpoints) {
            jsplumb.addEndpoint($newElement, jsplumbConfig.streamEndpointOptions);
        }
        if (!preview) {
            loadOptionsButtons(scope, $newElement);
        }
        return $newElement;
    };

    jsplumbService.sepaDropped = function (scope, jsplumb, $newElement, endpoints, preview) {
        var jsplumbConfig = getJsplumbConfig(preview);
        scope.isSepaInAssembly = true;
        $newElement.addClass("connectable sepa");
        if ($newElement.data("JSON").staticProperties != null && !$rootScope.state.adjustingPipelineState && !$newElement.data("options")) {
            $newElement
                .addClass('disabled');
        }


        if (endpoints) {
            if ($newElement.data("JSON").inputStreams.length < 2) { //1 InputNode
                //jsPlumb.addEndpoint($newElement, apiConstants.leftTargetPointOptions);
                jsplumb.addEndpoint($newElement, jsplumbConfig.leftTargetPointOptions);
            } else {
                jsplumb.addEndpoint($newElement, getNewTargetPoint(0, 0.25));

                jsplumb.addEndpoint($newElement, getNewTargetPoint(0, 0.75));
            }
            jsplumb.addEndpoint($newElement, jsplumbConfig.sepaEndpointOptions);
        }
        if (!preview) loadOptionsButtons(scope, $newElement);
        return $newElement;
    };

    jsplumbService.actionDropped = function (scope, jsplumb, $newElement, endpoints, preview) {
        var jsplumbConfig = getJsplumbConfig(preview);
        scope.isActionInAssembly = true;
        $newElement
            .addClass("connectable action");
        if ($newElement.data("JSON").staticProperties != null && !$rootScope.state.adjustingPipelineState) {
            $newElement
                .addClass('disabled');
        }
        if (endpoints) {
            jsplumb.addEndpoint($newElement, jsplumbConfig.leftTargetPointOptions);
        }
        if (!preview) loadOptionsButtons(scope, $newElement);
        return $newElement;
    };

    var getJsplumbConfig = function(preview) {
        return  preview ? jsplumbConfigService.getPreviewConfig() : jsplumbConfigService.getEditorConfig();
    }

    var getNewTargetPoint = function(x, y) {
        return {
            endpoint: ["Dot", {radius: 12}],
            type: "empty",
            anchor: [x, y, -1, 0],
            isTarget: true
        };
    }

    return jsplumbService;
}