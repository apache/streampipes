import {OutputStrategyController} from "./output-strategy.controller";
declare const require: any;

export let OutputStrategyComponent = {
    template: require('./output-strategy.tmpl.html'),
    bindings: {
        disabled : "<",
        strategies : "="
    },
    controller: OutputStrategyController,
    controllerAs: 'ctrl'
};