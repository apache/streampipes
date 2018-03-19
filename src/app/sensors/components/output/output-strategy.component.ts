import {OutputStrategyController} from "./output-strategy.controller";

export let OutputStrategyComponent = {
    templateUrl: 'output-strategy.tmpl.html',
    bindings: {
        disabled : "<",
        strategies : "="
    },
    controller: OutputStrategyController,
    controllerAs: 'ctrl'
};