import {ReplaceOutputController} from "./replaceoutput.controller";

export let ReplaceOutputComponent = {
    templateUrl: 'replaceoutput.tmpl.html',
    bindings: {
        outputStrategy : "="
    },
    controller: ReplaceOutputController,
    controllerAs: 'ctrl'
};
