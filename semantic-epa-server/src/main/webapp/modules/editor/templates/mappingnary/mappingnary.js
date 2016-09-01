angular.module('streamPipesApp')
    .directive('mappingPropertyNary', function ($interval) {
        return {
            restrict : 'E',
            templateUrl : 'modules/editor/templates/mappingnary/mappingnary.tmpl.html',
            scope : {
                staticProperty : "="
            },
            controller: function($scope, $element) {

                $scope.toggle = function(property, staticProperty) {
                    if ($scope.exists(property, staticProperty)) {
                        remove(property, staticProperty);
                    } else {
                        add(property, staticProperty);
                    }
                }

                $scope.exists = function(property, staticProperty) {
                    if (!staticProperty.properties.mapsTo) return false;
                    return staticProperty.properties.mapsTo.indexOf(property.properties.elementName) > -1;
                }

                var add = function(property, staticProperty) {
                    if (!staticProperty.properties.mapsTo) {
                        staticProperty.properties.mapsTo = [];
                    }
                    staticProperty.properties.mapsTo.push(property.properties.elementName);
                    console.log(staticProperty);
                }

                var remove = function(property, staticProperty) {
                    var index = staticProperty.properties.mapsTo.indexOf(property.elementName);
                    staticProperty.properties.mapsTo.splice(index, 1);
                }

                $scope.staticProperty.validator = function() {
                    return $scope.staticProperty.properties.mapsTo &&
                        $scope.staticProperty.properties.mapsTo.length > 0;
                }
            }
        }

    });