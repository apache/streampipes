import { Component, Input } from '@angular/core';

@Component({
    selector: 'consul-service',
    templateUrl: './consul-service.component.html',
    styleUrls: ['./consul-service.component.css']
})
export class ConsulServiceComponent {

    @Input() serviceData;
    showConfiguration: boolean = false;

    constructor() {}

    toggleConfiguration() {
        this.showConfiguration = !this.showConfiguration;
    }

}