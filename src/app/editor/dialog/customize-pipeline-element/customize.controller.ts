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
    sepa: any;
    customizeForm: any;
    ShepherdService: any;

    constructor($rootScope, $mdDialog, elementData, sourceEndpoint, sepa, ShepherdService) {
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
        this.ShepherdService = ShepherdService;
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
            if (this.sourceEndpoint) {
                this.sourceEndpoint.setType("token");
            }
            if (this.ShepherdService.isTourActive()) {
                this.ShepherdService.trigger("save-" +this.sepa.type);
            }
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
                    console.log("value missing");
                    console.log(staticProperty);
                    valid = false;
                }
                if (staticProperty.properties.requiredDatatype) {

                }
            } else if (staticProperty.properties.staticPropertyType === 'MappingPropertyUnary') {
                if (!staticProperty.properties.selectedProperty) {
                    valid = false;
                }

            } else if (staticProperty.properties.staticPropertyType === 'MappingPropertyNary') {
                if (staticProperty.properties.valueRequired) {
                    if (!staticProperty.properties.selectedProperties ||
                        !(staticProperty.properties.selectedProperties.length > 0)) {
                        valid = false;
                    }
                }
            }
        });

        angular.forEach(this.selectedElement.outputStrategies, strategy => {
            if (strategy.type == 'org.streampipes.model.output.CustomOutputStrategy') {
                if (!strategy.properties.selectedPropertyKeys && !(strategy.properties.selectedPropertyKeys.length > 0)) {
                    valid = false;
                }
            }
            // TODO add replace output strategy
            // TODO add support for replace output strategy
        });

        return valid;
    }





}

CustomizeController.$inject = ['$rootScope', '$mdDialog', 'elementData', 'sourceEndpoint', 'sepa', 'ShepherdService'];