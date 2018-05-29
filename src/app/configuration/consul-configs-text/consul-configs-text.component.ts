import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ConsulService } from '../shared/consul-service.model';
import {ConsulServiceConfigs} from '../shared/consul-service-configs'
@Component({
    selector: 'consul-configs-text',
    templateUrl: './consul-configs-text.component.html',
    styleUrls: ['./consul-configs-text.component.css']
})
export class ConsulConfigsTextComponent {
    @Input() configuration: ConsulServiceConfigs
    constructor() {
        
    }

}