import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ConsulService } from '../shared/consul-service.model';
import {ConsulServiceConfigs} from '../shared/consul-service-configs'
@Component({
    selector: 'consul-configs-password',
    templateUrl: './consul-configs-password.component.html',
    styleUrls: ['./consul-configs-password.component.css']
})
export class ConsulConfigsPasswordComponent {
    @Input() configuration: ConsulServiceConfigs
    constructor() {
        
    }

}