import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class ConfigurationService {

    constructor(private http: HttpClient) {}

    getServerUrl() {
        return "/streampipes-backend";;
    }

    getConsulServices() {
        return this.http.get(this.getServerUrl() + '/api/v2/consul');
    }

    updateConsulService(configuration) {
        return this.http.post(this.getServerUrl() + '/api/v2/consul', configuration)
    }

}