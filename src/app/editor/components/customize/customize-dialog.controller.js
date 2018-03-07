export class CustomizeDialogController {

    constructor() {
    }

    getMappingProperty(mapsTo) {
        var sps;
        angular.forEach(this.selectedElement.staticProperties, sp => {
            if (sp.properties.internalName == mapsTo) {
                console.log("match");
                sps = sp;
            }
        });
        return sps;
    }
}