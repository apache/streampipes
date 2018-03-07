export class PipelineElementIconStandController {

    constructor($scope, RestApi) {
        this.RestApi = RestApi;
        this.availableOptions = [];
        this.selectedOptions = [];
        this.options = [];
        this.loadOptions();

        $scope.$watch(() => this.activeType, () => {
            this.selectAllOptions();
        });
    }

    loadOptions(type) {
        this.RestApi.getEpCategories()
            .then(s => this.handleCategoriesSuccess("stream", s));

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

PipelineElementIconStandController.$inject = ['$scope', 'RestApi'];