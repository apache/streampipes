import {Component, EventEmitter, Input, Output} from '@angular/core';
import {TransportProcessEventModel} from "../../model/transport-process-event.model";
import {TimestampConverterService} from "../../services/timestamp-converter.service";

@Component({
    selector: 'transport-summary',
    templateUrl: './transport-summary.component.html',
    styleUrls: ['./transport-summary.component.css']
})
export class TransportSummaryComponent {

    @Input() statusValue: string;
    @Input() label: string;
    @Input() color: string;

    @Input() transportProcess: TransportProcessEventModel;

    shippedTime: string;
    deliveredTime: string;
    tookTime: string;

    constructor(private timestampConverterService: TimestampConverterService) {

    }

    ngOnInit() {
        this.shippedTime = this.timestampConverterService.convertTimestampHoursOnly(this.transportProcess.startTime);
        this.deliveredTime = this.timestampConverterService.convertTimestampHoursOnly(this.transportProcess.endTime);
        this.tookTime = this.timestampConverterService.dateDiffHoursOnly(this.transportProcess.startTime, this.transportProcess.endTime);
    }


}