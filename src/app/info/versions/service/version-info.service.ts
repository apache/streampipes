import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { VersionInfo } from './version-info.model';
import { SystemInfo } from "./system-info.model";

@Injectable()
export class VersionInfoService {

    constructor(private http: HttpClient) {
    }

    getServerUrl() {
        return '/streampipes-backend';
    }

    getVersionInfo(): Observable<VersionInfo> {
        return this.http.get(this.getServerUrl() + '/api/v2/info/versions')
            .pipe(
                map(response => {
                    return response as VersionInfo;
                })
            );
    }

    getSysteminfo(): Observable<SystemInfo> {
        return this.http.get(this.getServerUrl() + '/api/v2/info/system')
            .pipe(
                map(response => {
                    return response as SystemInfo;
                })
            );
    }

}