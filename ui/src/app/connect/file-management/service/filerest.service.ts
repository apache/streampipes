/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
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
