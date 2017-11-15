customizeDialog.$inject = [];

export default function customizeDialog() {

    return {
        restrict: 'E',
        templateUrl: 'app/editor/directives/customize/customize-dialog.tmpl.html',
        scope: {
            staticProperty: "=",
            selectedElement: "="
        },
        link: function (scope) {
            scope.getMappingProperty = function(mapsTo) {
                var sps;
                angular.forEach(scope.selectedElement.staticProperties, function(sp) {
                    if (sp.properties.internalName == mapsTo) {
                        sps = sp;
                    }
                });
                return sps;
            }
        }
    }

};
