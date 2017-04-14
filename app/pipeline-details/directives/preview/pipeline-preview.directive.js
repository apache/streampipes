import angular from 'npm/angular';

pipelinePreview.$inject = [];
'use strict';

export default function pipelinePreview() {

    return {
        restrict: 'E',
        templateUrl: 'app/pipeline-details/directives/preview/pipeline-preview.tmpl.html',
        scope : {
            pipeline: "=pipeline",
        },
        controller: function ($scope) {


        },
        link: function postLink(scope, element) {

        }
    }
};