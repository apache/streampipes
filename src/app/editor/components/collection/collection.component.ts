import {CollectionController} from "./collection.controller";

export let CollectionComponent = {
    templateUrl: 'collection.tmpl.html',
    bindings: {
        staticProperty: "="
    },
    controller: CollectionController,
    controllerAs: 'ctrl'
};
