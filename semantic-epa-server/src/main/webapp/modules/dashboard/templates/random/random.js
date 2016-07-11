'use strict';

angular.module('streamPipesApp')
    .directive('wtRandom', function ($interval) {
        return {
            restrict: 'A',
            replace: true,
            templateUrl: 'modules/dashboard/templates/random/random.html',
            link: function postLink(scope) {
                function update() {
                    scope.number = Math.floor(Math.random() * 100);
                }

                var promise = $interval(update, 500);

                scope.$on('$destroy', function () {
                    $interval.cancel(promise);
                });
            }
        };
    });