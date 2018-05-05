import * as angular from 'angular';

export class PipelineElementIconStandController {

    RestApi: any;
    availableOptions: any;
    selectedOptions: any;
    options: any;
    activeType: any;
    currentElementName: any;
    EditorDialogManager: any;

    constructor($scope, $rootScope, RestApi, EditorDialogManager) {
        this.RestApi = RestApi;
        this.availableOptions = [];
        this.selectedOptions = [];
        this.options = [];
        this.EditorDialogManager = EditorDialogManager;
        this.loadOptions();

        $scope.$watch(() => this.activeType, () => {
            this.selectAllOptions();
        });

    }

    openHelpDialog(pipelineElement) {
        this.EditorDialogManager.openHelpDialog(pipelineElement);
    }

    updateMouseOver(elementName) {
        this.currentElementName = elementName;
    }

    loadOptions(type?) {
        this.RestApi.getEpCategories()
            .then(s => {
                this.handleCategoriesSuccess("stream", s);
                this.handleCategoriesSuccess("set", s);
            });

        this.RestApi.getEpaCategories()
            .then(s => this.handleCategoriesSuccess("sepa", s));

        this.RestApi.getEcCategories()
            .then(s => this.handleCategoriesSuccess("action", s));

    };

    getOptions(type) {
        this.selectAllOptions();
        return this.availableOptions[type];
    }


    handleCategoriesSuccess(type, result) {
        this.availableOptions[type] = result.data;
        this.selectAllOptions();
    }

    toggleFilter(option) {
        this.selectedOptions = [];
        this.selectedOptions.push(option.type);
    }

    optionSelected(option) {
        return this.selectedOptions.indexOf(option.type) > -1;
    }

    selectAllOptions() {
        this.selectedOptions = [];
        angular.forEach(this.availableOptions[this.activeType], o => {
            this.selectedOptions.push(o.type);
        });
    }

    deselectAllOptions() {
        this.selectedOptions = [];
    }
}

PipelineElementIconStandController.$inject = ['$scope', '$rootScope', 'RestApi', 'EditorDialogManager'];