/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {map} from "rxjs/operators";
import {from, Observable} from "rxjs";
import {AuthStatusService} from "../../services/auth-status.service";
import {ConfiguredWidget, Dashboard} from "../models/dashboard.model";
import {EventSchema} from "../../connect/schema-editor/model/EventSchema";
import {EventProperty} from "../../connect/schema-editor/model/EventProperty";
import {EventPropertyPrimitive} from "../../connect/schema-editor/model/EventPropertyPrimitive";
import {TsonLdSerializerService} from "../../platform-services/tsonld-serializer.service";
import {StatusMessage} from "../../connect/model/message/StatusMessage";
import {RuntimeOptionsResponse} from "../../connect/model/connect/runtime/RuntimeOptionsResponse";
import {DashboardWidget} from "../../core-model/dashboard/DashboardWidget";
import {VisualizablePipeline} from "../../core-model/dashboard/VisualizablePipeline";

@Injectable()
export class DashboardService {


    constructor(private http: HttpClient, 
                private authStatusService: AuthStatusService,
                private tsonLdSerializerService: TsonLdSerializerService,) {
    }

    getVisualizablePipelines(): Observable<Array<VisualizablePipeline>> {
        return this.http
            .get(this.visualizablePipelineUrl)
            .map(data => {
                return this.tsonLdSerializerService.fromJsonLdContainer(data, 'sp:VisualizablePipeline')
            });
    }

    deserializeSchema(schema: any): EventSchema {
        let eventSchema: EventSchema = new EventSchema();
        let eventProperties = new Array<EventProperty>();

        schema.eventProperties.forEach(ep => {
           eventProperties.push(this.makeProperty(ep));
        });

        eventSchema.eventProperties = eventProperties;
        return eventSchema;
    }

    makeProperty(ep: any): EventProperty {
        // TODO find a better way to deserialize the schema
        if (ep.type === "org.apache.streampipes.model.schema.EventPropertyPrimitive") {
            return Object.assign(new EventPropertyPrimitive(), ep.properties);
        }
    }

    getPipeline(pipelineId): Observable<any> {
        return this.http.get('/pipeline/' + pipelineId);
    }

    private get baseUrl() {
        return '/streampipes-backend';
    }

    private get dashboardUrl() {
        return this.baseUrl + '/api/v2/users/' + this.authStatusService.email + '/ld/dashboards'
    }

    private get dashboardWidgetUrl() {
        return this.baseUrl + '/api/v2/users/' + this.authStatusService.email + '/ld/widgets'
    }

    private get visualizablePipelineUrl() {
        return this.baseUrl + '/api/v2/users/' + this.authStatusService.email + '/ld/pipelines'
    }

    saveWidget(widget: DashboardWidget): Observable<StatusMessage> {
        return this.serializeAndPost(this.dashboardWidgetUrl, widget);
    }

    saveDashboard(dashboard: Dashboard) {
        this.http.post('/dashboard', dashboard);
    }

    serializeAndPost(url: string, object: DashboardWidget): Observable<StatusMessage> {
        let promise = new Promise<StatusMessage>((resolve, reject) => {
            this.tsonLdSerializerService.toJsonLd(object).subscribe(serialized => {
                console.log(serialized);
                const httpOptions = {
                    headers: new HttpHeaders({
                        'Content-Type': 'application/ld+json',
                    }),
                };
                this.http.post(url, serialized, httpOptions).pipe(map(response => {
                    console.log(response);
                    resolve(response as StatusMessage);
                })).subscribe();
            });
        });
        return from(promise);
    }
}