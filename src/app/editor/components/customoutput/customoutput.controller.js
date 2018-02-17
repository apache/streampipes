export class CustomOutputController {

    constructor() {

    }

    toggle(property) {
    if (this.exists(property)) {
        this.remove(property);
    } else {
        this.add(property);
    }
}

    exists(property) {
    if (!this.outputStrategy.properties.eventProperties) {
        return false;
    } else {
        var valid = false;
        angular.forEach(this.outputStrategy.properties.eventProperties, p => {
            if (property.properties.elementId === p.properties.elementId) {
                valid = true;
            }
        });
        return valid;
    }
}

    add(property) {
        if (!this.outputStrategy.properties.eventProperties) {
            this.outputStrategy.properties.eventProperties = [];
        }
        this.outputStrategy.properties.eventProperties.push(property);

    }

    remove(property) {
        var index = this.outputStrategy.properties.eventProperties.indexOf(property);
        this.outputStrategy.properties.eventProperties.splice(index, 1);
    }
}