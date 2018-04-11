import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { Log } from '../model/log.model';
import { map } from 'rxjs/operators';
import { LogRequest } from '../model/logRequest.model';

@Injectable()
export class LogViewRestService {

    constructor(private http: HttpClient) {
    }

    getServerUrl() {
        return '/streampipes-backend/api';
    }

    getLogs(logRequest: LogRequest): Observable<Log[]> {
        return this.http.post(this.getServerUrl() + '/v2/logs', logRequest)
            .pipe(
                map(response => {
                    return response as Log[];
                    }
                )
            );
    }
}


