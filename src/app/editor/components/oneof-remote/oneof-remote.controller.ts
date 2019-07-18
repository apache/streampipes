import * as angular from 'angular';

export class OneOfRemoteController {

    RestApi: any;
    $rootScope: any;
    staticProperty: any;
    selectedElement: any;
    currentlyLinkedProperty: any;

    showOptions: boolean = false;

    dependentStaticProperties: any = new Map();
    loading: boolean = false;

    constructor(RestApi, $rootScope) {
        this.RestApi = RestApi;
        this.$rootScope = $rootScope;
    }

    $onInit() {
        if (this.staticProperty.properties.options.length == 0 && (!this.staticProperty.properties.dependsOn || this.staticProperty.properties.dependsOn.length == 0)) {
            this.loadOptionsFromRestApi();
        } else {
            this.loadSavedProperty();
        }

        angular.forEach(this.selectedElement.staticProperties, sp => {
            if (sp.properties.internalName === this.staticProperty.properties.linkedMappingPropertyId) {
                this.currentlyLinkedProperty = sp.properties.mapsTo;
            }
        });

        if (this.staticProperty.properties.dependsOn && this.staticProperty.properties.dependsOn.length > 0) {
            angular.forEach(this.staticProperty.properties.dependsOn, dp => {
                this.dependentStaticProperties.set(dp, false);
                this.$rootScope.$on(dp, (valid) => {
                    this.showOptions = false;
                    this.dependentStaticProperties.set(dp, valid);
                    if (Array.from(this.dependentStaticProperties.values()).every(v => v === true)) {
                        this.loadOptionsFromRestApi();
                    }
                });
            });
        }
    }

    loadOptionsFromRestApi() {
        var resolvableOptionsParameterRequest = {};
        resolvableOptionsParameterRequest['staticProperties'] = this.selectedElement.staticProperties;
        resolvableOptionsParameterRequest['inputStreams'] = this.selectedElement.inputStreams;
        resolvableOptionsParameterRequest['belongsTo'] = this.selectedElement.belongsTo;
        resolvableOptionsParameterRequest['runtimeResolvableInternalId'] = this.staticProperty.properties.internalName;

        this.showOptions = false;
        this.loading = true;
        this.RestApi.fetchRemoteOptions(resolvableOptionsParameterRequest).then(msg => {
            let data = msg.data;
            this.staticProperty.properties.options = data;
            if (this.staticProperty.properties.options.length > 0) {
                this.staticProperty.properties.options[0].selected = true;
            }
            this.loading = false;
            this.loadSavedProperty();
        });
    }

    loadSavedProperty() {
        angular.forEach(this.staticProperty.properties.options, option => {
            if (option.selected) {
                this.staticProperty.properties.currentSelection = option;
            }
        });
        this.showOptions = true;
    }
}

OneOfRemoteController.$inject = ['RestApi', '$rootScope'];