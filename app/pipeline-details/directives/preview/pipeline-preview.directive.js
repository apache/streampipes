import angular from 'npm/angular';
'use strict';

pipelinePreview.$inject = ['pipelinePositioningService', 'jsplumbService'];


export default function pipelinePreview(pipelinePositioningService, jsplumbService) {

    return {
        restrict: 'E',
        templateUrl: 'app/pipeline-details/directives/preview/pipeline-preview.tmpl.html',
        scope: {
            pipeline: "=",
            name: "@",
            updateSelected: "&"
        },
        controller: function ($scope) {

        },
        link: function postLink(scope, element) {
            var js2 = jsPlumb.getInstance({});
            js2.setContainer("assembly-preview");
            jsplumbService.prepareJsplumb(js2);
            pipelinePositioningService.displayPipeline(scope, js2, scope.pipeline, "#assembly-preview", true);

            var existingEndpointIds = [];
            js2.selectEndpoints().each(function(endpoint) {
                if (existingEndpointIds.indexOf(endpoint.element.id) == -1) {
                    $(endpoint.element).click(function(ev) {
                        scope.updateSelected({selected: $(endpoint.element).data("JSON")});
                    });
                    existingEndpointIds.push(endpoint.element.id);
                }
            });
        }
    }
};