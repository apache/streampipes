import { Injectable } from '@angular/core';

import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Observable, Subscribable } from 'rxjs/Observable';

import { TsonLd } from './tsonld';


import 'rxjs/add/operator/map';
import 'rxjs/add/observable/fromPromise';

import { ProtocolDescriptionList } from './model/connect/grounding/ProtocolDescriptionList';
import { AdapterDescription } from './model/connect/AdapterDescription';
import { AdapterSetDescription } from './model/connect/AdapterSetDescription';
import { AdapterStreamDescription } from './model/connect/AdapterStreamDescription';
import { ProtocolDescription } from './model/connect/grounding/ProtocolDescription';
import { FormatDescriptionList } from './model/connect/grounding/FormatDescriptionList';
import { FormatDescription } from './model/connect/grounding/FormatDescription';
import { FreeTextStaticProperty } from './model/FreeTextStaticProperty';
import { EventSchema } from './schema-editor/model/EventSchema';
import { EventProperty } from './schema-editor/model/EventProperty';
import { EventPropertyNested } from './schema-editor/model/EventPropertyNested';
import { EventPropertyPrimitive } from './schema-editor/model/EventPropertyPrimitive';
import { EventPropertyList } from './schema-editor/model/EventPropertyList';
import { AdapterDescriptionList } from './model/connect/AdapterDescriptionList';
import { DataSetDescription } from './model/DataSetDescription';
import { DomainPropertyProbability } from './schema-editor/model/DomainPropertyProbability';
import { GuessSchema } from './schema-editor/model/GuessSchema';
import { DomainPropertyProbabilityList } from './schema-editor/model/DomainPropertyProbabilityList';
import { URI } from './model/URI';
import { AuthStatusService } from '../services/auth-status.service';
import { RenameRuleDescription } from './model/connect/rules/RenameRuleDescription';
import { DeleteRuleDescription } from './model/connect/rules/DeleteRuleDescription';
import { AddNestedRuleDescription } from './model/connect/rules/AddNestedRuleDescription';
import { MoveRuleDescription } from './model/connect/rules/MoveRuleDesctiption';
import { TransformationRuleDescription } from './model/connect/rules/TransformationRuleDescription';

@Injectable()
export class RestService {
    private host = '/streampipes-backend/';


    private getTsonLd(): any {

        const tsonld = new TsonLd();
        tsonld.addClassMapping(ProtocolDescription);
        tsonld.addClassMapping(ProtocolDescriptionList);
        tsonld.addClassMapping(FreeTextStaticProperty);
        tsonld.addClassMapping(FormatDescriptionList);
        tsonld.addClassMapping(FormatDescription);
        tsonld.addClassMapping(AdapterDescriptionList);
        tsonld.addClassMapping(AdapterDescription);
        tsonld.addClassMapping(AdapterSetDescription);
        tsonld.addClassMapping(AdapterStreamDescription);
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
        tsonld.addClassMapping(RenameRuleDescription);
        tsonld.addClassMapping(DeleteRuleDescription);
        tsonld.addClassMapping(AddNestedRuleDescription);
        tsonld.addClassMapping(MoveRuleDescription);

        return tsonld;
    }

    constructor(private http: HttpClient, private authStatusService: AuthStatusService) {
    }

    addAdapter(adapter: AdapterDescription) {
        const tsonld = new TsonLd();
        tsonld.addContext('sp', 'https://streampipes.org/vocabulary/v1/');
        tsonld.addContext('spi', 'urn:streampipes.org:spi:');
        tsonld.addContext('foaf', 'http://xmlns.com/foaf/0.1/');

        adapter.userName = this.authStatusService.email;

        tsonld.toflattenJsonLd(adapter).subscribe(res => {
            const httpOptions = {
                headers: new HttpHeaders({
                    'Content-Type': 'application/ld+json'
                })
            };
            console.log(JSON.stringify(res));
            this.http.post('/streampipes-connect/api/v1/a@a.de/master/adapters', res, httpOptions).subscribe();
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
            // .delete(this.host + 'api/v2/adapter/' + adapter.couchDbId);
            .delete('/streampipes-connect/api/v1/a@a.de/master/adapters/' + adapter.couchDbId);
    }


    getGuessSchema(adapter: AdapterDescription): Observable<GuessSchema> {
        const self = this;
        const tsonld = new TsonLd();
        tsonld.addContext('sp', 'https://streampipes.org/vocabulary/v1/');
        tsonld.addContext('spi', 'urn:streampipes.org:spi:');
        tsonld.addContext('foaf', 'http://xmlns.com/foaf/0.1/');

        console.log(adapter.constructor.name);

        return Observable.fromPromise(new Promise(function (resolve, reject) {
            tsonld.toflattenJsonLd(adapter).subscribe(res => {
                return self.http
                    .post('/streampipes-connect/api/v1/a@a.de/master/guess/schema', res)
                    .map(response => {

                        tsonld.addClassMapping(EventSchema);
                        tsonld.addClassMapping(EventProperty);
                        tsonld.addClassMapping(EventPropertyPrimitive);
                        tsonld.addClassMapping(EventPropertyList);
                        tsonld.addClassMapping(EventPropertyNested);
                        tsonld.addClassMapping(GuessSchema);
                        tsonld.addClassMapping(DomainPropertyProbability);
                        tsonld.addClassMapping(DomainPropertyProbabilityList);
                        tsonld.addClassMapping(RenameRuleDescription);
                        tsonld.addClassMapping(DeleteRuleDescription);
                        tsonld.addClassMapping(AddNestedRuleDescription);
                        tsonld.addClassMapping(MoveRuleDescription);
                        tsonld.addClassMapping(TransformationRuleDescription);

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
            .get('/streampipes-connect/api/v1/riemer@fzi.de/master/description/formats')
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
