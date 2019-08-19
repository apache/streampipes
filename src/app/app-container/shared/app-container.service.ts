import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';


import { InstalledApp } from './installed-app.model';

@Injectable()
export class AppContainerService {

    constructor(private http: HttpClient) {
    }

    getServerUrl() {
        return '/streampipes-backend';
    }

    getInstalledApps(): Observable<InstalledApp[]> {
        //return this.http.get(this.getServerUrl() + '/api/v2/consul').map(res => <InstalledApp[]>res);
        //TODO: Mock
        return this.http.get('/streampipes-apps/') as Observable<InstalledApp[]>;
    }

}