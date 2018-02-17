import {FileDetailsController} from "./file-details.controller";

export let FileDetails = {
    restrict: 'E',
    templateUrl: 'app/app-file-download/directives/file-details.tmpl.html',
    scope: {},
    controller: FileDetailsController,
    controllerAs: 'FileDetailsController'
};
