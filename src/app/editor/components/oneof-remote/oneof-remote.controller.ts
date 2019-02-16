import * as angular from 'angular';

export class OneOfRemoteController {

    RestApi: any;
    $rootScope: any;
    staticProperty: any;
    staticProperties: any;
    eventProperties: any;
    belongsTo: any;
    currentlyLinkedProperty: any;

    showOptions: boolean = false;

    constructor(RestApi, $rootScope) {
        this.RestApi = RestApi;
        this.$rootScope = $rootScope;
    }

    $onInit() {
        if (this.staticProperty.properties.options.length == 0) {
            this.loadOptionsFromRestApi();
        } else {
            this.loadSavedProperty();
        }

        angular.forEach(this.staticProperties, sp => {
            if (sp.properties.internalName === this.staticProperty.properties.linkedMappingPropertyId) {
                this.currentlyLinkedProperty = sp.properties.mapsTo;
            }
        });

        this.$rootScope.$on(this.staticProperty.properties.linkedMappingPropertyId, () => {
            this.showOptions = false;
            angular.forEach(this.staticProperties, sp => {
                if (sp.properties.internalName === this.staticProperty.properties.linkedMappingPropertyId) {
                    if (this.currentlyLinkedProperty !== sp.properties.mapsTo) {
                        console.log("reloading");
                        this.currentlyLinkedProperty = sp.properties.mapsTo;
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

        this.showOptions = false;
        this.RestApi.fetchRemoteOptions(resolvableOptionsParameterRequest).then(msg => {
            let data = msg.data;
            this.staticProperty.properties.options = data;
                this.staticProperty.properties.options[0].selected = true;
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