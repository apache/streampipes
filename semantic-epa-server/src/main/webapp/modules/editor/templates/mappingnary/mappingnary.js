angular.module('streamPipesApp')
    .directive('mappinPropertyNary', function ($interval) {
        return {
            restrict : 'E',
            templateUrl : 'modules/editor/templates/mappingnary/mappingnary.tmpl.html',
            scope : {
                staticProperty : "="
            },
            controller: function($scope, $element) {

            }
        }

    });