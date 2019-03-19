import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable()
export class KviVisualizationService {

    constructor(private http: HttpClient) {
    }

    getServerUrl() {
        return '/streampipes-backend';
    }


    getKviData(): Observable<any> {
        return this.http.get(this.getServerUrl() + '/api/v2/couchdb/kvi')
            .pipe(map(response => {
                return response;
            }));
    }

}