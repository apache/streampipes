import {Component, EventEmitter, Output} from '@angular/core';
import {TransportProcessEventModel} from "./model/transport-process-event.model";
import {AppTransportMonitoringRestService} from "./services/app-transport-monitoring-rest.service";
import {ParcelInfoEventModel} from "./model/parcel-info-event.model";

@Component({
    selector: 'app-transport-monitoring',
    templateUrl: './app-transport-monitoring.component.html',
    styleUrls: ['./app-transport-monitoring.component.css']
})
export class AppTransportMonitoringComponent {


    selectedIndex: number = 0;
    @Output() appOpened = new EventEmitter<boolean>();

    incomingExpanded: boolean = true;
    transportExpanded: boolean = true;
    outgoingExpanded: boolean = true;
    summaryExpanded: boolean = true;

    transportProcessSelected: boolean = false;
    selectedTransportProcess: TransportProcessEventModel;

    incomingParcelInfo: ParcelInfoEventModel[];
    outgoingParcelInfo: ParcelInfoEventModel[];

    incomingParcelInfoPresent: boolean = false;
    outgoingParcelInfoPresent: boolean = false;

    constructor(private restService: AppTransportMonitoringRestService) {

    }

    ngOnInit() {
        this.appOpened.emit(true);
    }

    selectedIndexChange(index: number) {
        this.selectedIndex = index;
    }

    selectTransportProcess(transportProcess: TransportProcessEventModel) {
        this.selectedTransportProcess = transportProcess;
        this.transportProcessSelected = true;
        this.fetchOutgoingParcelInfo();
        this.fetchIncomingParcelInfo();
    }

    fetchOutgoingParcelInfo() {
        this.restService.getOutgoingParcelInfo(this.selectedTransportProcess.startTime, this.selectedTransportProcess.endTime).subscribe(resp => {
            this.outgoingParcelInfo = resp.events;
            this.outgoingParcelInfoPresent = true;
        });
    }

    fetchIncomingParcelInfo() {
        this.restService.getIncomingParcelInfo(this.selectedTransportProcess.startTime, this.selectedTransportProcess.endTime).subscribe(resp => {
            this.incomingParcelInfo = resp.events;
            this.incomingParcelInfoPresent = true;
        });
    }


}