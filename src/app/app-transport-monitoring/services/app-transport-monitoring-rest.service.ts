import {HttpClient} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {map} from "rxjs/operators";
import {Observable, of} from "rxjs";
import {AuthStatusService} from "../../services/auth-status.service";
import {ActivityDetectionModel} from "../model/activity-detection.model";
import {ParcelInfoModel} from "../model/parcel-info.model";
import {ParcelInfoEventModel} from "../model/parcel-info-event.model";
import {OldEventModel} from "../model/old-event.model";
import {TransportProcessEventModel} from "../model/transport-process-event.model";
import {TransportProcessModel} from "../model/transport-process.model";
import {OpenBoxModel} from "../model/open-box.model";

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
            return resp as ActivityDetectionModel
        }));
    }

    getBoxOpenModel(startTimestamp: number, endTimestamp: number): Observable<OpenBoxModel> {
        // box open/close
        return this.http.get(this.getOpenBoxUrl(startTimestamp, endTimestamp, "timestamp")).pipe(map (resp => {
            return resp as OpenBoxModel
        }));
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

    truncateIncomingGoodsDb() {
        let index = "sp_incoming_goods";
        this.http.get(this.getDeleteUrl(index)).subscribe();
    }

    truncateOutgoingGoodsDb() {
        let index = "sp_outgoing_goods";
        this.http.get(this.getDeleteUrl(index)).subscribe();
    }

    truncateTransportProcessDb() {
        let index = "sp_transport_processes";
        this.http.get(this.getDeleteUrl(index)).subscribe();
    }

    truncateParcelActivitiesDb() {
        let index = "sp_parcel_activity";
        this.http.get(this.getDeleteUrl(index)).subscribe();
    }

    truncateParcelOpenBoxDb() {
        let index = "sp_parcel_open_box";
        this.http.get(this.getDeleteUrl(index)).subscribe();
    }

    getDeleteUrl(index: string) {
        return this.baseUrl + '/api/v2/users/' + this.authStatusService.email + '/datalake/delete/' +index;
    }

    getTransportProcessesUrl(): string {
        return this.url + "/sp_transport_processes";
    }

    getOutgoingParcelInfoUrl(from: number, to:number, timestampProperty: string): string {
        return this.url + "/sp_outgoing_goods?from=" +(from - 6000) +"&to=" +(from-1000) +"&timestamp=" +timestampProperty;
    }

    getIncomingParcelInfoUrl(from: number, to:number, timestampProperty: string): string {
        return this.url + "/sp_incoming_goods?from=" +(to+1000) +"&to=" +(to + 6000) +"&timestamp=" +timestampProperty;
    }

    getParcelActivityUrl(from: number, to: number, timestampProperty: string): string {
        return this.url + "/sp_parcel_activity?from=" +from +"&to=" +to +"&timestamp=" +timestampProperty;
    }

    getOpenBoxUrl(from: number, to: number, timestampProperty: string): string {
        return this.url + "/sp_parcel_open_box?from=" +from +"&to=" +to +"&timestamp=" +timestampProperty;
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