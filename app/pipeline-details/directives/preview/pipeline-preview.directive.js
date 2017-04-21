import angular from 'npm/angular';
'use strict';

pipelinePreview.$inject = ['pipelinePositioningService', 'jsplumbService', '$timeout'];


export default function pipelinePreview(pipelinePositioningService, jsplumbService, $timeout) {

    return {
        restrict: 'E',
        templateUrl: 'app/pipeline-details/directives/preview/pipeline-preview.tmpl.html',
        scope: {
            pipeline: "=",
            name: "@",
            updateSelected: "&",
            jspcanvas: "@"
        },
        controller: function ($scope) {

        },
        link: function postLink(scope, element) {
            $timeout(function() {
                var js2 = jsPlumb.getInstance({});
                js2.setContainer(scope.jspcanvas);
                jsplumbService.prepareJsplumb(js2);
                var elid = "#" + scope.jspcanvas;
                pipelinePositioningService.displayPipeline(scope, js2, scope.pipeline, elid, true);
                var existingEndpointIds = [];
                js2.selectEndpoints().each(function (endpoint) {
                    if (existingEndpointIds.indexOf(endpoint.element.id) == -1) {
                        $(endpoint.element).click(function (ev) {
                            scope.updateSelected({selected: $(endpoint.element).data("JSON")});
                        });
                        existingEndpointIds.push(endpoint.element.id);
                    }
                });
                
            });
        }
    }
};