export class OptionsController {

    constructor() {

    }

    addOption(options) {
        if (options == undefined) options = [];
        options.push({"name": ""});
    }

    removeOption(options, index) {
        options.splice(index, 1);
    }
}