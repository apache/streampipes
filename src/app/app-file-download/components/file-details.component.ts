import {FileDetailsController} from "./file-details.controller";

export let FileDetails = {
    restrict: 'E',
    templateUrl: 'file-details.tmpl.html',
    bindings: {
        file: "=",
        getFiles: "&"
    },
    controller: FileDetailsController,
    controllerAs: 'ctrl'
};
