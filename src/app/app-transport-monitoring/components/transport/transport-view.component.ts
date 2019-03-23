import {Component, EventEmitter, Output} from '@angular/core';
import {AppTransportMonitoringRestService} from "../../services/app-transport-monitoring-rest.service";
import {ActivityDetectionModel} from "../../model/activity-detection.model";

@Component({
    selector: 'transport-view',
    templateUrl: './transport-view.component.html',
    styleUrls: ['./transport-view.component.css']
})
export class TransportViewComponent {

    processActivities: ActivityDetectionModel;

    constructor(private restService: AppTransportMonitoringRestService) {

    }

    ngOnInit() {
        this.fetchProcessActivities();
    }

    fetchProcessActivities() {
        this.restService.getActivityDetection(0, 0).subscribe(resp => {
            this.processActivities = resp;
        })
    }

    getShakePercentage() {
        return this.processActivities.events.filter(pa => pa.activity == 'shake').length / this.processActivities.events.length;
    }

    getThrowPercentage() {
        return this.processActivities.events.filter(pa => pa.activity == 'throw').length / this.processActivities.events.length;
    }

    getNormalPercentage() {
        return this.processActivities.events.filter(pa => pa.activity == 'normal').length / this.processActivities.events.length;
    }


}