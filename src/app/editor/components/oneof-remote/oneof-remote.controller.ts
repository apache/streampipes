import * as angular from 'angular';

export class OneOfRemoteController {

    RestApi: any;
    $rootScope: any;
    staticProperty: any;
    staticProperties: any;
    eventProperties: any;
    belongsTo: any;
    $timeout: any;

    constructor(RestApi, $rootScope, $timeout) {
        this.RestApi = RestApi;
        this.$rootScope = $rootScope
        this.$timeout = $timeout;
        // this.loadSavedProperty();


        if (this.staticProperty.properties.linkedMappingPropertyId == undefined && this.staticProperty.properties.options.length == 0) {
            this.loadOptionsFromRestApi();
        }

        this.$rootScope.$on(this.staticProperty.properties.linkedMappingPropertyId, () => {
            angular.forEach(this.staticProperties, sp => {
                if (sp.properties.internalName === this.staticProperty.properties.linkedMappingPropertyId) {
                    if (this.staticProperty.lastMappingState !== sp.properties.mapsTo) {
                        this.staticProperty.lastMappingState = sp.properties.mapsTo;
                        this.loadOptionsFromRestApi();
                    }
                }
            });
        });
    }

    loadOptionsFromRestApi() {
        var resolvableOptionsParameterRequest = {};
        resolvableOptionsParameterRequest['staticProperties'] = this.staticProperties;
        resolvableOptionsParameterRequest['eventProperties'] = this.eventProperties;
        resolvableOptionsParameterRequest['belongsTo'] = this.belongsTo;
        resolvableOptionsParameterRequest['runtimeResolvableInternalId'] = this.staticProperty.properties.internalName;
        this.RestApi.fetchRemoteOptions(resolvableOptionsParameterRequest).success(data => {
                this.$timeout(() => {
                    this.staticProperty.properties.options = data;
                    if (this.staticProperty.properties.options.length > 0) {
                        this.staticProperty.properties.options[0].selected = true;
                        this.loadSavedProperty();
                    }
                    // this.$rootScope.$apply();
                }, 400);


        });
    }

    loadSavedProperty() {
        angular.forEach(this.staticProperty.properties.options, option => {
            if (option.selected) {
                this.staticProperty.properties.currentSelection = option;
            }
        });
    }
}

OneOfRemoteController.$inject= ['RestApi', '$rootScope', '$timeout'];