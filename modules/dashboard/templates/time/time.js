'use strict';

angular.module('streamPipesApp')
    .directive('wtTime', function ($interval) {
        return {
            restrict: 'A',
            replace: true,
            templateUrl: 'modules/dashboard/templates/time/time.html',
            link: function (scope) {
                function update() {
                    scope.time = new Date().toLocaleTimeString();
                }

                var promise = $interval(update, 500);

                scope.$on('$destroy', function () {
                    $interval.cancel(promise);
                });
            }
        };
    });