import * as angular from 'angular';

export class PropertySelectionController {

    outputStrategy: any;
    eventProperty: any;
    layer: any;

    toggle(runtimeId) {
        if (this.exists(runtimeId)) {
            this.remove(runtimeId);
        } else {
            this.add(runtimeId);
        }
    }

    exists(runtimeId) {
        return this.outputStrategy.properties.selectedPropertyKeys.some(e => e === runtimeId);
    }

    add(runtimeId) {
        this.outputStrategy.properties.selectedPropertyKeys.push(runtimeId);
        // This is needed to trigger update of scope
        this.outputStrategy.properties.selectedPropertyKeys = this.outputStrategy.properties.selectedPropertyKeys.filter(el => {return true;});
    }

    remove(runtimeId) {
        this.outputStrategy.properties.selectedPropertyKeys =  this.outputStrategy.properties.selectedPropertyKeys.filter(el => { return el != runtimeId; });
    }
}