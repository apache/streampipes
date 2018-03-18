import * as angular from 'angular';

export class OneOfController {

    staticProperty: any;

    constructor() {
        this.loadSavedProperty();
    }

    loadSavedProperty() {
        angular.forEach(this.staticProperty.properties.options, option => {
            if (option.selected) {
                this.staticProperty.properties.currentSelection = option;
            }
        });
    }

    toggleOption(option, options) {
        angular.forEach(options, function (o) {
            if (o.elementId == option.elementId) {
                o.selected = true;
            } else {
                o.selected = false;
            }
        });
    }

    exists(option, options) {
        angular.forEach(options, o => {
            if (o.elementId == option.elementId) {
                return o.selected;
            }
        });
    }
}