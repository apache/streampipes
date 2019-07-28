import * as angular from 'angular';

export class SecretController {

    staticProperty: any;
    selectedEventProperty: any;
    customizeForm: any;

    $scope: any;
    $rootScope: any;

    constructor($scope, $rootScope) {
        this.$scope = $scope;
        this.$rootScope = $rootScope;
    }

    $onInit() {
    }

    notifyListeners() {
        this.$rootScope.$emit(
            this.staticProperty.properties.internalName,
            this.customizeForm[this.staticProperty.properties.internalName].$valid);
    }

    applyChanges() {
        this.staticProperty.properties.encrypted=false;
    }
}
