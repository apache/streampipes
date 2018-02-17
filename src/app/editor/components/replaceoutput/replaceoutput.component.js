import {ReplaceOutputController} from "./replaceoutput.controller";

export let ReplaceOutputComponent = {
    templateUrl: 'app/editor/components/replaceoutput/replaceoutput.tmpl.html',
    bindings: {
        outputStrategy : "="
    },
    controller: ReplaceOutputController,
    controllerAs: 'ctrl'
};
