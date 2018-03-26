import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class PipelineLogsRestService {

    constructor(private http: HttpClient) {
    }

}
