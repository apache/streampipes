'use strict';

pipelineStatus.$inject = ['restApi'];


export default function pipelineStatus(restApi) {

    return {
        restrict: 'E',
        templateUrl: 'app/pipeline-details/directives/status/pipeline-status.tmpl.html',
        scope: {
            pipeline: "=",
        },
        controller: function ($scope) {
            $scope.pipelineStatus;

            var getPipelineStatus = function () {
                restApi.getPipelineStatusById($scope.pipeline._id)
                    .success(function (data) {

                        $scope.pipelineStatus = data;
                    })
            }

            getPipelineStatus();

        },
        link: function postLink(scope, element) {
        }
    }
};