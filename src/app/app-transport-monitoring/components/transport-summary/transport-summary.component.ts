import {Component, EventEmitter, Input, Output} from '@angular/core';
import {TransportProcessEventModel} from "../../model/transport-process-event.model";
import {TimestampConverterService} from "../../services/timestamp-converter.service";
import {DetectedBoxModel} from "../../model/detected-box.model";

@Component({
    selector: 'transport-summary',
    templateUrl: './transport-summary.component.html',
    styleUrls: ['./transport-summary.component.css']
})
export class TransportSummaryComponent {

    @Input() statusValue: string;
    @Input() label: string;
    @Input() color: string;

    _incomingBoxCount: DetectedBoxModel;
    _outgoingBoxCount: DetectedBoxModel;

    shippedTime: string;
    deliveredTime: string;
    tookTime: string;

    statusMessages: string[] = [];

    errorCode: string = "OK";
    statusColor: string = "green";

    constructor(private timestampConverterService: TimestampConverterService) {

    }

    ngOnInit() {
    }

    @Input()
    set transportProcess(transportProcess: TransportProcessEventModel) {
        this.shippedTime = this.timestampConverterService.convertTimestampHoursOnly(transportProcess.startTime);
        this.deliveredTime = this.timestampConverterService.convertTimestampHoursOnly(transportProcess.endTime);
        this.tookTime = this.timestampConverterService.dateDiffHoursOnly(transportProcess.startTime, transportProcess.endTime);
    }

    @Input()
    set outgoingBoxCount(outgoingBoxCount: DetectedBoxModel) {
        this._outgoingBoxCount = outgoingBoxCount;
        this.calculateDeviations();
    }

    @Input()
    set incomingBoxCount(incomingBoxCount: DetectedBoxModel) {
        this._incomingBoxCount = incomingBoxCount;
        this.calculateDeviations();
    }

    calculateDeviations() {
        if (this._incomingBoxCount && this._outgoingBoxCount) {
            //this.toggleStatusSuccess();
            this.statusMessages = [];
            if (this._outgoingBoxCount.cardboardBoxCount != this._incomingBoxCount.cardboardBoxCount) {
                console.log("transparent box count not equal");
                this.statusMessages.push("Check the number of transparent boxes (Actual: " + this._incomingBoxCount.cardboardBoxCount + ", expected: " + this._outgoingBoxCount.cardboardBoxCount +")");
                this.toggleStatusError();
            }
            if (this._outgoingBoxCount.transparentBoxCount != this._incomingBoxCount.transparentBoxCount) {
                console.log("cardboard box count not equal");
                this.statusMessages.push("Check the number of cardboard boxes (Actual: " + this._incomingBoxCount.transparentBoxCount + ", expected: " + this._outgoingBoxCount.transparentBoxCount +")");
                this.toggleStatusError();
            }
        }
    }

    toggleStatusSuccess() {
        this.errorCode = "OK";
        this.statusColor = "green";
    }

    toggleStatusError() {
        this.errorCode = "Warning";
        this.statusColor = "orange";
    }


}