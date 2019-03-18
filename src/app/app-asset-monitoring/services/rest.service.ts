import {HttpClient} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {map} from "rxjs/operators";
import {Observable} from "rxjs/Observable";
import {AuthStatusService} from "../../services/auth-status.service";
import {DashboardConfiguration} from "../model/dashboard-configuration.model";

@Injectable()
export class RestService {


    constructor(private http: HttpClient, private authStatusService: AuthStatusService) {
    }

    getVisualizablePipelines(): Observable<any> {
        return this.http.get('/visualizablepipeline/_all_docs?include_docs=true');
    }

    getPipeline(pipelineId): Observable<any> {
        return this.http.get('/pipeline/' + pipelineId);
    }

    storeImage(file: File): Observable<any> {
        const data: FormData = new FormData();
        data.append('file_upload', file, file.name);
        return this.http.post(this.imagePath, data)
            .map(res => {
                return res;
            });
    }

    deleteDashboard(dashboardId: string) {
        return this.http.delete(this.url + "/" +dashboardId);
    }

    storeDashboard(dashboardConfig: DashboardConfiguration) {
        return this.http.post(this.url, dashboardConfig);
    }

    getDashboards(): Observable<DashboardConfiguration[]> {
        return this.http.get(this.url).map(response => {
            return response as DashboardConfiguration[];
        });
    }

    getImageUrl(imageName: string): string {
        return this.imagePath + "/" + imageName;
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