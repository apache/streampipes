angular.module('streamPipesApp')
    .directive('replaceOutput', function ($interval) {
        return {
            restrict : 'E',
            templateUrl : 'modules/editor/templates/replaceoutput/replaceoutput.tmpl.html',
            scope : {
                outputStrategy : "="
            },
            link: function ($scope) {
                console.log($scope.outputStrategy);
            }
        }

    });