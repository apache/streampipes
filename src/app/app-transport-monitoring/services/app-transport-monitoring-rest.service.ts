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

    // datalake/data/sp_sp_acceleration?from=1553100861004&to=1553100861941&timestamp=appendedTime

    constructor(private http: HttpClient, private authStatusService: AuthStatusService) {
    }

    getTransportProcesses(): Observable<TransportProcessModel[]> {
        // start / end times of transport
        return undefined;
    }

    getActivityDetection(startTimestamp: number, endTimestamp: number): Observable<ActivityDetectionModel> {
        // parcel activity
        startTimestamp = 0;
        endTimestamp = 2653100861941;
        return this.http.get(this.getParcelActivityUrl(startTimestamp, endTimestamp, "timestamp")).pipe(map (resp => {
            return resp as ActivityDetectionModel
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