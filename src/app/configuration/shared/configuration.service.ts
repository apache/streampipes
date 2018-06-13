import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { map } from 'rxjs/operators';

import { StreampipesPeContainer } from './streampipes-pe-container.model';

@Injectable()
export class ConfigurationService {

    
    constructor(private http: HttpClient) {
    }

    getServerUrl() {
        return '/streampipes-backend';
    }

    getConsulServices(): Observable<StreampipesPeContainer[]> {
        return this.http.get(this.getServerUrl() + '/api/v2/consul')
            .pipe(
                map(response => {
                    for (let service of response as any[]) {
                        for (let config of service['configs']) {
                            if (config.valueType === 'xs:integer') {
                                config.value = parseInt(config.value);
                            } else if (config.valueType === 'xs:double') {
                                config.value = parseFloat('xs:double');
                            } else if (config.valueType === 'xs:boolean') {
                                config.value = (config.value === 'true');
                            }
                        }
                    }
                    return response as StreampipesPeContainer[];
                })
            );
    }

    updateConsulService(consulService: StreampipesPeContainer): Observable<Object> {
        return this.http.post(this.getServerUrl() + '/api/v2/consul', consulService);
    }


    adjustConfigurationKey(consulKey) {         
            var str1 = consulKey.replace(/SP/g,"");
            str1 = str1.replace(/_/g," "); 
            if(str1.startsWith(" ")){
                str1 = str1.slice(1,str1.length)
            }
            return str1 
    }
}