/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map} from 'rxjs/operators';
import {AuthStatusService} from '../../services/auth-status.service';
import {ConnectService} from '../connect.service';
import {
  AdapterDescription,
  AdapterDescriptionList,
  AdapterDescriptionUnion,
  EventSchema,
  GenericAdapterSetDescription,
  GenericAdapterStreamDescription,
  ProtocolDescription,
  ProtocolDescriptionList,
  SpDataSet,
  SpDataStream,
  SpecificAdapterSetDescription,
  SpecificAdapterStreamDescription
} from "../../core-model/gen/streampipes-model";
import {Observable, zip} from "rxjs";

@Injectable()
export class DataMarketplaceService {
  private host = '/streampipes-connect/';

  constructor(
      private http: HttpClient,
      private authStatusService: AuthStatusService,
      private connectService: ConnectService) {
  }


  getAdapterDescriptions(): Observable<AdapterDescriptionUnion[]> {
    return this.requestAdapterDescriptions('/master/description/adapters').pipe(map(response => {
      console.log(response);
      return (response as any[]).map(resp => AdapterDescription.fromDataUnion(resp)).filter(ad => this.connectService.isSpecificDescription(ad));
    }));
  }

  getAdapters(): Observable<AdapterDescriptionUnion[]> {
    return this.requestAdapterDescriptions('/master/adapters');
  }

  getAdapterTemplates(): Observable<AdapterDescriptionUnion[]> {
    return this.requestAdapterDescriptions('/master/adapters/template/all');
  }

  requestAdapterDescriptions(path: string): Observable<AdapterDescriptionUnion[]> {
    return this.http
        .get(
            this.host +
            'api/v1/' +
            this.authStatusService.email +
            path
        )
        .pipe(map(response => {
          let adapterDescriptionList: AdapterDescriptionList = AdapterDescriptionList.fromData(response as AdapterDescriptionList);
          return adapterDescriptionList.list;
          // const res: AdapterDescriptionList = this.tsonLdSerializer.fromJsonLd(
          //   response,
          //   'sp:AdapterDescriptionList'
          // );
          // res.list.forEach(adapterDescription => {
          //   adapterDescription.config.sort((a, b) => a.index - b.index);
          //   adapterDescription.config.forEach(sp => {
          //     this.sortStaticProperties(sp)
          //   });
          // });
          // return res.list;
        }));
  }

  deleteAdapter(adapter: AdapterDescription): Observable<Object> {
    return this.deleteRequest(adapter, '/master/adapters/')
  }

  deleteAdapterTemplate(adapter: AdapterDescription): Observable<Object> {
    return this.deleteRequest(adapter, '/master/adapters/template/')
  }

  getAdapterCategories(): Observable<Object> {
    return this.http.get(
        this.baseUrl +
        '/api/v2' +
        "/categories/adapter"
    )
  }

  private deleteRequest(adapter: AdapterDescription, url: String) {
    return this.http.delete(
        this.host +
        'api/v1/' +
        this.authStatusService.email +
        url +
        adapter.couchDBId
    );
  }

  getProtocols(): Observable<AdapterDescriptionUnion[]> {
    return this.http
        .get(
            this.host +
            'api/v1/' +
            this.authStatusService.email +
            '/master/description/protocols'
        )
        .pipe(map(response => {
          let adapterDescriptions: AdapterDescriptionUnion[] = [];
          let protocols: ProtocolDescription[] = (ProtocolDescriptionList.fromData(response as ProtocolDescriptionList)).list;

          for (let protocol of protocols) {
            let newAdapterDescription: AdapterDescriptionUnion;
            if (protocol.sourceType === 'SET') {
              newAdapterDescription = new GenericAdapterSetDescription();
              newAdapterDescription["@class"] = "org.apache.streampipes.model.connect.adapter.GenericAdapterSetDescription";
              newAdapterDescription.elementId = 'http://streampipes.org/genericadaptersetdescription'
              newAdapterDescription.dataSet = new SpDataSet();
              newAdapterDescription.dataSet.eventSchema = new EventSchema();
            } else if (protocol.sourceType === 'STREAM') {
              newAdapterDescription = new GenericAdapterStreamDescription();
              newAdapterDescription["@class"] = "org.apache.streampipes.model.connect.adapter.GenericAdapterStreamDescription";
              newAdapterDescription.elementId = 'http://streampipes.org/genericadapterstreamdescription'
              newAdapterDescription.dataStream = new SpDataStream();
              newAdapterDescription.dataStream.eventSchema = new EventSchema();
            }
            newAdapterDescription.appId = protocol.appId;
            newAdapterDescription.name = protocol.name;
            newAdapterDescription.description = protocol.description;
            newAdapterDescription.iconUrl = protocol.iconUrl;
            newAdapterDescription.uri = newAdapterDescription.elementId;
            newAdapterDescription.category = protocol.category;
            newAdapterDescription.includedAssets = protocol.includedAssets;
            newAdapterDescription.includesAssets = protocol.includesAssets;
            newAdapterDescription.includedLocales = protocol.includedLocales;
            newAdapterDescription.includesLocales = protocol.includesLocales;

            if (
                newAdapterDescription instanceof GenericAdapterSetDescription ||
                newAdapterDescription instanceof GenericAdapterStreamDescription
            ) {
              newAdapterDescription.protocolDescription = protocol;
            }
            adapterDescriptions.push(newAdapterDescription);
          }
          return adapterDescriptions;
        }));
  }

  // sortStaticProperties(sp: StaticProperty) {
  //   if (sp instanceof AlternativesStaticProperty) {
  //     sp.alternatives.sort((a, b) => a.index - b.index);
  //     sp.alternatives.forEach(a => {
  //       if (a.staticProperty instanceof GroupStaticProperty) {
  //         a.staticProperty.staticProperties.sort((a, b) => a.index - b.index);
  //       } else if (a.staticProperty instanceof CollectionStaticProperty) {
  //         this.sortStaticProperties((<CollectionStaticProperty> a.staticProperty).staticPropertyTemplate)
  //       }
  //     })
  //   } else if (sp instanceof GroupStaticProperty) {
  //       sp.staticProperties.sort((a, b) => a.index - b.index);
  //   } else if (sp instanceof CollectionStaticProperty) {
  //       this.sortStaticProperties((<CollectionStaticProperty> sp).staticPropertyTemplate)
  //   }
  // }

  getGenericAndSpecificAdapterDescriptions(): Observable<[AdapterDescriptionUnion[], AdapterDescriptionUnion[]]> {
  return zip(this.getAdapterDescriptions(), this.getProtocols());
  }

  cloneAdapterDescription(toClone: AdapterDescriptionUnion): AdapterDescriptionUnion {
    var result: AdapterDescriptionUnion;

    if (this.connectService.isGenericDescription(toClone)) {
      if (toClone instanceof GenericAdapterStreamDescription) {
        result = GenericAdapterStreamDescription.fromData(toClone, new GenericAdapterStreamDescription());
      } else if (toClone instanceof GenericAdapterSetDescription) {
        result = GenericAdapterSetDescription.fromData(toClone, new GenericAdapterSetDescription());
      }
    } else {
      if (toClone instanceof SpecificAdapterStreamDescription) {
        result = SpecificAdapterStreamDescription.fromData(toClone, new SpecificAdapterStreamDescription());
      } else if (toClone instanceof SpecificAdapterSetDescription) {
        result = SpecificAdapterSetDescription.fromData(toClone, new SpecificAdapterSetDescription());
      }
    }

    return result;
  }

  getAssetUrl(appId) {
    return this.host + 'api/v1/' + this.authStatusService.email + "/master/description/" + appId + "/assets"
  }

  private get baseUrl() {
    return '/streampipes-backend';
  }
}
