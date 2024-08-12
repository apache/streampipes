import { Injectable } from '@angular/core';
import { RuntimeResolvableTreeInputStaticProperty } from '@streampipes/platform-services';

@Injectable({
    providedIn: 'root',
})
export class StaticTreeInputServiceService {
    constructor() {}

    getSelectedNodeIndex(
        staticProperty: RuntimeResolvableTreeInputStaticProperty,
        internalNodeName: string,
    ) {
        return staticProperty.selectedNodesInternalNames.indexOf(
            internalNodeName,
        );
    }
}
