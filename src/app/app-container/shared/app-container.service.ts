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
        return <Observable<InstalledApp[]>>Observable.create(observer => {
            observer.next([{
                bundleUrl: 'http://localhost:8082/assets/lib/apps/main.js',
                moduleName: 'AppModule',
                selector: 'test-lol'
            },
            {
                bundleUrl: 'http://localhost:8082/assets/lib/apps/test.bundle.min.js',
                moduleName: 'AppModule',
                selector: 'test-lol'
            }]);
            observer.complete();
        });
    }

}