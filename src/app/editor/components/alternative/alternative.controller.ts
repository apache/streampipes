import * as angular from 'angular';

export class AlternativeController {

    staticProperty: any;

    constructor() {
    }

    $onInit() {
    }

    toggle() {
        this.staticProperty.properties.alternatives.forEach(sp => {
           if (sp.internalName === this.staticProperty.properties.currentSelection) {
               sp.selected = true;
           }  else {
               sp.selected = false;
           }
        });
    }
}