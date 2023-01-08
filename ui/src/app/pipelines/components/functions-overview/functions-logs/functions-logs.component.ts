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

import { Component, OnInit } from '@angular/core';
import { AbstractFunctionDetailsDirective } from '../abstract-function-details.directive';
import { SpLogEntry } from '@streampipes/platform-services';

@Component({
    selector: 'sp-functions-logs',
    templateUrl: './functions-logs.component.html',
    styleUrls: [],
})
export class SpFunctionsLogsComponent
    extends AbstractFunctionDetailsDirective
    implements OnInit
{
    logs: SpLogEntry[] = [];

    ngOnInit(): void {
        super.onInit();
    }

    afterFunctionLoaded(): void {
        this.loadLogs();
    }

    loadLogs(): void {
        this.functionsService
            .getFunctionLogs(this.activeFunction.functionId.id)
            .subscribe(logs => {
                this.logs = logs;
                this.contentReady = true;
            });
    }

    getBreadcrumbLabel(): string {
        return 'Logs';
    }
}
