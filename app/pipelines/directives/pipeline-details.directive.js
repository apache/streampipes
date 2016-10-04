import angular from 'npm/angular';

pipelineDetails.$inject = [];
'use strict';

export default function pipelineDetails() {

    return {
        restrict: 'E',
        templateUrl: 'app/pipelines/directives/pipeline-details.tmpl.html',
        scope: false,
        controller: function ($scope) {


        },
        link: function postLink(scope, element) {

        }
    }
};