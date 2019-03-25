import {Component, EventEmitter, Input, Output} from '@angular/core';
import {AppTransportMonitoringRestService} from "../../services/app-transport-monitoring-rest.service";
import {ActivityDetectionModel} from "../../model/activity-detection.model";
import {ParcelInfoEventModel} from "../../model/parcel-info-event.model";
import {TransportProcessEventModel} from "../../model/transport-process-event.model";

@Component({
    selector: 'transport-view',
    templateUrl: './transport-view.component.html',
    styleUrls: ['./transport-view.component.css']
})
export class TransportViewComponent {

    @Input() transportProcess: TransportProcessEventModel;

    processActivities: ActivityDetectionModel;

    activitiesPresent: boolean = false;
    fallActivities: number = 0;
    normalActivitiesTotalTime: number = 0;
    shakeActivitiesTotalTime: number = 0;

    constructor(private restService: AppTransportMonitoringRestService) {

    }

    ngOnInit() {
        this.fetchProcessActivities();
    }

    fetchProcessActivities() {
        this.restService.getActivityDetection(this.transportProcess.startTime, this.transportProcess.endTime).subscribe(resp => {
            this.processActivities = resp;
            this.activitiesPresent = true;
            this.fallActivities = this.filter('fall');
            this.normalActivitiesTotalTime = this.filter('shake');
            this.shakeActivitiesTotalTime = this.filter('fall');
        })
    }

    filter(activity: string) {
        return this.processActivities.events.filter(pa => pa.activity == activity).length / this.processActivities.events.length;
    }

    getShakePercentage() {

    }

    getThrowPercentage() {
        return this.processActivities.events.filter(pa => pa.activity == 'throw').length / this.processActivities.events.length;
    }

    getNormalPercentage() {
        return this.processActivities.events.filter(pa => pa.activity == 'normal').length / this.processActivities.events.length;
    }


}