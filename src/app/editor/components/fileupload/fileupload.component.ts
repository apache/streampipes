import {FileUploadController} from "./fileupload.controller";
declare const require: any;

export let FileUploadComponent = {
    template: require('./fileupload.tmpl.html'),
    bindings: {
        staticProperty: "=",
        inputStreams : "=",
        selectedElement: "=",
        customizeForm: "="
    },
    controller: FileUploadController,
    controllerAs: 'ctrl'
};