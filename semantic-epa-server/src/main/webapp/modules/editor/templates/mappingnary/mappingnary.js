angular.module('streamPipesApp')
    .directive('mappingPropertyNary', function ($interval) {
        return {
            restrict : 'E',
            templateUrl : 'modules/editor/templates/mappingnary/mappingnary.tmpl.html',
            scope : {
                staticProperty : "="
            },
            controller: function($scope, $element) {

                console.log($scope.staticProperty);
            }
        }

    });