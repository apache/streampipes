import {ReplaceOutputController} from "./replaceoutput.controller";
declare const require: any;

export let ReplaceOutputComponent = {
    template: require('./replaceoutput.tmpl.html'),
    bindings: {
        outputStrategy : "="
    },
    controller: ReplaceOutputController,
    controllerAs: 'ctrl'
};
