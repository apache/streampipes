import {Component, EventEmitter, Input, Output} from '@angular/core';
import {AppTransportMonitoringRestService} from "../../services/app-transport-monitoring-rest.service";
import {ParcelInfoModel} from "../../model/parcel-info.model";
import {ParcelInfoEventModel} from "../../model/parcel-info-event.model";

@Component({
    selector: 'outgoing-view',
    templateUrl: './outgoing-view.component.html',
    styleUrls: ['./outgoing-view.component.css']
})
export class OutgoingViewComponent {

    @Input() parcelInfo: ParcelInfoEventModel[];
    showImage: boolean = false;

    totalBoxes: number = 0;
    transparentBoxes: number = 0;
    cardboardBoxes: number = 0;

    constructor() {

    }

    ngOnInit() {
        this.showImage = true;
        this.totalBoxes = this.parcelInfo[0].number_of_detected_boxes;
        this.transparentBoxes = this.parcelInfo[0].number_of_transparent_boxes;
        this.cardboardBoxes = this.parcelInfo[0].number_of_cardboard_boxes;
        this.showImage = true;
    }
}