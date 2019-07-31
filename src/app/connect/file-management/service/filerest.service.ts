import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import { map } from 'rxjs/operators';
import {HttpClient, HttpRequest} from '@angular/common/http';
import {AuthStatusService} from '../../../services/auth-status.service';


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

    /**
     *  @deprecated use deleteFile
     */
    delete(id: string) {
        return this.http.delete(this.url + '/' + id);
    }

    deleteFile(adapterId: string, id: string) {
        const req = new HttpRequest('DELETE', this.url + '/' + id, adapterId);
        return this.http.request(req);
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
