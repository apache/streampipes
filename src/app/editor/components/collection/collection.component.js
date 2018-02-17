import {CollectionController} from "./collection.controller";

export let CollectionComponent = {
    templateUrl: 'app/editor/components/collection/collection.tmpl.html',
    bindings: {
        staticProperty: "="
    },
    controller: CollectionController,
    controllerAs: 'ctrl'
};
