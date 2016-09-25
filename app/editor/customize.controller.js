
CustomizeController.$inject = ['$scope', '$rootScope', '$mdDialog', 'elementData', 'sepaName', 'sourceEndpoint'];

export default function CustomizeController($scope, $rootScope, $mdDialog, elementData, sepaName, sourceEndpoint) {

    $scope.selectedElement = elementData.data("JSON");
    $scope.selection = [];
    $scope.matchingSelectionLeft = [];
    $scope.matchingSelectionRight = [];
    $scope.sepaName = sepaName;
    $scope.invalid = false;
    $scope.helpDialogVisible = false;
    $scope.currentStaticProperty;
    $scope.validationErrors = [];

    $scope.primitiveClasses = [{"id": "http://www.w3.org/2001/XMLSchema#string"},
        {"id": "http://www.w3.org/2001/XMLSchema#boolean"},
        {"id": "http://www.w3.org/2001/XMLSchema#integer"},
        {"id": "http://www.w3.org/2001/XMLSchema#long"},
        {"id": "http://www.w3.org/2001/XMLSchema#double"}];

    $scope.toggleHelpDialog = function () {
        $scope.helpDialogVisible = !$scope.helpDialogVisible;
    }

    $scope.setCurrentStaticProperty = function (staticProperty) {
        $scope.currentStaticProperty = staticProperty;
    }

    $scope.getStaticPropertyInfo = function () {
        var info = "";
        if (currentStaticProperty.type == 'MAPPING_PROPERTY')
            info += "This field is a mapping property. It requires you to select one or more specific data elements from a stream.<b>"
        info += "This field requires the following specifc input: <b>";
        return info;
    }

    $scope.hide = function () {
        $mdDialog.hide();
    };

    $scope.cancel = function () {
        $mdDialog.cancel();
    };

    $scope.setSelectValue = function (c, q) {
        console.log(q);
        angular.forEach(q, function (item) {
            item.selected = false;
        });

        c.selected = true;
    };

    /**
     * saves the parameters in the current element's data with key "options"
     */
    $scope.saveProperties = function () {

        angular.forEach($scope.selectedElement.staticProperties, function (item) {
                if (item.properties.staticPropertyType === 'OneOfStaticProperty') {
                    console.log(item);
                    angular.forEach(item.properties.options, function (option) {
                            if (item.properties.currentSelection) {
                                if (option.elementId == item.properties.currentSelection.elementId) {
                                    option.selected = true;
                                }
                            }
                        }
                    );
                }
            }
        )
        ;

        if ($scope.validate()) {
            $rootScope.state.currentElement.data("options", true);
            $rootScope.state.currentElement.data("JSON").staticProperties = $scope.selectedElement.staticProperties;
            $rootScope.state.currentElement.data("JSON").configured = true;
            $rootScope.state.currentElement.removeClass("disabled");
            $rootScope.$broadcast("SepaElementConfigured", elementData);
            $scope.hide();
            if (sourceEndpoint) sourceEndpoint.setType("token");
        }
        else $scope.invalid = true;

    }

    $scope.validate = function () {
        $scope.validationErrors = [];
        var valid = true;

        angular.forEach($scope.selectedElement.staticProperties, function (staticProperty) {
            if (staticProperty.properties.staticPropertyType === 'OneOfStaticProperty' ||
                staticProperty.properties.staticPropertyType === 'AnyStaticProperty') {
                var anyOccurrence = false;
                angular.forEach(staticProperty.properties.options, function (option) {
                    if (option.selected) anyOccurrence = true;
                });
                if (!anyOccurrence) valid = false;
            } else if (staticProperty.properties.staticPropertyType === 'FreeTextStaticProperty') {
                if (!staticProperty.properties.value) {
                    valid = false;
                }
                if (staticProperty.properties.requiredDatatype) {
                    if (!$scope.typeCheck(staticProperty.properties.value, staticProperty.properties.requiredDatatype)) {
                        valid = false;
                        $scope.validationErrors.push(staticProperty.properties.label + " must be of type " + staticProperty.properties.requiredDatatype);
                    }
                }
            } else if (staticProperty.properties.staticPropertyType === 'MappingPropertyUnary') {
                if (!staticProperty.properties.mapsTo) {
                    valid = false;
                }

            } else if (staticProperty.properties.staticPropertyType === 'MappingPropertyNary') {
                if (!staticProperty.properties.mapsTo ||
                    !staticProperty.properties.mapsTo.length > 0) {
                    valid = false;
                }
            }
        });

        angular.forEach($scope.selectedElement.outputStrategies, function (strategy) {
            if (strategy.type == 'de.fzi.cep.sepa.model.impl.output.CustomOutputStrategy') {
                if (!strategy.properties.eventProperties && !strategy.properties.eventProperties.length > 0) {
                    valid = false;
                }
            }
            // TODO add replace output strategy
            // TODO add support for replace output strategy
        });

        return valid;
    }

    $scope.typeCheck = function (property, datatype) {
        if (datatype == $scope.primitiveClasses[0].id) return true;
        if (datatype == $scope.primitiveClasses[1].id) return (property == 'true' || property == 'false');
        if (datatype == $scope.primitiveClasses[2].id) return (!isNaN(property) && parseInt(Number(property)) == property && !isNaN(parseInt(property, 10)));
        if (datatype == $scope.primitiveClasses[3].id) return (!isNaN(property) && parseInt(Number(property)) == property && !isNaN(parseInt(property, 10)));
        if (datatype == $scope.primitiveClasses[4].id) return !isNaN(property);
        return false;
    }

}