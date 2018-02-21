import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
    selector: 'consul-service',
    templateUrl: './consul-service.component.html',
    styleUrls: ['./consul-service.component.css']
})
export class ConsulServiceComponent {

    @Input() serviceData;
    @Output() updateServiceData: EventEmitter<any> = new EventEmitter<any>();
    showConfiguration: boolean = false;

    constructor() {}

    toggleConfiguration() {
        this.showConfiguration = !this.showConfiguration;
    }

    updateConfiguration() {
        this.updateServiceData.emit(this.serviceData);
    }

}