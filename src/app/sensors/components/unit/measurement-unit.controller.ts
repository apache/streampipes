import * as angular from 'angular';

export class MeasurementUnitController {

    query: any;
    selectedItem: any;
    items: any;
    property: any;

    constructor(MeasurementUnitsService) {
        this.query = {};
        this.selectedItem = "";
        this.items = MeasurementUnitsService.getUnits();

        if (this.property != undefined && this.property != "") {
            angular.forEach(this.items, function (item) {
                if (item.resource == this.property) this.selectedItem = item;
            });
        }
    }

    querySearch(query) {
        var results = [];

        angular.forEach(this.items, function (item) {
            if (query == undefined || item.label.substring(0, query.length) === query) results.push(item);
        })

        return results;
    }

    searchTextChange(text) {

    }

    selectedItemChange(item) {
        if (item != undefined) this.property = item.resource;
    }

}

MeasurementUnitController.$inject = ['MeasurementUnitsService'];
