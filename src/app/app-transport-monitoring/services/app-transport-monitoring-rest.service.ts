import {HttpClient} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {map} from "rxjs/operators";
import {Observable, of} from "rxjs";
import {AuthStatusService} from "../../services/auth-status.service";
import {ActivityDetectionModel} from "../model/activity-detection.model";
import {AmbientLightModel} from "../model/ambient-light.model";
import {ParcelMetricsModel} from "../model/parcel-metrics.model";
import {ParcelInfoModel} from "../model/parcel-info.model";
import {ParcelInfoEventModel} from "../model/parcel-info-event.model";
import {OldEventModel} from "../model/old-event.model";
import {TransportProcessEventModel} from "../model/transport-process-event.model";
import {TransportProcessModel} from "../model/transport-process.model";

@Injectable()
export class AppTransportMonitoringRestService {

    // datalake/data/sp_sp_acceleration?from=1553100861004&to=1553100861941&timestamp=appendedTime
    startTimestampDefault: number = 0;
    endTimestampDefault: number = 2653100861941;

    constructor(private http: HttpClient, private authStatusService: AuthStatusService) {
    }

    getTransportProcesses(): Observable<TransportProcessEventModel[]> {
        return this.http.get(this.getTransportProcessesUrl()).pipe(map(resp => {
           let transportProcessModel:TransportProcessModel = resp as TransportProcessModel;
           return transportProcessModel.events;
        }));
        // let transportProcesses: TransportProcessEventModel[] = [];
        // transportProcesses.push({startTime: this.startTimestampDefault, endTime: this.endTimestampDefault});
        // transportProcesses.push({startTime: this.startTimestampDefault, endTime: this.endTimestampDefault});
        // return of(transportProcesses);
    }

    getActivityDetection(startTimestamp: number, endTimestamp: number): Observable<ActivityDetectionModel> {
        // parcel activity
        return this.http.get(this.getParcelActivityUrl(startTimestamp, endTimestamp, "timestamp")).pipe(map (resp => {
            console.log(resp);
            return resp as ActivityDetectionModel
        }));
    }

    getBoxOpenModel(startTimestamp: number, endTimestamp: number): Observable<AmbientLightModel[]> {
        // box open/close
        return undefined;
    }

    getOutgoingParcelInfo(startTimestamp: number, endTimestamp: number): Observable<ParcelInfoModel> {
        return this.http.get(this.getOutgoingParcelInfoUrl(startTimestamp, endTimestamp, "timestamp"))
             .pipe(map (resp => {
             return resp as ParcelInfoModel
         }));
        //return this.getLatestOutgoingParcelInfo(0, 0);
    }

    getIncomingParcelInfo(startTimestamp: number, endTimestamp: number): Observable<ParcelInfoModel> {
        return this.http.get(this.getIncomingParcelInfoUrl(startTimestamp, endTimestamp, "timestamp"))
            .pipe(map (resp => {
                return resp as ParcelInfoModel
            }));
        //return this.getLatestOutgoingParcelInfo(0, 0);
    }

    getLatestOutgoingParcelInfo(startTimestamp: number, endTimestamp: number): Observable<ParcelInfoModel> {
        // parcel info: image, number of parcels, number of boxes
        return this.http.get("/assets/Camera.json").pipe(map(resp => {
            return resp as OldEventModel[];
        })).pipe(map(res => {
            let total: string = "0";
            let image: string = res[0].image;
            let events: ParcelInfoEventModel[] = [];
            events.push({date: "a", timestamp: 0, segmentationImage: image, number_of_transparent_boxes: 0, number_of_detected_boxes: 0, number_of_cardboard_boxes: 0});
            events.push({date: "a", timestamp: 0, segmentationImage: image, number_of_transparent_boxes: 0, number_of_detected_boxes: 0, number_of_cardboard_boxes: 0});
            return {total: total, events: events };
        }));
    }

    getLatestOutgoingParcelMetrics(startTimestamp: number, endTimestamp: number): Observable<ParcelMetricsModel> {
        // parcel metrics: output from felix
        return undefined;
    }

    getLatestIncomingParcelInfo(startTimestamp: number, endTimestamp: number): Observable<ParcelInfoModel> {
        return this.http.get("/assets/Camera.json").pipe(map(resp => {
            return (resp as ParcelInfoModel[])[1];
        }));
    }

    getLatestIncomingParcelMetrics(startTimestamp: number, endTimestamp: number): Observable<ParcelMetricsModel> {
        return undefined;
    }

    getTransportProcessesUrl(): string {
        return this.url + "/sp_transport_processes";
    }

    getOutgoingParcelInfoUrl(from: number, to:number, timestampProperty: string): string {
        return this.url + "/sp_new_box_data?from=" +(from - 5000) +"&to=" +from +"&timestamp=" +timestampProperty;
    }

    getIncomingParcelInfoUrl(from: number, to:number, timestampProperty: string): string {
        return this.url + "/sp_incoming_goods?from=" +to +"&to=" +(to + 5000) +"&timestamp=" +timestampProperty;
    }


    getParcelActivityUrl(from: number, to: number, timestampProperty: string): string {
        return this.url + "/sp_dominik_xdk?from=" +from +"&to=" +to +"&timestamp=" +timestampProperty;
    }

    private get baseUrl() {
        return '/streampipes-backend';
    }

    private get url() {
        return this.baseUrl + '/api/v2/users/' + this.authStatusService.email + '/datalake/data'
    }

    private get imagePath() {
        return this.url + "/images";
    }

}