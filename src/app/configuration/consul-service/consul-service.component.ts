import { Component, EventEmitter, Input, Output } from '@angular/core';

import { ConsulService } from '../shared/consul-service.model';

@Component({
    selector: 'consul-service',
    templateUrl: './consul-service.component.html',
    styleUrls: ['./consul-service.component.css']
})
export class ConsulServiceComponent {

    @Input() consulService: ConsulService;
    @Output() updateConsulService: EventEmitter<ConsulService> = new EventEmitter<ConsulService>();
    showConfiguration: boolean = false;

    constructor() {
    }

    toggleConfiguration(): void {
        this.showConfiguration = !this.showConfiguration;
    }

    updateConfiguration(): void {
        this.updateConsulService.emit(this.consulService);
    }

}