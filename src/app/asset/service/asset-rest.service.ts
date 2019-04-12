import {HttpClient} from '@angular/common/http';
import {InfoResult} from '../model/InfoResult';
import {map} from 'rxjs/operators';
import {AuthStatusService} from '../../services/auth-status.service';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {IndexInfo} from '../model/IndexInfo';

@Injectable()
export class AssetRestService {

    constructor(private http: HttpClient,
                private authStatusService: AuthStatusService) {
    }

    private get baseUrl() {
        return '/streampipes-backend';
    }

    private get dataLakeUrl() {
        return this.baseUrl + '/api/v2/users/' + this.authStatusService.email + '/datalake'
    }

    private get fileDownloadUrl() {
        return this.baseUrl + "/api/apps/v1/elasticsearch";
    }

    getAllInfos() {
        return this.http.get<InfoResult[]>(this.dataLakeUrl + "/info");
    }

    getAllIndices() {
        return this.http.get<IndexInfo[]>(this.fileDownloadUrl + "/indices");
    }

    getDataPage(index, itemsPerPage, page) {
        return this.http.get<any>(this.dataLakeUrl + '/data/' + index + '/paging?itemsPerPage=' + itemsPerPage + '&page=' + page);
    }

    getDataPageWithoutPage(index, itemsPerPage) {
        return this.http.get<any>(this.dataLakeUrl + '/data/' + index + '/paging?itemsPerPage=' + itemsPerPage);
    }



}