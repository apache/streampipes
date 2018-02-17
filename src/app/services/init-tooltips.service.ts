import * as $ from "jquery";

export class InitTooltips {
    constructor() {}

    initTooltips() {
        return function () {
            (<any>$('.tt')).tooltip();
        }
    };
}
