import { Injectable } from '@angular/core';

import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Observable, Subscribable } from 'rxjs/Observable';

import 'rxjs/add/operator/map';
import 'rxjs/add/observable/fromPromise';

import { ProtocolDescriptionList } from './model/connect/grounding/ProtocolDescriptionList';
import { AdapterDescription } from './model/connect/AdapterDescription';
import { FormatDescriptionList } from './model/connect/grounding/FormatDescriptionList';
import { EventProperty } from './schema-editor/model/EventProperty';
import { EventPropertyNested } from './schema-editor/model/EventPropertyNested';
import { GuessSchema } from './schema-editor/model/GuessSchema';
import { AuthStatusService } from '../services/auth-status.service';
import {StatusMessage} from "./model/message/StatusMessage";
import { UnitDescription } from './model/UnitDescription';
import {TsonLdSerializerService} from './tsonld-serializer.service';

@Injectable()
export class RestService {
    private host = '/streampipes-backend/';

    constructor(
        private http: HttpClient,
        private authStatusService: AuthStatusService,
        private tsonLdSerializerService: TsonLdSerializerService,
    ) {}

    addAdapter(adapter: AdapterDescription): Observable<StatusMessage> {
        return this.addAdapterDescription(adapter, '/master/adapters');
    }

    addAdapterTemplate(adapter: AdapterDescription): Observable<StatusMessage> {
        return this.addAdapterDescription(adapter, '/master/adapters/template');
    }

    addAdapterDescription(adapter: AdapterDescription, url: String): Observable<StatusMessage> {
        adapter.userName = this.authStatusService.email;
        var self = this;


        return Observable.fromPromise(
            new Promise(function(resolve, reject) {
                self.tsonLdSerializerService.toJsonLd(adapter).subscribe(res => {
                    const httpOptions = {
                        headers: new HttpHeaders({
                            'Content-Type': 'application/ld+json',
                        }),
                    };
                    self.http
                        .post(
                            '/streampipes-connect/api/v1/' + self.authStatusService.email + url,
                            res,
                            httpOptions
                        )
                        .map(response => {
                            var statusMessage = response as StatusMessage;
                            resolve(statusMessage);
                        })
                        .subscribe();
                });
            })
        );
    }


    getGuessSchema(adapter: AdapterDescription): Observable<GuessSchema> {
        const self = this;

        return Observable.fromPromise(
            new Promise(function(resolve, reject) {
                self.tsonLdSerializerService.toJsonLd(adapter).subscribe(res => {
                    return self.http
                        .post('/streampipes-connect/api/v1/' + self.authStatusService.email + '/master/guess/schema', res)
                        .map(response => {
                            const r = self.tsonLdSerializerService.fromJsonLd(response, 'sp:GuessSchema');

                            self.removeHeaderKeys(r.eventSchema.eventProperties);

                            resolve(r);
                        })
                        .subscribe(
                            data =>  data,
                            error => reject(error),
                        );
                });
            })
        );
    }

    removeHeaderKeys(eventProperties: EventProperty[]) {
        // remove header key form schema
        for (let ep of eventProperties) {
            if (ep.getRuntimeName() == "header") {
                ep.setRuntimeName("header_1");
            }

            if (ep instanceof EventPropertyNested) {
                this.removeHeaderKeys((<EventPropertyNested> ep).eventProperties);
            }
        }

    }

    getSourceDetails(sourceElementId): Observable<any> {
        return this.http
            .get(this.makeUserDependentBaseUrl() +"/sources/" +encodeURIComponent(sourceElementId));
    }

    getRuntimeInfo(sourceDescription): Observable<any> {
        return this.http.post(this.makeUserDependentBaseUrl() +"/pipeline-element/runtime", sourceDescription);
    }

    makeUserDependentBaseUrl() {
        return this.host  +'api/v2/users/' + this.authStatusService.email;
    }


    getFormats(): Observable<FormatDescriptionList> {
        var self = this;
        return this.http
            .get(
                '/streampipes-connect/api/v1/riemer@fzi.de/master/description/formats'
            )
            .map(response => {
                const res = self.tsonLdSerializerService.fromJsonLd(response, 'sp:FormatDescriptionList');
                return res;
            });
    }

    getProtocols(): Observable<ProtocolDescriptionList> {
        var self = this;
        return this.http
            .get(this.host + 'api/v2/adapter/allProtocols')
            .map(response => {
               const res = this.tsonLdSerializerService.fromJsonLd(
                    response,
                    'sp:ProtocolDescriptionList'
                );
                return res;
            });
    }

    getFittingUnits(unitDescription: UnitDescription): Observable<UnitDescription[]> {
        return this.http
            .post<UnitDescription[]>('/streampipes-connect/api/v1/' + this.authStatusService.email + '/master/unit', unitDescription)
            .map(response => {
                const descriptions = response as UnitDescription[];
                return descriptions.filter(entry => entry.resource != unitDescription.resource)
            });
    }


}
