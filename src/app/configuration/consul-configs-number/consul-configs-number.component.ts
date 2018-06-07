import { Component, EventEmitter, Input, Output } from '@angular/core';
import { StreampipesPeContainer } from "../shared/streampipes-pe-container.model";
import { StreampipesPeContainerConifgs } from "../shared/streampipes-pe-container-configs";
@Component({
    selector: 'consul-configs-number',
    templateUrl: './consul-configs-number.component.html',
    styleUrls: ['./consul-configs-number.component.css']
})
export class ConsulConfigsNumberComponent {
    @Input() configuration: StreampipesPeContainerConifgs
    constructor() {
        
    }

}