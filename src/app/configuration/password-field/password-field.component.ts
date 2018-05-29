import { Component, EventEmitter, Input, Output } from '@angular/core';

import { ConsulService } from '../shared/consul-service.model';
import { ConsulServiceComponent } from '../consul-service/consul-service.component';
import { ConsulServiceConfigs } from '../shared/consul-service-configs';

@Component({
    selector: 'password-field',
    templateUrl: './password-field.component.html',
    styleUrls: ['./password-field.component.css']
})
export class PasswordFieldComponent {
    @Input() configuration: ConsulServiceConfigs;
    @Output() updateConsulService: EventEmitter<ConsulService> = new EventEmitter<ConsulService>();
    constructor() {
        
    }


}