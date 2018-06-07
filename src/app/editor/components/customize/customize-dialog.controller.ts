import * as angular from 'angular';

export class CustomizeDialogController {

    selectedElement: any;
    staticProperty: any;
    customizeForm: any;

    constructor() {
    }


    getMappingProperty(mapsTo) {
        var sps;
        angular.forEach(this.selectedElement.staticProperties, sp => {
            if (sp.properties.internalName == mapsTo) {
                sps = sp;
            }
        });
        return sps;
    }
}