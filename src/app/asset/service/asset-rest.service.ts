import {HttpClient} from '@angular/common/http';
import {AuthStatusService} from '../../services/auth-status.service';
import {InfoResult} from '../model/InfoResult';
import {map} from 'rxjs/operators';

export class AssetRestService {

    constructor(private http: HttpClient, private authStatusService: AuthStatusService) {

    }

    getAllInfos() {
        this.http.get(this.url + "/info").pipe(map (resp => {
            return resp as InfoResult[]
        }));
    }

    private get url() {
        return this.baseUrl + '/api/v2/users/' + this.authStatusService.email + '/datalake'
    }

    private get baseUrl() {
        return '/streampipes-backend';
    }

}