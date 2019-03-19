import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient, HttpEvent, HttpParams, HttpRequest} from '@angular/common/http';
import {AuthStatusService} from '../../../services/auth-status.service';

@Injectable()
export class StaticFileRestService {


    constructor(
        private http: HttpClient,
        private authStatusService: AuthStatusService
    ) {
    }

    private get baseUrl() {
        return '/streampipes-connect';
    }

    private get url() {
        // TODO
        return this.baseUrl + '/api/v1/' + this.authStatusService.email + '/master/file'
    }



    upload(file: File): Observable<HttpEvent<any>> {
        const data: FormData = new FormData();
        data.append('file_upload', file, file.name);

        let params = new HttpParams();
        const options = {
            params: params,
            reportProgress: true,
        };

        const req = new HttpRequest('POST', this.url, data, options);
        return this.http.request(req);
           }

    delete(id: string) {
        return this.http.delete(this.url + '/' + id);
    }
}
