export class AnyController {

    constructor() {

    }

    toggle(option, options) {
        angular.forEach(options, o => {
            if (o.elementId === option.elementId) {
                o.selected = !o.selected;
            }
        });
    }

    exists(option, options) {
        angular.forEach(options, o => {
            if (o.elementId === option.elementId) {
                return option.selected;
            }
        });
    }
}