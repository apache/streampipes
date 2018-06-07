import { Component, EventEmitter, Input, Output } from '@angular/core';
import { StreampipesPeContainer } from "../shared/streampipes-pe-container.model";
import { StreampipesPeContainerConifgs } from "../shared/streampipes-pe-container-configs";
@Component({
    selector: 'consul-configs-boolean',
    templateUrl: './consul-configs-boolean.component.html',
    styleUrls: ['./consul-configs-boolean.component.css']
})
export class ConsulConfigsBooleanComponent {
    @Input() configuration: StreampipesPeContainerConifgs
    constructor() {
        
    }

}