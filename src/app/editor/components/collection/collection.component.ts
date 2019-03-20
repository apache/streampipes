import {CollectionController} from "./collection.controller";
declare const require: any;

export let CollectionComponent = {
    template: require('./collection.tmpl.html'),
    bindings: {
        staticProperty: "="
    },
    controller: CollectionController,
    controllerAs: 'ctrl'
};
