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
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {AuthStatusService} from '../../services/auth-status.service';
import {AdapterDescriptionList} from '../model/connect/AdapterDescriptionList';
import {AdapterDescription} from '../model/connect/AdapterDescription';
import {GenericAdapterSetDescription} from '../model/connect/GenericAdapterSetDescription';
import {SpecificAdapterSetDescription} from '../model/connect/SpecificAdapterSetDescription';
import {GenericAdapterStreamDescription} from '../model/connect/GenericAdapterStreamDescription';
import {SpecificAdapterStreamDescription} from '../model/connect/SpecificAdapterStreamDescription';
import {ProtocolDescription} from '../model/connect/grounding/ProtocolDescription';
import {ConnectService} from '../connect.service';
import {RestApi} from "../../services/rest-api.service";
import {TsonLdSerializerService} from "../../platform-services/tsonld-serializer.service";
import {AlternativesStaticProperty} from "../model/AlternativesStaticProperty";
import {GroupStaticProperty} from "../model/GroupStaticProperty";
import {StaticProperty} from "../model/StaticProperty";
import {CollectionStaticProperty} from "../model/CollectionStaticProperty";

@Injectable()
export class DataMarketplaceService {
  private host = '/streampipes-connect/';

  constructor(
    private http: HttpClient,
    private authStatusService: AuthStatusService,
    private connectService: ConnectService,
    private restApi: RestApi,
    private tsonLdSerializer: TsonLdSerializerService
  ) {}


  getAdapterDescriptions(): Observable<AdapterDescription[]> {
      return this.requestAdapterDescriptions('/master/description/adapters');
  }

  getAdapters(): Observable<AdapterDescription[]> {
    return this.requestAdapterDescriptions('/master/adapters');
  }

  getAdapterTemplates(): Observable<AdapterDescription[]> {
    return this.requestAdapterDescriptions('/master/adapters/template/all');
  }

  requestAdapterDescriptions(path: string) : Observable<AdapterDescription[]> {
    return this.http
      .get(
        this.host +
          'api/v1/' +
          this.authStatusService.email +
          path
      )
      .pipe(map(response => {
        if(response['@graph'] === undefined) return [];
        const res: AdapterDescriptionList = this.tsonLdSerializer.fromJsonLd(
          response,
          'sp:AdapterDescriptionList'
        );
        res.list.forEach(adapterDescription => {
          adapterDescription.config.sort((a, b) => a.index - b.index);
          adapterDescription.config.forEach(sp => {
            this.sortStaticProperties(sp)
          });
        });
        return res.list;
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
        adapter.couchDbId
    );
  }

  getProtocols(): Observable<ProtocolDescription[]> {
    return this.http
      .get(
        this.host +
          'api/v1/' +
          this.authStatusService.email +
          '/master/description/protocols'
      )
      .pipe(map(response => {
        const res = this.tsonLdSerializer.fromJsonLd(
          response,
          'sp:ProtocolDescriptionList'
        );
        res.list.forEach(protocolDescription => {
          protocolDescription.config.sort((a, b) => a.index - b.index);
          protocolDescription.config.forEach(sp => {
            this.sortStaticProperties(sp)
          });
        });
        return res.list;
      }));
  }

  sortStaticProperties(sp: StaticProperty) {
    if (sp instanceof AlternativesStaticProperty) {
      sp.alternatives.sort((a, b) => a.index - b.index);
      sp.alternatives.forEach(a => {
        if (a.staticProperty instanceof GroupStaticProperty) {
          a.staticProperty.staticProperties.sort((a, b) => a.index - b.index);
        } else if (a.staticProperty instanceof CollectionStaticProperty) {
          this.sortStaticProperties((<CollectionStaticProperty> a.staticProperty).staticPropertyTemplate)
        }
      })
    } else if (sp instanceof GroupStaticProperty) {
        sp.staticProperties.sort((a, b) => a.index - b.index);
    } else if (sp instanceof CollectionStaticProperty) {
        this.sortStaticProperties((<CollectionStaticProperty> sp).staticPropertyTemplate)
    }
  }

  getGenericAndSpecifigAdapterDescriptions(): Observable<
    Observable<AdapterDescription[]>
  > {
    return this.getAdapterDescriptions().pipe(map(adapterDescriptions => {
      adapterDescriptions = adapterDescriptions.filter(
        this.connectService.isSpecificDescription
      );

      return this.getProtocols().pipe(map(protocols => {
        for (let protocol of protocols) {
          let newAdapterDescription: AdapterDescription;
          if (protocol.id.includes('sp:protocol/set') || protocol.sourceType === 'SET') {
            newAdapterDescription = new GenericAdapterSetDescription(
              'http://streampipes.org/genericadaptersetdescription'
            );
          } else if (protocol.id.includes('sp:protocol/stream') || protocol.sourceType === 'STREAM') {
            newAdapterDescription = new GenericAdapterStreamDescription(
              'http://streampipes.org/genericadapterstreamdescription'
            );
          }
          newAdapterDescription.appId = protocol.appId;
          newAdapterDescription.label = protocol.label;
          newAdapterDescription.description = protocol.description;
          newAdapterDescription.iconUrl = protocol.iconUrl;
          newAdapterDescription.uri = newAdapterDescription.id;
          newAdapterDescription.category = protocol.category;
          newAdapterDescription.includedAssets = protocol.includedAssets;
          newAdapterDescription.includesAssets = protocol.includesAssets;
          newAdapterDescription.includedLocales = protocol.includedLocales;
          newAdapterDescription.includesLocales = protocol.includesLocales;

          if (
            newAdapterDescription instanceof GenericAdapterSetDescription ||
            newAdapterDescription instanceof GenericAdapterStreamDescription
          ) {
            newAdapterDescription.protocol = protocol;
          }
          adapterDescriptions.push(newAdapterDescription);
        }
        return adapterDescriptions;
      }));
    }));
  }

  cloneAdapterDescription(toClone: AdapterDescription): AdapterDescription {
    var result: AdapterDescription;

      if (this.connectService.isGenericDescription(toClone)) {
          if (this.connectService.isDataStreamDescription(toClone)) {
              result = Object.assign(new GenericAdapterStreamDescription(toClone.id), toClone);
          } else {
              result = Object.assign(new GenericAdapterSetDescription(toClone.id), toClone);
          }
      } else {
          if (this.connectService.isDataStreamDescription(toClone)) {
              result = Object.assign(new SpecificAdapterStreamDescription(toClone.id), toClone);
          } else {
              result = Object.assign(new SpecificAdapterSetDescription(toClone.id), toClone);
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
