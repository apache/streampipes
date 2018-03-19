import * as angular from 'angular';

export class CustomizeController {

    selectedElement: any;
    selection: any;
    matchingSelectionLeft: any;
    matchingSelectionRight: any;
    invalid: any;
    helpDialogVisible: any;
    currentStaticProperty: any;
    validationErrors: any;
    configVisible: any;
    displayRecommended: any;
    sourceEndpoint: any;
    $mdDialog: any;
    $rootScope: any;
    primitiveClasses: any;
    sepa: any;

    constructor($rootScope, $mdDialog, elementData, sourceEndpoint, sepa) {
        this.selectedElement = sepa;
        this.selection = [];
        this.matchingSelectionLeft = [];
        this.matchingSelectionRight = [];
        this.invalid = false;
        this.helpDialogVisible = false;
        this.validationErrors = [];
        this.configVisible = false;
        this.displayRecommended = false;
        this.sourceEndpoint = sourceEndpoint;
        this.$mdDialog = $mdDialog;
        this.$rootScope = $rootScope;

        this.primitiveClasses = [{"id": "http://www.w3.org/2001/XMLSchema#string"},
            {"id": "http://www.w3.org/2001/XMLSchema#boolean"},
            {"id": "http://www.w3.org/2001/XMLSchema#integer"},
            {"id": "http://www.w3.org/2001/XMLSchema#long"},
            {"id": "http://www.w3.org/2001/XMLSchema#double"}];

        if ((this.selectedElement.staticProperties && this.selectedElement.staticProperties.length > 0) || this.isCustomOutput()) {
            this.configVisible = true;
        } else {
            this.saveProperties();
        }
    }

    toggleHelpDialog() {
        this.helpDialogVisible = !this.helpDialogVisible;
    }

    setCurrentStaticProperty(staticProperty) {
        this.currentStaticProperty = staticProperty;
    }

    getStaticPropertyInfo() {
        var info = "";
        if (this.currentStaticProperty.type == 'MAPPING_PROPERTY')
            info += "This field is a mapping property. It requires you to select one or more specific data elements from a stream.<b>"
        info += "This field requires the following specifc input: <b>";
        return info;
    }

    hide() {
        this.$mdDialog.hide();
    };

    cancel() {
        this.$mdDialog.cancel();
    };

    setSelectValue(c, q) {
        console.log(q);
        angular.forEach(q, function (item) {
            item.selected = false;
        });

        c.selected = true;
    };

    /**
     * saves the parameters in the current element's data with key "options"
     */
    saveProperties() {

        angular.forEach(this.selectedElement.staticProperties, item => {
                if (item.properties.staticPropertyType === 'OneOfStaticProperty' || item.properties.staticPropertyType === 'RuntimeResolvableOneOfStaticProperty') {
                    angular.forEach(item.properties.options, option => {
                            if (item.properties.currentSelection) {
                                if (option.elementId == item.properties.currentSelection.elementId) {
                                    option.selected = true;
                                } else {
                                    option.selected = false;
                                }
                            }
                        }
                    );
                }
            }
        )
        ;

        if (this.validate()) {
            // this.$rootScope.state.currentElement.data("options", true);
            // this.$rootScope.state.currentElement.data("JSON").staticProperties = this.selectedElement.staticProperties;
            // this.$rootScope.state.currentElement.data("JSON").configured = true;
            // this.$rootScope.state.currentElement.removeClass("disabled");
            this.$rootScope.$broadcast("SepaElementConfigured", this.sepa.DOM);
            this.selectedElement.configured = true;
            this.hide();
            if (this.sourceEndpoint) this.sourceEndpoint.setType("token");
        }
        else this.invalid = true;

    }

    validate() {
        this.validationErrors = [];
        var valid = true;

        angular.forEach(this.selectedElement.staticProperties, staticProperty => {
            if (staticProperty.properties.staticPropertyType === 'OneOfStaticProperty' ||
                staticProperty.properties.staticPropertyType === 'AnyStaticProperty') {
                var anyOccurrence = false;
                angular.forEach(staticProperty.properties.options, option => {
                    if (option.selected) anyOccurrence = true;
                });
                if (!anyOccurrence) valid = false;
            } else if (staticProperty.properties.staticPropertyType === 'FreeTextStaticProperty') {
                if (!staticProperty.properties.value) {
                    console.log(staticProperty.properties.value);
                    console.log("VALUE");
                    valid = false;
                }
                if (staticProperty.properties.requiredDatatype) {
                    if (!this.typeCheck(staticProperty.properties.value, staticProperty.properties.requiredDatatype)) {
                        valid = false;
                        this.validationErrors.push(staticProperty.properties.label + " must be of type " + staticProperty.properties.requiredDatatype);
                    }
                }
            } else if (staticProperty.properties.staticPropertyType === 'MappingPropertyUnary') {
                if (!staticProperty.properties.mapsTo) {
                    valid = false;
                }

            } else if (staticProperty.properties.staticPropertyType === 'MappingPropertyNary') {
                if (staticProperty.properties.valueRequired) {
                    if (!staticProperty.properties.mapsTo ||
                        !(staticProperty.properties.mapsTo.length > 0)) {
                        valid = false;
                    }
                }
            }
        });

        angular.forEach(this.selectedElement.outputStrategies, strategy => {
            if (strategy.type == 'org.streampipes.model.output.CustomOutputStrategy') {
                if (!strategy.properties.eventProperties && !(strategy.properties.eventProperties.length > 0)) {
                    valid = false;
                }
            }
            // TODO add replace output strategy
            // TODO add support for replace output strategy
        });

        return valid;
    }

    typeCheck(property, datatype) {
        if (datatype == this.primitiveClasses[0].id) return true;
        if (datatype == this.primitiveClasses[1].id) return (property == 'true' || property == 'false');
        if (datatype == this.primitiveClasses[2].id) return (!isNaN(property) && parseInt(Number(property)+'') == property && !isNaN(parseInt(property, 10)));
        if (datatype == this.primitiveClasses[3].id) return (!isNaN(property) && parseInt(Number(property)+'') == property && !isNaN(parseInt(property, 10)));
        if (datatype == this.primitiveClasses[4].id) return !isNaN(property);
        return false;
    }

    isCustomOutput() {
        var custom = false;
        angular.forEach(this.selectedElement.outputStrategies, strategy => {
            if (strategy.type == 'org.streampipes.model.output.CustomOutputStrategy') {
                custom = true;
            }
        });
        return custom;
    }

}

CustomizeController.$inject = ['$rootScope', '$mdDialog', 'elementData', 'sourceEndpoint', 'sepa'];