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

import { Component, OnInit, ViewChild } from '@angular/core';
import {
    animate,
    state,
    style,
    transition,
    trigger,
} from '@angular/animations';
import { ConfigurationService } from '../shared/configuration.service';
import { StreampipesPeContainer } from '../shared/streampipes-pe-container.model';
import { StreampipesPeContainerConifgs } from '../shared/streampipes-pe-container-configs';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { SpConfigurationTabs } from '../configuration-tabs';
import { SpBreadcrumbService } from '@streampipes/shared-ui';
import { SpConfigurationRoutes } from '../configuration.routes';

@Component({
    selector: 'sp-pipeline-element-configuration',
    templateUrl: './pipeline-element-configuration.component.html',
    styleUrls: ['./pipeline-element-configuration.component.css'],
    animations: [
        trigger('detailExpand', [
            state(
                'collapsed',
                style({ height: '0px', minHeight: '0', display: 'none' }),
            ),
            state('expanded', style({ height: '*' })),
            transition(
                'expanded <=> collapsed',
                animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)'),
            ),
        ]),
    ],
})
export class PipelineElementConfigurationComponent implements OnInit {
    tabs = SpConfigurationTabs.getTabs();

    consulServices: StreampipesPeContainer[];

    displayedColumns: string[] = ['status', 'name', 'action'];
    @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;
    dataSource = new MatTableDataSource<StreampipesPeContainer>();

    expandedElement: any;

    selectedConsulService: StreampipesPeContainer;
    consulServiceSelected = false;

    constructor(
        private configurationService: ConfigurationService,
        private breadcrumbService: SpBreadcrumbService,
    ) {
        this.getConsulServices();
    }

    ngOnInit() {
        this.breadcrumbService.updateBreadcrumb([
            SpConfigurationRoutes.BASE,
            { label: SpConfigurationTabs.getTabs()[5].itemTitle },
        ]);
    }

    getConsulServices(): void {
        this.configurationService.getConsulServices().subscribe(
            response => {
                const sortedServices = this.sort(response);
                this.consulServices = sortedServices;
                this.dataSource.data = sortedServices;
            },
            error => {
                console.error(error);
            },
        );
    }

    sort(consulServices: StreampipesPeContainer[]): StreampipesPeContainer[] {
        if (!consulServices || consulServices.length === 0) {
            return null;
        }

        consulServices.sort(
            (a: StreampipesPeContainer, b: StreampipesPeContainer) => {
                if (a.name < b.name) {
                    return -1;
                } else if (a.name > b.name) {
                    return 1;
                } else {
                    return 0;
                }
            },
        );
        consulServices.forEach(cs =>
            cs.configs.sort(
                (
                    a: StreampipesPeContainerConifgs,
                    b: StreampipesPeContainerConifgs,
                ) => {
                    if (a.key < b.key) {
                        return -1;
                    } else if (a.key > b.key) {
                        return 1;
                    } else {
                        return 0;
                    }
                },
            ),
        );
        return consulServices;
    }

    updateConsulService(consulService: StreampipesPeContainer): void {
        this.configurationService.updateConsulService(consulService).subscribe(
            response => {},
            error => {
                console.error(error);
            },
        );
    }

    expand(element: StreampipesPeContainer) {
        if (this.expandedElement === element) {
            this.expandedElement = undefined;
        } else {
            this.expandedElement = element;
        }
    }
}
