import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ParcelInfoEventModel} from "../../model/parcel-info-event.model";
import {DetectedBoxModel} from "../../model/detected-box.model";

@Component({
    selector: 'outgoing-view',
    templateUrl: './outgoing-view.component.html',
    styleUrls: ['./outgoing-view.component.css']
})
export class OutgoingViewComponent {

    //@Input() parcelInfo: ParcelInfoEventModel[];
    @Output() detectedBoxes = new EventEmitter<DetectedBoxModel>();

    showImage: boolean = false;

    totalBoxes: number = 0;
    transparentBoxes: number = 0;
    cardboardBoxes: number = 0;

    _parcelInfo: ParcelInfoEventModel[] = [];

    constructor() {

    }

    @Input()
    set parcelInfo(parcelInfo: ParcelInfoEventModel[]) {
        this._parcelInfo = parcelInfo;
        this.showImage = false;
        if (parcelInfo.length > 0) {
            this.calculateBoxCounts();
            this.showImage = true;
        }
        this.emitDetectedBoxes();
    }

    ngOnInit() {

    }

    calculateBoxCounts() {
        let index = this._parcelInfo.length > 1 ? 1 : 0;
        this.totalBoxes = this._parcelInfo[index].number_of_detected_boxes;
        this.transparentBoxes = this._parcelInfo[index].number_of_transparent_boxes;
        this.cardboardBoxes = this._parcelInfo[index].number_of_cardboard_boxes;
    }

    emitDetectedBoxes() {
        let detectedBoxes: DetectedBoxModel = {totalBoxCount: this.totalBoxes, transparentBoxCount: this.transparentBoxes, cardboardBoxCount: this.cardboardBoxes};
        this.detectedBoxes.emit(detectedBoxes);
    }
}