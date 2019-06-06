import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { StreampipesPeContainer } from './streampipes-pe-container.model';
import { MessagingSettings } from './messaging-settings.model';

@Injectable()
export class ConfigurationService {

    
    constructor(private http: HttpClient) {
    }

    getServerUrl() {
        return '/streampipes-backend';
    }

    getMessagingSettings(): Observable<MessagingSettings> {
        return this.http.get(this.getServerUrl() + '/api/v2/consul/messaging')
            .pipe(
                map(response => {
                    console.log(response);
                    return response as MessagingSettings;
                })
            )
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

    updateMessagingSettings(messagingSettings: MessagingSettings):Observable<Object> {
        return this.http.post(this.getServerUrl() + '/api/v2/consul/messaging', messagingSettings);
    }


    adjustConfigurationKey(consulKey) {

            var removedKey = consulKey.substr(consulKey.lastIndexOf("/") + 1, consulKey.length);

            // console.log(removedKey);

            var str1 = removedKey.replace(/SP/g,"");
            str1 = str1.replace(/_/g," "); 
            if(str1.startsWith(" ")){
                str1 = str1.slice(1,str1.length)
            }
            return str1 
    }
}