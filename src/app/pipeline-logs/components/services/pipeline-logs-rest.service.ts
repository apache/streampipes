import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthStatusService } from '../../../services/auth-status.service';
import { map } from 'rxjs/operators';

@Injectable()
export class PipelineLogsRestService {

    constructor(private http: HttpClient, private authStatusService: AuthStatusService) {

    }

    getServerUrl() {
        return '/streampipes-backend/api';
    }

    getPipelineElement(pipelineID: string) {
        return this.http.get(this.getServerUrl() + '/v2/users/' + this.authStatusService.email + '/pipelines/' + pipelineID)
            .pipe(
                map(response => {
                    return response;
                })
            );
    }

}
