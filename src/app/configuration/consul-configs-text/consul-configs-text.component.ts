import { Component, EventEmitter, Input, Output } from '@angular/core';
import { StreampipesPeContainer } from "../shared/streampipes-pe-container.model";
import { StreampipesPeContainerConifgs } from "../shared/streampipes-pe-container-configs";
@Component({
    selector: 'consul-configs-text',
    templateUrl: './consul-configs-text.component.html',
    styleUrls: ['./consul-configs-text.component.css']
})
export class ConsulConfigsTextComponent {
    @Input() configuration: StreampipesPeContainerConifgs
    constructor() {    
    }
    
}