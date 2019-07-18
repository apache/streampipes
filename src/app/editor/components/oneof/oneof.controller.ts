import * as angular from 'angular';

export class OneOfController {

    staticProperty: any;

    constructor() { }

    $onInit() {
    }

    change(option) {
        angular.forEach(this.staticProperty.properties.options, function (o) {
            if (o.elementId == option.elementId) {
                o.selected = true;
            } else {
                o.selected = false;
            }
        });
    }

}