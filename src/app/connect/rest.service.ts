import { Injectable } from '@angular/core';

import {HttpClient} from '@angular/common/http';

import {Observable, Subscribable} from 'rxjs/Observable';

import { TsonLd } from './tsonld';


import 'rxjs/add/operator/map';
import 'rxjs/add/observable/fromPromise';

import {ProtocolDescriptionList} from './model/ProtocolDescriptionList';
import {AdapterDescription} from './model/AdapterDescription';
import {ProtocolDescription} from './model/ProtocolDescription';
import {FormatDescriptionList} from './model/FormatDescriptionList';
import {FormatDescription} from './model/FormatDescription';
import {FreeTextStaticProperty} from './model/FreeTextStaticProperty';
import {EventSchema} from './schema-editor/model/EventSchema';
import {EventProperty} from './schema-editor/model/EventProperty';
import {EventPropertyNested} from './schema-editor/model/EventPropertyNested';
import {EventPropertyPrimitive} from './schema-editor/model/EventPropertyPrimitive';
import {EventPropertyList} from './schema-editor/model/EventPropertyList';
import {AdapterDescriptionList} from './model/AdapterDescriptionList';
import {DataSetDescription} from './model/DataSetDescription';
import {DomainPropertyProbability} from './schema-editor/model/DomainPropertyProbability';
import {GuessSchema} from './schema-editor/model/GuessSchema';
import {DomainPropertyProbabilityList} from './schema-editor/model/DomainPropertyProbabilityList';
import {URI} from './model/URI';

@Injectable()
export class RestService {
    private host = 'http://localhost:8082/streampipes-backend/';


    private getTsonLd(): any {

        const tsonld = new TsonLd();
        tsonld.addClassMapping(ProtocolDescription);
        tsonld.addClassMapping(ProtocolDescriptionList);
        tsonld.addClassMapping(FreeTextStaticProperty);
        tsonld.addClassMapping(FormatDescriptionList);
        tsonld.addClassMapping(FormatDescription);
        tsonld.addClassMapping(AdapterDescriptionList);
        tsonld.addClassMapping(AdapterDescription);
        tsonld.addClassMapping(DataSetDescription);
        tsonld.addClassMapping(EventSchema);
        tsonld.addClassMapping(EventProperty);
        tsonld.addClassMapping(EventPropertyNested);
        tsonld.addClassMapping(EventPropertyList);
        tsonld.addClassMapping(EventPropertyPrimitive);
        tsonld.addClassMapping(DomainPropertyProbability);
        tsonld.addClassMapping(DomainPropertyProbabilityList);
        tsonld.addClassMapping(GuessSchema);
        tsonld.addClassMapping(URI);

        return tsonld;
    }

    constructor( private http: HttpClient) {
    }

    addAdapter(adapter: AdapterDescription ) {
        const tsonld = new TsonLd();
        tsonld.addContext('sp', 'https://streampipes.org/vocabulary/v1/');
        tsonld.addContext('spi', 'urn:streampipes.org:spi:');
        tsonld.addContext('foaf', 'http://xmlns.com/foaf/0.1/');


        tsonld.toflattenJsonLd(adapter).subscribe(res => {
            console.log(JSON.stringify(res));
            this.http.post(this.host + 'api/v2/adapter', res).subscribe();
        });

    }

    getAdapters(): Observable<AdapterDescription[]> {
        return this.http
            .get(this.host + 'api/v2/adapter/allrunning')
            .map(response => {


                // TODO remove this
                // quick fix to deserialize URIs
                response['@graph'].forEach(function (object) {
                   if (object['sp:domainProperty'] != undefined) {
                       // object['sp:domainProperty']['@type'] = "sp:URI";
                       object['sp:domainProperty'] = object['sp:domainProperty']['@id'];
                       delete object['sp:domainProperty']['@id'];
                   }
                })
                const tsonld = this.getTsonLd();

                // console.log(JSON.stringify(response, null, 2));
                const res = tsonld.fromJsonLdType(response, 'sp:AdapterDescriptionList');
                // console.log(JSON.stringify(res, null, 2));

                return res.list;
            });
    }

    deleteAdapter(adapter: AdapterDescription): Observable<any> {
         return this.http
            .delete(this.host + 'api/v2/adapter/' + adapter.couchDbId);
    }


    getGuessSchema(adapter: AdapterDescription): Observable<GuessSchema> {
        const self = this;
        const tsonld = new TsonLd();
        tsonld.addContext('sp', 'https://streampipes.org/vocabulary/v1/');
        tsonld.addContext('spi', 'urn:streampipes.org:spi:');
        tsonld.addContext('foaf', 'http://xmlns.com/foaf/0.1/');

        return Observable.fromPromise(new Promise(function(resolve, reject) {
            tsonld.toflattenJsonLd(adapter).subscribe(res => {
                return self.http
                    .post(self.host + 'api/v2/guess/schema', res)
                    .map(response => {

                        tsonld.addClassMapping(EventSchema);
                        tsonld.addClassMapping(EventProperty);
                        tsonld.addClassMapping(EventPropertyPrimitive);
                        tsonld.addClassMapping(EventPropertyList);
                        tsonld.addClassMapping(EventPropertyNested);
                        tsonld.addClassMapping(GuessSchema);
                        tsonld.addClassMapping(DomainPropertyProbability);
                        tsonld.addClassMapping(DomainPropertyProbabilityList);

                        const r = tsonld.fromJsonLdType(response, 'sp:GuessSchema');
                        resolve(r);
                    }).subscribe();
            });
        }));

    }


    getProtocols(): Observable<ProtocolDescriptionList> {

        return this.http
            .get(this.host + 'api/v2/adapter/allProtocols')
            .map(response => {

                const tsonld = new TsonLd();
                tsonld.addClassMapping(ProtocolDescriptionList);
                tsonld.addClassMapping(FreeTextStaticProperty);
                tsonld.addClassMapping(ProtocolDescription);

                // console.log(JSON.stringify(jsonResponse, null, 2));
                const res = tsonld.fromJsonLdType(response, 'sp:ProtocolDescriptionList');
                // console.log(JSON.stringify(res, null, 2));

                return res;
            });
    }

    getFormats(): Observable<FormatDescriptionList> {

        return this.http
            .get(this.host + 'api/v2/adapter/allFormats')
            .map(response => {

                const tsonld = new TsonLd();
                tsonld.addClassMapping(FreeTextStaticProperty);
                tsonld.addClassMapping(FormatDescription);
                tsonld.addClassMapping(FormatDescriptionList);

                // console.log(JSON.stringify(jsonResponse, null, 2));
                const res = tsonld.fromJsonLdType(response, 'sp:FormatDescriptionList');
                // console.log(JSON.stringify(res, null, 2));

                return res;
            });
    }

}
