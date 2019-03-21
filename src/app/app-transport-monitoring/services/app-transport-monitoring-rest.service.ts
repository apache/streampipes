import {HttpClient} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {map} from "rxjs/operators";
import {Observable} from "rxjs";
import {AuthStatusService} from "../../services/auth-status.service";
import {TransportProcessModel} from "../model/transport-process.model";
import {ActivityDetectionModel} from "../model/activity-detection.model";
import {AmbientLightModel} from "../model/ambient-light.model";
import {ParcelMetricsModel} from "../model/parcel-metrics.model";
import {ParcelInfoModel} from "../model/parcel-info.model";

@Injectable()
export class AppTransportMonitoringRestService {


    constructor(private http: HttpClient, private authStatusService: AuthStatusService) {
    }

    getTransportProcesses(): Observable<TransportProcessModel[]> {
        // start / end times of transport
        return undefined;
    }

    getActivityDetection(startTimestamp: number, endTimestamp: number): Observable<ActivityDetectionModel[]> {
        // parcel activity
        return this.http.get("/assets/Activity.json").pipe(map (resp => {
            return resp as ActivityDetectionModel[]
        }));
    }

    getBoxOpenModel(startTimestamp: number, endTimestamp: number): Observable<AmbientLightModel[]> {
        // box open/close
        return undefined;
    }

    getLatestOutgoingParcelInfo(startTimestamp: number, endTimestamp: number): Observable<ParcelInfoModel> {
        // parcel info: image, number of parcels, number of boxes
        return this.http.get("/assets/Camera.json").pipe(map(resp => {
            return resp as ParcelInfoModel[];
        })).pipe(map(res => {
           return res[0];
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

    getVisualizablePipelines(): Observable<any> {
        return this.http.get('/visualizablepipeline/_all_docs?include_docs=true');
    }

    private get baseUrl() {
        return '/streampipes-backend';
    }

    private get url() {
        return this.baseUrl + '/api/v2/users/' + this.authStatusService.email + '/asset-dashboards'
    }

    private get imagePath() {
        return this.url + "/images";
    }

}