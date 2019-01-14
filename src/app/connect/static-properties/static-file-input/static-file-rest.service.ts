import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
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



    upload(file: File): Observable<any> {
        const data: FormData = new FormData();
        data.append('file_upload', file, file.name);
        return this.http.post(this.url, data)
            .map(res => {
                return res;
            });
    }

    delete(id: string) {
        return this.http.delete(this.url + '/' + id);
    }
}
