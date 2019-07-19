import * as angular from 'angular';

export class AlternativeController {

    staticProperty: any;

    constructor() {
    }

    $onInit() {
        console.log("alternative");
        console.log(this.staticProperty);
    }

    toggle() {
        console.log("toggle");
        this.staticProperty.properties.alternatives.forEach(sp => {
            console.log(sp);
           if (sp.internalName === this.staticProperty.properties.currentSelection) {
               sp.selected = true;
           }  else {
               sp.selected = false;
           }
        });
    }
}