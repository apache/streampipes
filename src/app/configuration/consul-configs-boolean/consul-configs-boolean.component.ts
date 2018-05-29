import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ConsulService } from '../shared/consul-service.model';
import {ConsulServiceConfigs} from '../shared/consul-service-configs'
@Component({
    selector: 'consul-configs-boolean',
    templateUrl: './consul-configs-boolean.component.html',
    styleUrls: ['./consul-configs-boolean.component.css']
})
export class ConsulConfigsBooleanComponent {
    @Input() configuration: ConsulServiceConfigs
    constructor() {
        
    }

}