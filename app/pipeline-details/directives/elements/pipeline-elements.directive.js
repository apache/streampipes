'use strict';

pipelineElements.$inject = [];


export default function pipelineElements() {

    return {
        restrict: 'E',
        templateUrl: 'app/pipeline-details/directives/elements/pipeline-elements.tmpl.html',
        scope: {
            pipeline: "=",
            selectedElement: "="
        },
        controller: function ($scope) {
            
        },
        link: function postLink(scope, element) {
        }
    }
};