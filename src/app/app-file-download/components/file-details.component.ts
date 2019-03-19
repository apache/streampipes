import {FileDetailsController} from "./file-details.controller";

declare const require: any;

export let FileDetails = {
    restrict: 'E',
    template: require('./file-details.tmpl.html'),
    bindings: {
        file: "=",
        getFiles: "&"
    },
    controller: FileDetailsController,
    controllerAs: 'ctrl'
};
