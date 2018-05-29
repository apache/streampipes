import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ConsulService } from '../shared/consul-service.model';
import {ConsulServiceConfigs} from '../shared/consul-service-configs'
@Component({
    selector: 'consul-configs',
    templateUrl: './consul-configs.component.html',
    styleUrls: ['./consul-configs.component.css']
})
export class ConsulConfigsComponent {
    @Input() consulService: ConsulService;
    @Output() updateConsulService: EventEmitter<ConsulService> = new EventEmitter<ConsulService>();
    constructor() {
        
    }

    updateConfiguration(): void {
        this.updateConsulService.emit(this.consulService);
    }

}