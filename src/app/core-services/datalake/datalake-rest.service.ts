import {HttpClient, HttpRequest} from '@angular/common/http';
import {InfoResult} from '../../core-model/datalake/InfoResult';
import {AuthStatusService} from '../../services/auth-status.service';
import {Injectable} from '@angular/core';
import {PageResult} from '../../core-model/datalake/PageResult';
import {DataResult} from '../../core-model/datalake/DataResult';

@Injectable()
export class DatalakeRestService {

    constructor(private http: HttpClient,
                private authStatusService: AuthStatusService) {
    }

    private get baseUrl() {
        return '/streampipes-backend';
    }

    private get dataLakeUrlV3() {
        return this.baseUrl + '/api/v3/users/' + this.authStatusService.email + '/datalake'
    }


    getAllInfos() {
        return this.http.get<InfoResult[]>(this.dataLakeUrlV3 + "/info");
    }

    getDataPage(index, itemsPerPage, page) {
        return this.http.get<PageResult>(this.dataLakeUrlV3 + '/data/' + index + '/paging?itemsPerPage=' + itemsPerPage + '&page=' + page);
    }

    getDataPageWithoutPage(index, itemsPerPage) {
        return this.http.get<PageResult>(this.dataLakeUrlV3 + '/data/' + index + '/paging?itemsPerPage=' + itemsPerPage);
    }

    getLastData(index, timeunit, value, aggregationTimeUnit, aggregationValue) {
        return this.http.get<DataResult>(this.dataLakeUrlV3 + '/data/' + index + '/last/' + value + '/' + timeunit + '?aggregationUnit=' + aggregationTimeUnit + '&aggregationValue=' + aggregationValue);
    }

    getLastDataAutoAggregation(index, timeunit, value) {
        return this.http.get<DataResult>(this.dataLakeUrlV3 + '/data/' + index + '/last/' + value + '/' + timeunit);
    }

    getData(index, startDate, endDate, aggregationTimeUnit, aggregationValue) {
        return this.http.get<DataResult>(this.dataLakeUrlV3 + '/data/' + index + '/' + startDate + '/' + endDate + '?aggregationUnit=' + aggregationTimeUnit + '&aggregationValue=' + aggregationValue);
    }

    getDataAutoAggergation(index, startDate, endDate) {
        return this.http.get<DataResult>(this.dataLakeUrlV3 + '/data/' + index + '/' + startDate + '/' + endDate);
    }


    /*
        @deprecate
     */
    getFile(index, format) {
        const request = new HttpRequest('GET', this.dataLakeUrlV3 + '/data/' + index + "?format=" + format,  {
            reportProgress: true,
            responseType: 'text'
        });
        return this.http.request(request)
    }

    downloadRowData(index, format) {
        const request = new HttpRequest('GET', this.dataLakeUrlV3 + '/data/' + index + "/download?format=" + format,  {
            reportProgress: true,
            responseType: 'text'
        });
        return this.http.request(request)
    }

    downloadRowDataTimeInterval(index, format, startDate, endDate) {
        const request = new HttpRequest('GET', this.dataLakeUrlV3 + '/data/' + index + '/' + startDate + '/' + endDate + "/download" +
            "?format=" + format, {
            reportProgress: true,
            responseType: 'text'
        });
        return this.http.request(request)
    }

}