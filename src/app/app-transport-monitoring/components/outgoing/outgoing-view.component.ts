import {Component, EventEmitter, Output} from '@angular/core';
import {AppTransportMonitoringRestService} from "../../services/app-transport-monitoring-rest.service";
import {ParcelInfoModel} from "../../model/parcel-info.model";

@Component({
    selector: 'outgoing-view',
    templateUrl: './outgoing-view.component.html',
    styleUrls: ['./outgoing-view.component.css']
})
export class OutgoingViewComponent {

    parcelInfo: ParcelInfoModel;
    showImage: boolean = false;

    constructor(private restService: AppTransportMonitoringRestService) {

    }

    ngOnInit() {
        this.fetchOutgoingParcelInfo();
    }

    fetchOutgoingParcelInfo() {
        this.restService.getLatestOutgoingParcelInfo(0, 0).subscribe(resp => {
            this.parcelInfo = resp;
            this.showImage = true;
        });

    }


}