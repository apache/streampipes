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

import { Component, Input, OnInit } from '@angular/core';
import { FunctionId } from '@streampipes/platform-services';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';

@Component({
    selector: 'sp-functions-overview',
    templateUrl: './functions-overview.component.html',
    styleUrls: ['./functions-overview.component.scss'],
})
export class FunctionsOverviewComponent implements OnInit {
    @Input()
    functions: FunctionId[] = [];

    dataSource: MatTableDataSource<FunctionId>;

    displayedColumns: string[] = ['name', 'action'];

    constructor(private router: Router) {}

    ngOnInit(): void {
        this.dataSource = new MatTableDataSource<FunctionId>(this.functions);
    }

    showFunctionDetails(functionId: string): void {
        this.router.navigate(['pipelines', 'functions', functionId, 'metrics']);
    }
}
