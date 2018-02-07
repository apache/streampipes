import {OutputStrategyController} from "./output-strategy.controller";

export let OutputStrategyComponent = {
    templateUrl: 'app/sensors/components/output/output-strategy.tmpl.html',
    bindings: {
        disabled : "<",
        strategies : "<"
    },
    controller: OutputStrategyController,
    controllerAs: 'ctrl'
};