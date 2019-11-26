/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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


    /**
     *  @deprecated use uploadFile
     */
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

    uploadFile(adapterId, file: File): Observable<HttpEvent<any>> {
        const data: FormData = new FormData();
        data.append('file_upload', file, file.name);
        data.append('appId', adapterId);

        let params = new HttpParams();
        const options = {
            params: params,
            reportProgress: true,
        };

        //let url = this.url + '/' + adapterId;
        const req = new HttpRequest('POST', this.url, data, options);
        return this.http.request(req);
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
}
