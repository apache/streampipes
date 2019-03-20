import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import { map } from 'rxjs/operators';
import {AuthStatusService} from '../../services/auth-status.service';
import {HttpClient} from '@angular/common/http';

@Injectable()
export class FileRestService {


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
            .pipe(map(res => {
                return res;
            }));
    }

    delete(id: string) {
        return this.http.delete(this.url + '/' + id);
    }

    getURLS(): Observable<any> {
        return this.http.get(this.url)
            .pipe(map(res => {
                let result = [];
                let stringArray = res as String[];
                stringArray.forEach(url => {
                    let splitted = url.split("/");
                    let fileName = splitted[splitted.length - 1];
                    result.push({"name": fileName, "url": url})
                });
                return result;
            }));
    }


}
