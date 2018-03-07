export class OneOfRemoteController {

    constructor(RestApi, $rootScope) {
        this.RestApi = RestApi;
        this.$rootScope = $rootScope;
        this.loadSavedProperty();

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
        resolvableOptionsParameterRequest.staticProperties = this.staticProperties;
        resolvableOptionsParameterRequest.eventProperties = this.eventProperties;
        resolvableOptionsParameterRequest.belongsTo = this.belongsTo;
        resolvableOptionsParameterRequest.runtimeResolvableInternalId = this.staticProperty.properties.internalName;
        this.RestApi.fetchRemoteOptions(resolvableOptionsParameterRequest).success(data => {
            this.staticProperty.properties.options = data;
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

OneOfRemoteController.$inject= ['RestApi', '$rootScope'];