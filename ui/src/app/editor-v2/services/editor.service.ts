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
import {AuthStatusService} from "../../services/auth-status.service";
import {TsonLdSerializerService} from "../../platform-services/tsonld-serializer.service";
import {
    DataProcessorInvocation, PipelineElementRecommendationMessage,
    PipelineModificationMessage
} from "../../core-model/gen/streampipes-model";
import {Observable} from "rxjs";
import {PlatformServicesCommons} from "../../platform-services/apis/commons.service";

@Injectable()
export class EditorService {

    constructor(private http: HttpClient,
                private platformServicesCommons: PlatformServicesCommons,
                private authStatusService: AuthStatusService) {
    }

    recommendPipelineElement(pipeline): Observable<PipelineElementRecommendationMessage> {
        return this.http.post(this.pipelinesResourceUrl +"/recommend", pipeline)
            .map(data => PipelineElementRecommendationMessage.fromData(data as any));
    }

    updatePartialPipeline(pipeline): Observable<PipelineModificationMessage> {
        return this.http.post(this.pipelinesResourceUrl +"/update", pipeline)
            .map(data => {
                return PipelineModificationMessage.fromData(data as any);
            });
    }

    getCachedPipeline() {
        return this.http.get(this.platformServicesCommons.authUserBasePath() + "/pipeline-cache");
    }

    updateCachedPipeline(rawPipelineModel: any) {
        return this.http.post(this.platformServicesCommons.authUserBasePath() + "/pipeline-cache", rawPipelineModel);
    }

    removePipelineFromCache() {
        return this.http.delete(this.platformServicesCommons.authUserBasePath() + "/pipeline-cache");
    }

    private get pipelinesResourceUrl() {
        return this.platformServicesCommons.authUserBasePath() + '/pipelines'
    }



}