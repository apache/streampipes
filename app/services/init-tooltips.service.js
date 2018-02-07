export class InitTooltips {
    constructor() {}

    initTooltips() {
        return function () {
            $('.tt').tooltip();
        }
    };
}
