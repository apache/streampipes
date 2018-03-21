import { Injectable } from '@angular/core';

import {HttpClient} from '@angular/common/http';

import {Protocol} from './protocol-form/protocol';
import {Format} from './format-form/format';
import {Observable} from 'rxjs/Observable';

import { TsonLd } from './tsonld';


import 'rxjs/add/operator/map';
import 'rxjs/add/observable/fromPromise';

import {ProtocolDescriptionList} from './model/ProtocolDescriptionList';
import {AdapterDescription} from './model/AdapterDescription';
import {StaticProperty} from './model/StaticProperty';
import {ProtocolDescription} from './model/ProtocolDescription';
import {FormatDescriptionList} from './model/FormatDescriptionList';
import {FormatDescription} from './model/FormatDescription';
import {FreeTextStaticProperty} from './model/FreeTextStaticProperty';
import {EventSchema} from './schema-editor/model/EventSchema';
import {EventProperty} from './schema-editor/model/EventProperty';
import {EventPropertyNested} from './schema-editor/model/EventPropertyNested';
import {EventPropertyPrimitive} from './schema-editor/model/EventPropertyPrimitive';
import {EventPropertyList} from './schema-editor/model/EventPropertyList';

@Injectable()
export class RestService {
  // private obj: { protocol: Protocol; format: Format };
  // private http: HttpClient;
  constructor( private http: HttpClient) {
    // constructor( ) {
  }

  addAdapter(adapter: AdapterDescription ) {
    const tsonld = new TsonLd();
    tsonld.addContext('sp', 'https://streampipes.org/vocabulary/v1/');
    tsonld.addContext('spi', 'urn:streampipes.org:spi:');
    tsonld.addContext('foaf', 'http://xmlns.com/foaf/0.1/');

    tsonld.toflattenJsonLd(adapter).subscribe(res => {
      console.log(JSON.stringify(res));
      this.http.post('http://localhost:4200/api/v1/adapter', res).subscribe();
    });

  }

  getAdapters(): Observable<{ protocol: Protocol; format: Format}[]> {
    return this.http
      .get('http://localhost:4200/api/v1/adapter/all')
      .map(response => response as { protocol: Protocol; format: Format}[]);

  }


  getGuessSchema(adapter: AdapterDescription): Observable<EventSchema> {
    const self = this;
        const tsonld = new TsonLd();
    tsonld.addContext('sp', 'https://streampipes.org/vocabulary/v1/');
    tsonld.addContext('spi', 'urn:streampipes.org:spi:');
    tsonld.addContext('foaf', 'http://xmlns.com/foaf/0.1/');

    return Observable.fromPromise(new Promise(function(resolve, reject) {
      tsonld.toflattenJsonLd(adapter).subscribe(res => {
        return self.http
          .post('http://localhost:4200/api/v1/guess/schema', res)
          .map(response => {

            tsonld.addClassMapping(EventSchema);
            tsonld.addClassMapping(EventProperty);
            tsonld.addClassMapping(EventPropertyPrimitive);
            tsonld.addClassMapping(EventPropertyList);
            tsonld.addClassMapping(EventPropertyNested);

            const r = tsonld.fromJsonLdType(response, 'sp:EventSchema');
            resolve(r);
          }).subscribe();
      });
    }));

  }


  getProtocols(): Observable<ProtocolDescriptionList> {

    return this.http
      .get('http://localhost:8082/streampipes-backend/api/v2/adapter/allProtocols')
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
      .get('http://localhost:8082/streampipes-backend/api/v2/adapter/allFormats')
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

  // compactAsyncObservableProtocol(graph, context, type): Observable<ProtocolDescription[]> {
  //   compactAsyncObservableProtocol(data, type): Observable<ProtocolDescription[]> {
  //   const self = this;
  //   return Observable.fromPromise(new Promise(function(resolve, reject){
  //     // (jsonld as any).compact(graph, context, function (err, data) {
  //
  //       const  all: ProtocolDescription[] = self.transformProtocol(data['@graph'], type);
  //
  //       resolve(Array.from(all));
  //     // });
  //   }));
  // }


   // compactAsyncObservableFormat(graph, context, type): Observable<FormatDescription[]> {
  //  compactAsyncObservableFormat(data, type): Observable<ProtocolDescription[]> {
  //   const self = this;
  //   return Observable.fromPromise(new Promise(function(resolve, reject){
  //     // (jsonld as any).compact(graph, context, function (err, data) {
  //
  //       const  all: ProtocolDescription[] = self.transformProtocol(data['@graph'], type);
  //
  //       resolve(Array.from(all));
  //     // });
  //   }));
  // }

}
