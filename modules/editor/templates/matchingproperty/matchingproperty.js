angular.module('streamPipesApp')
    .directive('matchingProperty', function ($interval) {
        return {
            restrict : 'E',
            templateUrl : 'modules/editor/templates/matchingproperty/matchingproperty.tmpl.html',
            scope : {
                staticProperty : "="
            },
            controller: function($scope, $element) {

            }
        }

    });