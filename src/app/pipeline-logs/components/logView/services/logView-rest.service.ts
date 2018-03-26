import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class LogViewRestService {

    constructor(private http: HttpClient) {
    }

}
