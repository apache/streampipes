import {Component, EventEmitter, Output} from '@angular/core';
import {AppTransportMonitoringRestService} from "../../services/app-transport-monitoring-rest.service";
import {ParcelInfoModel} from "../../model/parcel-info.model";

@Component({
    selector: 'incoming-view',
    templateUrl: './incoming-view.component.html',
    styleUrls: ['./incoming-view.component.css']
})
export class IncomingViewComponent {

    parcelInfo: ParcelInfoModel;
    showImage: boolean = false;

    constructor(private restService: AppTransportMonitoringRestService) {

    }

    ngOnInit() {
        this.fetchIncomingParcelInfo();
    }

    fetchIncomingParcelInfo() {
        this.restService.getLatestIncomingParcelInfo(0, 0).subscribe(resp => {
            this.parcelInfo = resp;
            this.showImage = true;
        });
    }


}