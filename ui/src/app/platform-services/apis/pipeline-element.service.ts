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

import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {
  DataProcessorInvocation,
  DataSinkInvocation,
  DataSourceDescription
} from "../../core-model/gen/streampipes-model";
import {PlatformServicesCommons} from "./commons.service";
import {map} from "rxjs/operators";

@Injectable()
export class PipelineElementService {

  constructor(private http: HttpClient,
              private platformServicesCommons: PlatformServicesCommons) {
  }

  getDataProcessors(): Observable<Array<DataProcessorInvocation>> {
    return this.http.get(this.dataProcessorsUrl + "/own").pipe(map(data => {
      return (data as []).map(dpi => DataProcessorInvocation.fromData(dpi));
    }));
  }

  getDataSinks(): Observable<Array<DataSinkInvocation>> {
    return this.http.get(this.dataSinksUrl + "/own").pipe(map(data => {
      return (data as []).map(dpi => DataSinkInvocation.fromData(dpi));
    }));
  }

  getDataSources(): Observable<Array<DataSourceDescription>> {
    return this.http.get(this.dataSourcesUrl + "/own").pipe(map(data => {
      return (data as []).map(dpi => DataSourceDescription.fromData(dpi));
    }));
  }

  getDocumentation(appId) {
    return this.http.get(this.platformServicesCommons.unauthenticatedBasePath
        + "/pe/"
        + appId
        + "/assets/documentation", {responseType: 'text'});
  }

  private get dataProcessorsUrl(): string {
    return this.platformServicesCommons.authUserBasePath() + '/sepas'
  }

  private get dataSourcesUrl(): string {
    return this.platformServicesCommons.authUserBasePath() + '/sources'
  }

  private get dataSinksUrl(): string {
    return this.platformServicesCommons.authUserBasePath() + '/actions'
  }

}