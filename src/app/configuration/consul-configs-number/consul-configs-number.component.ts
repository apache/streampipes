import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ConsulService } from '../shared/consul-service.model';
import {ConsulServiceConfigs} from '../shared/consul-service-configs'
@Component({
    selector: 'consul-configs-number',
    templateUrl: './consul-configs-number.component.html',
    styleUrls: ['./consul-configs-number.component.css']
})
export class ConsulConfigsNumberComponent {
    @Input() configuration: ConsulServiceConfigs
    constructor() {
        
    }

}