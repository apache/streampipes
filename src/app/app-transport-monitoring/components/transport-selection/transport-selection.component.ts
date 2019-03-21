import {Component, EventEmitter, Output} from '@angular/core';
import {TransportProcessModel} from "../../model/transport-process.model";
import {AppTransportMonitoringRestService} from "../../services/app-transport-monitoring-rest.service";

@Component({
    selector: 'transport-selection',
    templateUrl: './transport-selection.component.html',
    styleUrls: ['./transport-selection.component.css']
})
export class TransportSelectionComponent {

    transportProcesses: TransportProcessModel[] = [];


    constructor(private restService: AppTransportMonitoringRestService) {

    }

    ngOnInit() {

    }

    fetchTransportProcesses() {
        return this.restService.getTransportProcesses().subscribe(resp => {
           this.transportProcesses = resp;
        });
    }


}