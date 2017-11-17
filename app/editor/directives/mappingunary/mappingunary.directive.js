mappingPropertyUnary.$inject = [];

export default function mappingPropertyUnary() {

    return {
        restrict: 'E',
        templateUrl: 'app/editor/directives/mappingunary/mappingunary.tmpl.html',
        scope: {
            staticProperty: "=",
            displayRecommended: "="
        },
        link: function (scope) {
            scope.selected = function(option, staticProperty) {
                if (!staticProperty.properties.mapsTo) {
                    if (option.properties.elementId == staticProperty.properties.mapsFromOptions[0].properties.elementId) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return option.properties.elementId == staticProperty.properties.mapsTo;
                }
            }
        }
    }

};
